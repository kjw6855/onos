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
 * Service for administering the inventory of kubernetes external lb service.
 */
public interface KubernetesExternalLbAdminService extends KubernetesExternalLbService {
    /**
     * Create a kubernetes external load balancer with the given information.
     *
     * @param externalLb a new load balancer
     */
    void createExternalLb(KubernetesExternalLb externalLb);

    /**
     * Update a kubernetes external load balancer with the given information.
     *
     * @param externalLb the updated load balancer
     */
    void updateExternalLb(KubernetesExternalLb externalLb);

    /**
     * Removes the load balancer.
     *
     * @param serviceName load balancer name
     */
    void removeExternalLb(String serviceName);

    /**
     * Removes all load balancers.
     */
    void clear();
}
