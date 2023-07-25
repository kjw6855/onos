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

/**
 * Service for administering inventory of Kubernetes External Lb Configs.
 */
public interface KubernetesExternalLbConfigAdminService extends KubernetesExternalLbConfigService {

    /**
     * Creates a new kubernetes external lb config.
     *
     * @param lbConfig kubernetes external lb config
     */
    void createKubernetesExternalLbConfig(KubernetesExternalLbConfig lbConfig);

    /**
     * Updates a new kubernetes external lb config.
     *
     * @param lbConfig kubernetes external lb config
     */
    void updateKubernetesExternalLbConfig(KubernetesExternalLbConfig lbConfig);

    /**
     * Removes a new kubernetes external lb config.
     *
     * @param configName kubernetes external lb config
     */
    void removeKubernetesExternalLbConfig(String configName);
}
