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

import org.onosproject.store.Store;

import java.util.Set;

/**
 * Manages inventory of kubernetes external load balancer states; not intended for direct use.
 */
public interface KubernetesExternalLbStore
        extends Store<KubernetesExternalLbEvent, KubernetesExternalLbStoreDelegate> {

    /**
     * Creates a new kubernetes external lb.
     *
     * @param lb kubernetes external lb
     */
    void createLoadBalancer(KubernetesExternalLb lb);

    /**
     * Updates a new kubernetes external lb.
     *
     * @param lb kubernetes external lb
     */
    void updateLoadBalancer(KubernetesExternalLb lb);

    /**
     * Removes the kubernetes external lb with the given lb name.
     *
     * @param serviceName service name
     * @return kubernetes external lb
     */
    KubernetesExternalLb removeLoadBalancer(String serviceName);

    /**
     * Returns the kubernetes external lb with the given lb name.
     *
     * @param serviceName service name
     * @return kubernetes external lb
     */
    KubernetesExternalLb loadBalancer(String serviceName);

    /**
     * Returns all kubernetes external lbs.
     *
     * @return set of kubernetes external lbs
     */
    Set<KubernetesExternalLb> loadBalancers();

    /**
     * Removes all kubernetes external lbs.
     */
    void clear();
}
