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

import com.google.common.base.MoreObjects;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Default implementation of kubernetes external lb config.
 */
public final class DefaultKubernetesExternalLbConfig implements KubernetesExternalLbConfig {

    private static final String NOT_NULL_MSG = "ExternalLbConfig % cannot be null";

    private final String configName;
    private final IpAddress loadBalancerGwIp;
    private final MacAddress loadBalancerGwMac;
    private final String globalIpRange;

    public DefaultKubernetesExternalLbConfig(String configName, IpAddress loadBalancerGwIp,
                                             MacAddress loadBalancerGwMac, String globalIpRange) {
        this.configName = configName;
        this.loadBalancerGwIp = loadBalancerGwIp;
        this.loadBalancerGwMac = loadBalancerGwMac;
        this.globalIpRange = globalIpRange;
    }

    @Override
    public String configName() {
        return configName;
    }

    @Override
    public IpAddress loadBalancerGwIp() {
        return loadBalancerGwIp;
    }

    @Override
    public MacAddress loadBalancerGwMac() {
        return loadBalancerGwMac;
    }

    @Override
    public String globalIpRange() {
        return globalIpRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultKubernetesExternalLbConfig that = (DefaultKubernetesExternalLbConfig) o;

        return Objects.equals(configName, that.configName) &&
                Objects.equals(loadBalancerGwIp, that.loadBalancerGwIp) &&
                Objects.equals(globalIpRange, that.globalIpRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName, loadBalancerGwIp, globalIpRange);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("configName", configName)
                .add("loadBalancerGwIp", loadBalancerGwIp)
                .add("loadBalancerGwMac", loadBalancerGwMac)
                .add("globalIpRange", globalIpRange)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public KubernetesExternalLbConfig updateLbGatewayMac(MacAddress gatewayMac) {
        return DefaultKubernetesExternalLbConfig.builder()
                .configName(configName)
                .loadBalancerGwIp(loadBalancerGwIp)
                .loadBalancerGwMac(gatewayMac)
                .globalIpRange(globalIpRange)
                .build();
    }

    public static final class Builder implements KubernetesExternalLbConfig.Builder {

        private String configName;
        private IpAddress loadBalancerGwIp;
        private MacAddress loadBalancerGwMac;
        private String globalIpRange;

        private Builder() {
        }

        @Override
        public KubernetesExternalLbConfig build() {
            checkArgument(configName != null, NOT_NULL_MSG, "configName");
            checkArgument(loadBalancerGwIp != null, NOT_NULL_MSG, "loadBalancerGwIp");
            checkArgument(globalIpRange != null, NOT_NULL_MSG, "globalIpRange");

            return new DefaultKubernetesExternalLbConfig(configName, loadBalancerGwIp,
                    loadBalancerGwMac, globalIpRange);
        }

        @Override
        public Builder configName(String configName) {
            this.configName = configName;
            return this;
        }

        @Override
        public Builder loadBalancerGwIp(IpAddress loadBalancerGwIp) {
            this.loadBalancerGwIp = loadBalancerGwIp;
            return this;
        }

        @Override
        public KubernetesExternalLbConfig.Builder loadBalancerGwMac(MacAddress loadBalancerGwMac) {
            this.loadBalancerGwMac = loadBalancerGwMac;
            return this;
        }

        @Override
        public Builder globalIpRange(String globalIpRange) {
            this.globalIpRange = globalIpRange;
            return this;
        }
    }
}
