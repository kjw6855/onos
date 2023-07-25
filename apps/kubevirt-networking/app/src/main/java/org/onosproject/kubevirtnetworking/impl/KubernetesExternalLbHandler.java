/*
 * Copyright 2022-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.kubevirtnetworking.impl;

import org.onlab.packet.ARP;
import org.onlab.packet.EthType;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.TpPort;
import org.onosproject.cluster.ClusterService;
import org.onosproject.cluster.LeadershipService;
import org.onosproject.cluster.NodeId;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLb;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbAdminService;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbEvent;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbListener;
import org.onosproject.kubevirtnetworking.api.KubevirtFlowRuleService;
import org.onosproject.kubevirtnetworking.api.KubevirtGroupRuleService;
import org.onosproject.kubevirtnetworking.util.RulePopulatorUtil;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbInterface;
import org.onosproject.kubevirtnode.api.KubevirtApiConfigService;
import org.onosproject.kubevirtnode.api.KubevirtNode;
import org.onosproject.kubevirtnode.api.KubevirtNodeService;
import org.onosproject.net.Device;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.DriverService;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.instructions.ExtensionTreatment;
import org.onosproject.net.packet.PacketService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.onlab.util.Tools.groupedThreads;
import static org.onosproject.kubevirtnetworking.api.Constants.GW_DROP_TABLE;
import static org.onosproject.kubevirtnetworking.api.Constants.GW_ENTRY_TABLE;
import static org.onosproject.kubevirtnetworking.api.Constants.KUBERNETES_EXTERNAL_LB_FAKE_MAC;
import static org.onosproject.kubevirtnetworking.api.Constants.KUBEVIRT_NETWORKING_APP_ID;
import static org.onosproject.kubevirtnetworking.api.Constants.PRIORITY_ARP_GATEWAY_RULE;
import static org.onosproject.kubevirtnetworking.api.Constants.PRIORITY_ELB_DOWNSTREAM_RULE;
import static org.onosproject.kubevirtnetworking.api.Constants.PRIORITY_ELB_UPSTREAM_RULE;
import static org.onosproject.kubevirtnetworking.util.KubevirtNetworkingUtil.elbPatchPortNum;
import static org.onosproject.kubevirtnetworking.util.KubevirtNetworkingUtil.externalPatchPortNum;
import static org.onosproject.kubevirtnetworking.util.KubevirtNetworkingUtil.kubernetesElbMac;
import static org.onosproject.kubevirtnetworking.util.RulePopulatorUtil.CT_NAT_SRC_FLAG;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Handles Kubernetes External load balancer.
 */
@Component(immediate = true)
public class KubernetesExternalLbHandler {
    protected final Logger log = getLogger(getClass());

    private static final int TP_PORT_MINIMUM_NUM = 10000;
    private static final int TP_PORT_MAXIMUM_NUM = 65535;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ClusterService clusterService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LeadershipService leadershipService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubevirtApiConfigService configService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubevirtNodeService nodeService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubevirtGroupRuleService groupRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubevirtFlowRuleService flowService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubernetesExternalLbAdminService externalLbService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DriverService driverService;

    private final ExecutorService eventExecutor = newSingleThreadExecutor(
            groupedThreads(this.getClass().getSimpleName(), "event-handler"));

    private ApplicationId appId;
    private NodeId localNodeId;

    private final InternalKubernetesExternalLbListener lbListener =
            new InternalKubernetesExternalLbListener();

    @Activate
    protected void activate() {
        appId = coreService.registerApplication(KUBEVIRT_NETWORKING_APP_ID);
        localNodeId = clusterService.getLocalNode().id();
        leadershipService.runForLeadership(appId.name());
        externalLbService.addListener(lbListener);

        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        leadershipService.withdraw(appId.name());
        externalLbService.removeListener(lbListener);

        eventExecutor.shutdown();

        log.info("Stopped");
    }


    private class InternalKubernetesExternalLbListener implements KubernetesExternalLbListener {
        private boolean isRelevantHelper() {
            return Objects.equals(localNodeId, leadershipService.getLeader(appId.name()));
        }

