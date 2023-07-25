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
 * Representation of a Kubernetes external load balancer interface for kubevirt node.
 */
public interface KubernetesExternalLbInterface {

    /**
     *  Returns the name of the elb bridge.
     *  Using this bridge, TEG internally communicates with data IP's in worker nodes.
     *
     * @return gateway bridge name
     */
    String externalLbBridgeName();

    /**
     *  Returns the internal Ip Address of TEG for kubernetes external lb purpose.
     *
     * @return elb ip address
     */
    IpAddress externalLbIp();

    /**
     *  Returns the gateway IP of the elb IP.
     *
     * @return elb gw ip address
     */
    IpAddress externalLbGwIp();

    /**
     *  Returns the mac address of the elb gw.
     *
     * @return elb gw mac address
     */
    MacAddress externalLbGwMac();


    interface Builder {

        /**
         * Builds an immutable kubernetes external load balancer interface instance.
         *
         * @return  external load balancer interface instance
         */
        KubernetesExternalLbInterface build();

        /**
         * Returns kubernetes external load balancer interface builder with supplied elb bridge name.
         *
         * @param elbBridgeName elb bridge name
         * @return kubernetes external load balancer interface builder
         */
        Builder externalLbBridgeName(String elbBridgeName);

        /**
         * Returns kubernetes external load balancer interface builder with supplied supplied elb Ip address.
         *
         * @param elbIp elb ip address
         * @return kubernetes external load balancer interface builder
         */
        Builder externalLbIp(IpAddress elbIp);

        /**
         * Returns kubernetes external load balancer interface builder with supplied supplied elb gw Ip address.
         *
         * @param elbGwIp elb gw ip address
         * @return kubernetes external load balancer interface builder
         */
        Builder externallbGwIp(IpAddress elbGwIp);

        /**
         * Returns kubernetes external load balancer interface builder with supplied supplied elb gw MAC address.
         *
         * @param elbGwMac elb gw mac address
         * @return kubernetes external load balancer interface builder
         */
        Builder externalLbGwMac(MacAddress elbGwMac);
    }
}
