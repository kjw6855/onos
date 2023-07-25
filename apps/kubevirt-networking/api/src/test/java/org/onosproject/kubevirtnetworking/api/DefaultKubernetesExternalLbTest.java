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
package org.onosproject.kubevirtnetworking.api;

import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.onlab.junit.ImmutableClassChecker.assertThatClassIsImmutable;

/**
 * Unit tests for the default Kubernetes external lb class.
 */
public class DefaultKubernetesExternalLbTest {
    private static final String SERVICE_NAME_1 = "service_name_1";
    private static final String SERVICE_NAME_2 = "service_name_2";
    private static final IpAddress LOADBALANCER_IP_1 = IpAddress.valueOf("1.1.1.2");
    private static final IpAddress LOADBALANCER_IP_2 = IpAddress.valueOf("2.2.2.2");
    private static final KubernetesServicePort SERVICE_PORT_1 = DefaultKubernetesServicePort.builder()
            .port(Integer.valueOf(8080))
            .nodePort(Integer.valueOf(31080))
            .build();
    private static final KubernetesServicePort SERVICE_PORT_2 = DefaultKubernetesServicePort.builder()
            .port(Integer.valueOf(8081))
            .nodePort(Integer.valueOf(31081))
            .build();
    private static final Set<KubernetesServicePort> SERVICE_PORT_SET_1 = Sets.newHashSet(SERVICE_PORT_1);
    private static final Set<KubernetesServicePort> SERVICE_PORT_SET_2 = Sets.newHashSet(SERVICE_PORT_2);
    private static final Set<String> ENDPOINT_SET_1 = Sets.newHashSet(String.valueOf("1.1.2.1"));
    private static final Set<String> ENDPOINT_SET_2 = Sets.newHashSet(String.valueOf("1.1.2.2"));
    private static final String ELECTED_GATEWAY_1 = "gateway1";
    private static final String ELECTED_GATEWAY_2 = "gateway2";
    private static final String ELECTED_WORKER_1 = "worker1";
    private static final String ELECTED_WORKER_2 = "worker2";

    private static final IpAddress LOADBALANCER_GW_IP_1 = IpAddress.valueOf("1.1.1.1");
    private static final IpAddress LOADBALANCER_GW_IP_2 = IpAddress.valueOf("2.2.2.1");
    private static final MacAddress LOADBALANCER_GW_MAC_1 = MacAddress.valueOf("aa:bb:cc:dd:ee:ff");
    private static final MacAddress LOADBALANCER_GW_MAC_2 = MacAddress.valueOf("ff:ee:dd:cc:bb:aa");

    private KubernetesExternalLb lb1;
    private KubernetesExternalLb sameAsLb1;
    private KubernetesExternalLb lb2;

    /**
     * Tests class immutability.
     */
    @Test
    public void testImmutability() {
        assertThatClassIsImmutable(DefaultKubernetesExternalLb.class);
    }

    /**
     * Initial setup for this unit test.
     */
    @Before
    public void setUp() {
        lb1 = DefaultKubernetesExternalLb.builder()
                .serviceName(SERVICE_NAME_1)
                .loadBalancerIp(LOADBALANCER_IP_1)
                .servicePorts(SERVICE_PORT_SET_1)
                .endpointSet(ENDPOINT_SET_1)
                .electedGateway(ELECTED_GATEWAY_1)
                .electedWorker(ELECTED_WORKER_1)
                .loadBalancerGwIp(LOADBALANCER_GW_IP_1)
                .loadBalancerGwMac(LOADBALANCER_GW_MAC_1)
                .build();

        sameAsLb1 = DefaultKubernetesExternalLb.builder()
                .serviceName(SERVICE_NAME_1)
                .loadBalancerIp(LOADBALANCER_IP_1)
                .servicePorts(SERVICE_PORT_SET_1)
                .endpointSet(ENDPOINT_SET_1)
                .electedGateway(ELECTED_GATEWAY_1)
                .electedWorker(ELECTED_WORKER_1)
                .loadBalancerGwIp(LOADBALANCER_GW_IP_1)
                .loadBalancerGwMac(LOADBALANCER_GW_MAC_1)
                .build();

        lb2 = DefaultKubernetesExternalLb.builder()
                .serviceName(SERVICE_NAME_2)
                .loadBalancerIp(LOADBALANCER_IP_2)
                .servicePorts(SERVICE_PORT_SET_2)
                .endpointSet(ENDPOINT_SET_2)
                .electedGateway(ELECTED_GATEWAY_2)
                .electedWorker(ELECTED_WORKER_2)
                .loadBalancerGwIp(LOADBALANCER_GW_IP_2)
                .loadBalancerGwMac(LOADBALANCER_GW_MAC_2)
                .build();
    }

    /**
     * Tests object equality.
     */
    @Test
    public void testEquality() {
        new EqualsTester().addEqualityGroup(lb1, sameAsLb1)
                .addEqualityGroup(lb2)
                .testEquals();
    }

    /**
     * Test object construction.
     */
    @Test
    public void testConstruction() {
        KubernetesExternalLb lb = lb1;

        assertEquals(SERVICE_NAME_1, lb.serviceName());
        assertEquals(LOADBALANCER_IP_1, lb.loadBalancerIp());
        assertEquals(SERVICE_PORT_SET_1, lb.servicePorts());
        assertEquals(ENDPOINT_SET_1, lb.endpointSet());
    }
}