        @Override
        public void event(KubernetesExternalLbEvent event) {
            switch (event.type()) {
                case KUBERNETES_EXTERNAL_LOAD_BALANCER_CREATED:
                case KUBERNETES_EXTERNAL_LOAD_BALANCER_UPDATED:
                    eventExecutor.execute(() -> processKubernetesExternalLbCreatedOrUpdated(
                            event.subject()));
                    break;
                case KUBERNETES_EXTERNAL_LOAD_BALANCER_GATEWAY_CHANGED:
                    eventExecutor.execute(() -> processKubernetesExternalLbGatewayChanged(
                            event.subject(), event.oldGateway()));
                    break;
                case KUBERNETES_EXTERNAL_LOAD_BALANCER_WORKER_CHANGED:
                    eventExecutor.execute(() -> processKubernetesExternalLbWorkerChanged(
                            event.subject(), event.oldWorker()));
                    break;
                case KUBERNETES_EXTERNAL_LOAD_BALANCER_REMOVED:
                    eventExecutor.execute(() -> processKubernetesExternalLbRemoved(
                            event.subject()));
                    break;
                default:
                    //do nothing
                    break;
            }
        }

        private void processKubernetesExternalLbCreatedOrUpdated(KubernetesExternalLb lb) {
            if (!isRelevantHelper()) {
                return;
            }

            if (lb.electedGateway() == null || lb.electedWorker() == null || lb.loadBalancerGwMac() == null) {
                log.warn("processKubernetesExternalLbCreatedOrUpdated called but electedGateway " +
                        "or electedWorker or loadBalancerGwMacis null. Stop this task.");
                return;
            }

            log.info("Create or update elb {}", lb);

            setExternalLbRulesForService(lb, true);
        }

        private void processKubernetesExternalLbGatewayChanged(KubernetesExternalLb lb, String oldGatway) {
            if (!isRelevantHelper()) {
                return;
            }

            if (lb.electedWorker() == null || oldGatway == null || lb.loadBalancerGwMac() == null) {
                log.warn("processKubernetesExternalLbGatewayChanged called but old electedWorker " +
                        "or electedWorker or loadBalancerGwMacis null. Stop this task.");
                return;
            }

            log.info("KubernetesExternalLbGatewayChanged from oldateway {} to new gateway {}",
                    oldGatway, lb.electedGateway());

            setExternalLbRulesForService(lb.updateElectedGateway(oldGatway), false);

            setExternalLbRulesForService(lb, true);
        }

        private void processKubernetesExternalLbWorkerChanged(KubernetesExternalLb lb, String oldWorker) {
            if (!isRelevantHelper()) {
                return;
            }

            if (lb.electedGateway() == null || oldWorker == null) {
                return;
            }

            log.info("ExternalLbWorkerChanged from oldworker {} to new worker {}",
                    oldWorker, lb.electedWorker());

            setExternalLbRulesForService(lb.updateElectedWorker(oldWorker), false);

            setExternalLbRulesForService(lb, true);
        }


        private void processKubernetesExternalLbRemoved(KubernetesExternalLb lb) {
            if (!isRelevantHelper()) {
                return;
            }

            if (lb.electedGateway() == null) {
                return;
            }

            setExternalLbRulesForService(lb, false);
        }
    }

    private void setExternalLbRulesForService(KubernetesExternalLb lb, boolean install) {
        if (lb.electedGateway() == null) {
            return;
        }

        KubevirtNode gateway = nodeService.node(lb.electedGateway());

        if (gateway == null) {
            return;
        }

        setLoadbalanceIpArpResponseRules(lb, gateway, install);
        setDownstreamRules(lb, gateway, install);
        setUpstreamRules(lb, gateway, install);
    }

