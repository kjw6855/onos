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
package org.onosproject.kubevirtnode.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.event.ListenerRegistry;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfig;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfigAdminService;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfigEvent;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfigListener;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfigService;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfigStore;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbConfigStoreDelegate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Provides implementation of administrating and interfacing kubernetes external lb config.
 */
@Component(
        immediate = true,
        service = {KubernetesExternalLbConfigAdminService.class, KubernetesExternalLbConfigService.class}
)
public class KubernetesExternalLbConfigManager
        extends ListenerRegistry<KubernetesExternalLbConfigEvent, KubernetesExternalLbConfigListener>
        implements KubernetesExternalLbConfigAdminService, KubernetesExternalLbConfigService {
    protected final Logger log = getLogger(getClass());

    private static final String MSG_LOAD_BALANCER_CONFIG = "Kubernetes external lb config %s %s";
    private static final String MSG_CREATED = "created";
    private static final String MSG_UPDATED = "updated";
    private static final String MSG_REMOVED = "removed";

    private static final String ERR_NULL_LOAD_BALANCER_CONFIG = "Kubernetes external lb config cannot be null";
    private static final String ERR_NULL_LOAD_BALANCER_CONFIG_NAME
            = "Kubernetes external lb config name cannot be null";
    private static final String ERR_IN_USE = " still in use";

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KubernetesExternalLbConfigStore lbConfigStore;

    private final InternalKubernetesExternalLbConfigStorageDelegate delegate =
            new InternalKubernetesExternalLbConfigStorageDelegate();

    private ApplicationId appId;


    @Activate
    protected void activate() {
        appId = coreService.registerApplication(APP_ID);

        lbConfigStore.setDelegate(delegate);
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        lbConfigStore.unsetDelegate(delegate);
        log.info("Stopped");
    }


    @Override
    public void createKubernetesExternalLbConfig(KubernetesExternalLbConfig lbConfig) {
        checkNotNull(lbConfig, ERR_NULL_LOAD_BALANCER_CONFIG);
        checkArgument(!Strings.isNullOrEmpty(lbConfig.configName()), ERR_NULL_LOAD_BALANCER_CONFIG_NAME);

        lbConfigStore.createExternalLbConfig(lbConfig);
        log.info(String.format(MSG_LOAD_BALANCER_CONFIG, lbConfig.configName(), MSG_CREATED));
    }

    @Override
    public void updateKubernetesExternalLbConfig(KubernetesExternalLbConfig lbConfig) {
        checkNotNull(lbConfig, ERR_NULL_LOAD_BALANCER_CONFIG);
        checkArgument(!Strings.isNullOrEmpty(lbConfig.configName()), ERR_NULL_LOAD_BALANCER_CONFIG_NAME);

        lbConfigStore.updateExternalLbConfig(lbConfig);
        log.info(String.format(MSG_LOAD_BALANCER_CONFIG, lbConfig.configName(), MSG_UPDATED));
    }

    @Override
    public void removeKubernetesExternalLbConfig(String configName) {

        checkArgument(configName != null, ERR_NULL_LOAD_BALANCER_CONFIG_NAME);

        synchronized (this) {
            KubernetesExternalLbConfig lbConfig = lbConfigStore.removeExternalLbConfig(configName);

            if (lbConfig != null) {
                log.info(String.format(MSG_LOAD_BALANCER_CONFIG, lbConfig.configName(), MSG_REMOVED));
            }
        }
    }

    @Override
    public KubernetesExternalLbConfig lbConfig(String configName) {
        checkArgument(configName != null, ERR_NULL_LOAD_BALANCER_CONFIG_NAME);

        return lbConfigStore.externalLbConfig(configName);
    }

    @Override
    public Set<KubernetesExternalLbConfig> lbConfigs() {
        return ImmutableSet.copyOf(lbConfigStore.externalLbConfigs());
    }

    private class InternalKubernetesExternalLbConfigStorageDelegate
            implements KubernetesExternalLbConfigStoreDelegate {

        @Override
        public void notify(KubernetesExternalLbConfigEvent event) {
            log.trace("send kubernetes external lb config event {}", event);
            process(event);
        }
    }
}
