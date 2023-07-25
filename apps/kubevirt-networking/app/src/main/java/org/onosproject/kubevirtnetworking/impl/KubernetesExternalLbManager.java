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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.event.ListenerRegistry;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLb;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbAdminService;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbEvent;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbListener;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbService;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbStore;
import org.onosproject.kubevirtnetworking.api.KubernetesExternalLbStoreDelegate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.kubevirtnetworking.api.Constants.KUBEVIRT_NETWORKING_APP_ID;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Provides implementation of administrating and interfacing kubernetes external lb.
 */
@Component(
        immediate = true,
        service = {KubernetesExternalLbAdminService.class, KubernetesExternalLbService.class}
)
public class KubernetesExternalLbManager
        extends ListenerRegistry<KubernetesExternalLbEvent, KubernetesExternalLbListener>
        implements KubernetesExternalLbAdminService, KubernetesExternalLbService {

    protected final Logger log = getLogger(getClass());

    private static final String MSG_LOAD_BALANCER = "Kubernetes external lb %s %s";
    private static final String MSG_CREATED = "created";
    private static final String MSG_UPDATED = "updated";
    private static final String MSG_REMOVED = "removed";

    private static final String ERR_NULL_LOAD_BALANCER = "Kubernetes external lb cannot be null";
    private static final String ERR_NULL_LOAD_BALANCER_NAME = "Kubernetes external lb name cannot be null";
    private static final String ERR_IN_USE = " still in use";

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubernetesExternalLbStore lbStore;

    private final InternalKubernetesExternalLbStorageDelegate delegate =
            new InternalKubernetesExternalLbStorageDelegate();

    private ApplicationId appId;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication(KUBEVIRT_NETWORKING_APP_ID);

        lbStore.setDelegate(delegate);
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        lbStore.unsetDelegate(delegate);
        log.info("Stopped");
    }

    @Override
    public void createExternalLb(KubernetesExternalLb externalLb) {
        checkNotNull(externalLb, ERR_NULL_LOAD_BALANCER);
        checkArgument(!Strings.isNullOrEmpty(externalLb.serviceName()), ERR_NULL_LOAD_BALANCER_NAME);


        lbStore.createLoadBalancer(externalLb);
        log.info(String.format(MSG_LOAD_BALANCER, externalLb.serviceName(), MSG_CREATED));

    }

    @Override
    public void updateExternalLb(KubernetesExternalLb externalLb) {
        checkNotNull(externalLb, ERR_NULL_LOAD_BALANCER);
        checkArgument(!Strings.isNullOrEmpty(externalLb.serviceName()), ERR_NULL_LOAD_BALANCER_NAME);

        lbStore.updateLoadBalancer(externalLb);
        log.info(String.format(MSG_LOAD_BALANCER, externalLb.serviceName(), MSG_UPDATED));
    }

    @Override
    public void removeExternalLb(String serviceName) {
        checkArgument(serviceName != null, ERR_NULL_LOAD_BALANCER_NAME);

        synchronized (this) {
            KubernetesExternalLb externalLb = lbStore.removeLoadBalancer(serviceName);

            if (externalLb != null) {
                log.info(String.format(MSG_LOAD_BALANCER, externalLb.serviceName(), MSG_REMOVED));
            }
        }
    }

    @Override
    public void clear() {
        lbStore.clear();
    }

    @Override
    public KubernetesExternalLb loadBalancer(String serviceName) {
        checkArgument(!Strings.isNullOrEmpty(serviceName), ERR_NULL_LOAD_BALANCER_NAME);

        return lbStore.loadBalancer(serviceName);
    }

    @Override
    public Set<KubernetesExternalLb> loadBalancers() {
        return ImmutableSet.copyOf(lbStore.loadBalancers());
    }

    private class InternalKubernetesExternalLbStorageDelegate
            implements KubernetesExternalLbStoreDelegate {

        @Override
        public void notify(KubernetesExternalLbEvent event) {
            log.trace("send kubernetes external lb event {}", event);
            process(event);
        }
    }
}