    private void setLoadbalanceIpArpResponseRules(KubernetesExternalLb lb, KubevirtNode gateway, boolean install) {

        IpAddress loadBalancerIp = lb.loadBalancerIp();

        if (loadBalancerIp == null) {
            return;
        }

        TrafficSelector selector = DefaultTrafficSelector.builder()
                .matchInPort(externalPatchPortNum(deviceService, gateway))
                .matchEthType(EthType.EtherType.ARP.ethType().toShort())
                .matchArpOp(ARP.OP_REQUEST)
                .matchArpTpa(loadBalancerIp.getIp4Address())
                .build();

        Device device = deviceService.getDevice(gateway.intgBridge());

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .extension(RulePopulatorUtil.buildMoveEthSrcToDstExtension(device), device.id())
                .extension(RulePopulatorUtil.buildMoveArpShaToThaExtension(device), device.id())
                .extension(RulePopulatorUtil.buildMoveArpSpaToTpaExtension(device), device.id())
                .setArpOp(ARP.OP_REPLY)
                .setEthSrc(KUBERNETES_EXTERNAL_LB_FAKE_MAC)
                .setArpSha(KUBERNETES_EXTERNAL_LB_FAKE_MAC)
                .setArpSpa(loadBalancerIp.getIp4Address())
                .setOutput(PortNumber.IN_PORT)
                .build();

        flowService.setRule(
                appId,
                gateway.intgBridge(),
                selector,
                treatment,
                PRIORITY_ARP_GATEWAY_RULE,
                GW_ENTRY_TABLE,
                install);
    }

    private void setDownstreamRules(KubernetesExternalLb lb, KubevirtNode gateway, boolean install) {

        IpAddress loadBalancerIp = lb.loadBalancerIp();

        if (loadBalancerIp == null) {
            log.warn("setDownstreamRules called but loadBalancerIp is null. Stop this task.");
            return;
        }

        MacAddress elbIntfMac = kubernetesElbMac(deviceService, gateway);
        if (elbIntfMac == null) {
            log.warn("setDownstreamRules called but elbIntfMac is null. Stop this task.");
            return;
        }

        PortNumber elbBridgePortNum = elbPatchPortNum(deviceService, gateway);
        if (elbBridgePortNum == null) {
            log.warn("setDownstreamRules called but elbBridgePortNum is null. Stop this task.");
            return;
        }

        KubernetesExternalLbInterface externalLbInterface = gateway.kubernetesExternalLbInterface();
        if (externalLbInterface == null || externalLbInterface.externalLbGwMac() == null) {
            log.warn("setDownstreamRules called but externalLbInterface is null or " +
                    "externalLbInterfaceGwMac is null. Stop this task.");
            return;
        }

        KubevirtNode electedWorker = nodeService.node(lb.electedWorker());
        if (electedWorker == null) {
            log.warn("setDownstreamRules called but electedWorker is null. Stop this task.");
            return;
        }

        lb.servicePorts().forEach(servicePort -> {
            TrafficSelector selector = DefaultTrafficSelector.builder()
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchEthDst(KUBERNETES_EXTERNAL_LB_FAKE_MAC)
                    .matchIPDst(loadBalancerIp.toIpPrefix())
                    .matchIPProtocol(IPv4.PROTOCOL_TCP)
                    .matchTcpDst(TpPort.tpPort(servicePort.port().intValue()))
                    .build();

            ExtensionTreatment natTreatment = RulePopulatorUtil
                    .niciraConnTrackTreatmentBuilder(driverService, gateway.intgBridge())
                    .commit(true)
                    .natFlag(CT_NAT_SRC_FLAG)
                    .natAction(true)
                    .natIp(externalLbInterface.externalLbIp())
                    .natPortMin(TpPort.tpPort(TP_PORT_MINIMUM_NUM))
                    .natPortMax(TpPort.tpPort(TP_PORT_MAXIMUM_NUM))
                    .build();


            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .extension(natTreatment, gateway.intgBridge())
                    .setEthSrc(elbIntfMac)
                    .setEthDst(externalLbInterface.externalLbGwMac())
                    .setIpDst(electedWorker.dataIp())
                    .setTcpDst(TpPort.tpPort(servicePort.nodePort().intValue()))
                    .setOutput(elbBridgePortNum)
                    .build();

            flowService.setRule(
                    appId,
                    gateway.intgBridge(),
                    selector,
                    treatment,
                    PRIORITY_ELB_DOWNSTREAM_RULE,
                    GW_ENTRY_TABLE,
                    install);
        });
    }

