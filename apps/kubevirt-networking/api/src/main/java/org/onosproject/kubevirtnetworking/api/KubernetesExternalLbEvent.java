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

import org.onosproject.event.AbstractEvent;

/**
 * Manages inventory of kubernetes external load balancer states; not intended for direct use.
 */
public class KubernetesExternalLbEvent
    extends AbstractEvent<KubernetesExternalLbEvent.Type, KubernetesExternalLb> {

    private final String oldGateway;
    private final String oldWorker;


    public KubernetesExternalLbEvent(Type type, KubernetesExternalLb subject) {
        super(type, subject);
        this.oldGateway = null;
        this.oldWorker = null;
    }

    public KubernetesExternalLbEvent(Type type, KubernetesExternalLb subject, String oldGateway, String oldWorker) {
        super(type, subject);
        this.oldGateway = oldGateway;
        this.oldWorker = oldWorker;
    }

    public KubernetesExternalLbEvent(Type type, KubernetesExternalLb subject, String oldWorker) {
        super(type, subject);
        this.oldGateway = null;
        this.oldWorker = oldWorker;
    }

    /**
     * Kubernetes external lb events.
     */
    public enum Type {
        /**
         * Signifies that a new kubernetex external load balancer is created.
         */
        KUBERNETES_EXTERNAL_LOAD_BALANCER_CREATED,

        /**
         * Signifies that a kubernetex external load balancer is removed.
         */
        KUBERNETES_EXTERNAL_LOAD_BALANCER_REMOVED,

        /**
         * Signifies that a kubernetex external load balancer is updated.
         */
        KUBERNETES_EXTERNAL_LOAD_BALANCER_UPDATED,

        /**
         * Signifies that a kubernetes external load balancer gateway node is updated.
         */
        KUBERNETES_EXTERNAL_LOAD_BALANCER_GATEWAY_CHANGED,

        /**
         * Signifies that a kubernetes external load balancer worker node is updated.
         */
        KUBERNETES_EXTERNAL_LOAD_BALANCER_WORKER_CHANGED,
    }

    /**
     * Returns the old gateway of the router event.
     *
     * @return gateway node hostname
     */
    public String oldGateway() {
        return oldGateway;
    }

    /**
     * Returns the old worker of the router event.
     *
     * @return worker node hostname
     */
    public String oldWorker() {
        return oldWorker;
    }
}
