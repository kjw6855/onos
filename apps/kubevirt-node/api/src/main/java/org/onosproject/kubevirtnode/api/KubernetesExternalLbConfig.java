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
package org.onosproject.kubevirtnode.api;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;

/**
 * Representation of configuration used in Kubernetes External Lb service.
 */
public interface KubernetesExternalLbConfig {

    /**
     * Returns the name of kubernetes external lb config.
     * This is defined in the configmap.
     *
     * @return config name
     */
    String configName();
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

    /**
     * Returns the global IP range used in external LB.
     * Each service of type LoadBalancer would get the public IP out of those.
     * Format: "223.39.6.85-223.39.6.90"
     *
     * @return global Ip range
     */
    String globalIpRange();

    /**
     * Returns the KubernetesExternalLbConfig with updated external lb gateway mac address.
     *
     * @param gatewayMac external lb gateway mac address
     * @return KubernetesExternalLbConfig
     */
    KubernetesExternalLbConfig updateLbGatewayMac(MacAddress gatewayMac);

    interface Builder {
        /**
         * Builds an immutable kubernal external lb config instance.
         *
         * @return kubernetes external lb config
         */
        KubernetesExternalLbConfig build();

        /**
         * Returns kubernetes external lb config builder with supplied config name.
         *
         * @param configName config name
         * @return kubernetes external lb config builder
         */
        Builder configName(String configName);

        /**
         * Returns kubernetes external lb config builder with supplied loadbalancer gw Ip.
         *
         * @param loadBalancerGwIp loadbalancer gw Ip
         * @return kubernetes external lb config builder
         */
        Builder loadBalancerGwIp(IpAddress loadBalancerGwIp);

        /**
         * Returns kubernetes external lb config builder with supplied loadbalancer gw Mac.
         *
         * @param loadBalancerGwMac loadbalancer gw Mac
         * @return kubernetes external lb config builder
         */
        Builder loadBalancerGwMac(MacAddress loadBalancerGwMac);

        /**
         * Returns kubernetes external lb config builder with supplied global Ip range.
         *
         * @param globalIpRange global Ip range
         * @return kubernetes external lb config builder
         */
        Builder globalIpRange(String globalIpRange);

    }
}