    private void setUpstreamRules(KubernetesExternalLb lb, KubevirtNode gateway, boolean install) {
        IpAddress loadBalancerIp = lb.loadBalancerIp();

        if (loadBalancerIp == null) {
            log.warn("setUpstreamRules called but loadBalancerIp is null. Stop this task.");
            return;
        }

        MacAddress elbIntfMac = kubernetesElbMac(deviceService, gateway);
        if (elbIntfMac == null) {
            log.warn("setUpstreamRules called but elbIntfMac is null. Stop this task.");
            return;
        }

        PortNumber elbBridgePortNum = elbPatchPortNum(deviceService, gateway);
        if (elbBridgePortNum == null) {
            log.warn("setUpstreamRules called but elbBridgePortNum is null. Stop this task.");
            return;
        }

        PortNumber externalPatchPortNum = externalPatchPortNum(deviceService, gateway);
        if (externalPatchPortNum == null) {
            log.warn("setUpstreamRules called but externalPatchPortNum is null. Stop this task.");
            return;
        }

        KubernetesExternalLbInterface externalLbInterface = gateway.kubernetesExternalLbInterface();
        if (externalLbInterface == null) {
            log.warn("setUpstreamRules called but externalLbInterface is null. Stop this task.");
            return;
        }


        KubevirtNode electedWorker = nodeService.node(lb.electedWorker());
        if (electedWorker == null) {
            log.warn("setDownstreamRules called but electedWorker is null. Stop this task.");
            return;
        }

        lb.servicePorts().forEach(servicePort -> {
            TrafficSelector.Builder sBuilder = DefaultTrafficSelector.builder()
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchIPSrc(electedWorker.dataIp().toIpPrefix())
                    .matchIPDst(externalLbInterface.externalLbIp().toIpPrefix())
                    .matchIPProtocol(IPv4.PROTOCOL_TCP)
                    .matchTcpSrc(TpPort.tpPort(servicePort.nodePort().intValue()));

            ExtensionTreatment natTreatment = RulePopulatorUtil
                    .niciraConnTrackTreatmentBuilder(driverService, gateway.intgBridge())
                    .commit(false)
                    .natAction(true)
                    .table((short) GW_DROP_TABLE)
                    .build();

            TrafficTreatment.Builder tBuilder = DefaultTrafficTreatment.builder()
                    .setEthSrc(KUBERNETES_EXTERNAL_LB_FAKE_MAC)
                    .setIpSrc(lb.loadBalancerIp())
                    .setEthDst(lb.loadBalancerGwMac())
                    .setTcpSrc(TpPort.tpPort(servicePort.port().intValue()))
                    .extension(natTreatment, gateway.intgBridge())
                    .transition(GW_DROP_TABLE);

            flowService.setRule(
                    appId,
                    gateway.intgBridge(),
                    sBuilder.build(),
                    tBuilder.build(),
                    PRIORITY_ELB_UPSTREAM_RULE,
                    GW_ENTRY_TABLE,
                    install);

            sBuilder = DefaultTrafficSelector.builder()
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchIPProtocol(IPv4.PROTOCOL_TCP)
                    .matchEthSrc(KUBERNETES_EXTERNAL_LB_FAKE_MAC)
                    .matchIPSrc(lb.loadBalancerIp().toIpPrefix())
                    .matchEthDst(lb.loadBalancerGwMac())
                    .matchTcpSrc(TpPort.tpPort(servicePort.port().intValue()));

            tBuilder = DefaultTrafficTreatment.builder()
                    .setOutput(externalPatchPortNum);

            flowService.setRule(
                    appId,
                    gateway.intgBridge(),
                    sBuilder.build(),
                    tBuilder.build(),
                    PRIORITY_ELB_UPSTREAM_RULE,
                    GW_DROP_TABLE,
                    install);
        });
    }
}
