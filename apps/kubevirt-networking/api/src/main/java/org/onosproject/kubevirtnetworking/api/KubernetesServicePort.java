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

/**
 * Representation of a Kubernetes service port for kubernetes external load balancer.
 */
public interface KubernetesServicePort {

    /**
     * Returns the port.
     *
     * @return port
     */
    Integer port();

    /**
     * Returns the node port.
     *
     * @return node port
     */
    Integer nodePort();

    interface Builder {

        /**
         * Builds immutable kubernetes service port instance.
         *
         * @return kubernetes service port instance
         */
        KubernetesServicePort build();

        /**
         * Returns kubernetes service port builder with supplied port.
         *
         * @param port port
         * @return kubernetes service port builder
         */
        Builder port(Integer port);

        /**
         * Returns kubernetes service port builder with supplied node port.
         *
         * @param nodePort node port
         * @return kubernetes service port builder
         */
        Builder nodePort(Integer nodePort);
    }
}
