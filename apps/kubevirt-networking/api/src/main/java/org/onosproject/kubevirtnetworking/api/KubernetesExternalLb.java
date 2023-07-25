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

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;

import java.util.Set;

/**
 * Representation of kubernetes external load balancer.
 */
public interface KubernetesExternalLb {
    /**
     * Returns the service name.
     *
     * @return service name
     */
    String serviceName();

    /**
     * Returns the load balancer IP.
     *
     * @return load balancer IP
     */
    IpAddress loadBalancerIp();

    /**
     * Returns the set of service ports.
     *
     * @return node port
     */
    Set<KubernetesServicePort> servicePorts();

    /**
     * Returns the set of endpoint.
     *
     * @return endpoint set
     */
    Set<String> endpointSet();

    /**
     * Returns the elected gateway node for this service.
     *
     * @return gateway node hostname
     */
    String electedGateway();

    /**
     * Returns the elected worker node for this service.
     *
     * @return worker node hostname
     */
    String electedWorker();

    /**
     * Updates the elected gateway node host name.
     *
     * @param electedGateway updated elected gateway node hostname
     * @return kubernetes external lb with the updated gateway node hostname
     */
    KubernetesExternalLb updateElectedGateway(String electedGateway);

    /**
     * Updates the elected worker node host name.
     *
     * @param electedWorker updated elected worker node hostname
     * @return  kubernetes external lb with the updated worker node hostname
     */
    KubernetesExternalLb updateElectedWorker(String electedWorker);

    /**
     * Returns the gateway IP of load balancer.
     * TEG would send outbound traffic to this gateway.
     *
     * @return load balancer gateway IP
     */
    IpAddress loadBalancerGwIp();

    /**
     * Returns the gateway MAC of load balancer.
     * TEG would send outbound traffic to this gateway.
     *
     * @return load balancer gateway IP
     */
    MacAddress loadBalancerGwMac();


    interface Builder {
        /**
         * Builds an immutable kubernetes external load balancer instance.
         *
         * @return kubernetes external load balancer
         */
        KubernetesExternalLb build();

        /**
         * Returns kubernetes external load balancer builder with supplied service name.
         *
         * @param serviceName external load balancer service name
         * @return external load balancer builder
         */
        Builder serviceName(String serviceName);

        /**
         * Returns kubernetes external load balancer builder with supplied load balancer Ip.
         *
         * @param loadBalancerIp external load balancer Ip
         * @return external load balancer builder
         */
        Builder loadBalancerIp(IpAddress loadBalancerIp);


        /**
         * Returns kubernetes external load balancer builder with supplied service port set.
         *
         * @param servicePorts service port set
         * @return external load balancer builder
         */
        Builder servicePorts(Set<KubernetesServicePort> servicePorts);

        /**
         * Returns kubernetes external load balancer builder with supplied endpoint set.
         *
         * @param endpointSet endpoint set
         * @return external load balancer builder
         */
        Builder endpointSet(Set<String> endpointSet);

        /**
         * Returns kubernetes external load balancer builder with supplied elected gateway.
         *
         * @param gateway gateway node hostname
         * @return external load balancer builder
         */
        Builder electedGateway(String gateway);

        /**
         * Returns kubernetes external load balancer builder with supplied elected worker.
         *
         * @param worker worker node hostname
         * @return external load balancer builder
         */
        Builder electedWorker(String worker);


        /**
         * Returns kubernetes external load balancer builder with supplied load balancer gateway Ip.
         *
         * @param loadBalancerGwIp gateway IP of the external load balancer
         * @return external load balancer builder
         */
        Builder loadBalancerGwIp(IpAddress loadBalancerGwIp);

        /**
         * Returns kubernetes external load balancer builder with supplied load balancer gateway Mac.
         *
         * @param loadBalancerGwMac gateway Mac of the external load balancer
         * @return external load balancer builder
         */
        Builder loadBalancerGwMac(MacAddress loadBalancerGwMac);
    }
}
