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

public class DefaultKubernetesExternalLbInterface implements KubernetesExternalLbInterface {

    private static final String NOT_NULL_MSG = "KubernetesExternalLbInterface % cannot be null";

    private String elbBridgeName;
    private IpAddress elbIp;
    private IpAddress elbGwIp;
    private MacAddress elbGwMac;

    public DefaultKubernetesExternalLbInterface(String elbBridgeName, IpAddress elbIp,
                                                IpAddress elbGwIp, MacAddress elbGwMac) {
        this.elbBridgeName = elbBridgeName;
        this.elbIp = elbIp;
        this.elbGwIp = elbGwIp;
        this.elbGwMac = elbGwMac;
    }

    @Override
    public String externalLbBridgeName() {
        return elbBridgeName;
    }

    @Override
    public IpAddress externalLbIp() {
        return elbIp;
    }

    @Override
    public IpAddress externalLbGwIp() {
        return elbGwIp;
    }

    @Override
    public MacAddress externalLbGwMac() {
        return elbGwMac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultKubernetesExternalLbInterface that = (DefaultKubernetesExternalLbInterface) o;

        return Objects.equals(elbBridgeName, that.elbBridgeName) &&
                Objects.equals(elbIp, that.elbIp) &&
                Objects.equals(elbGwIp, that.elbGwIp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("elbBridgeName", elbBridgeName)
                .add("elbIp", elbIp)
                .add("elbGwIp", elbGwIp)
                .add("elbGwMac", elbGwMac)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(elbBridgeName, elbIp, elbGwIp);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements KubernetesExternalLbInterface.Builder {
        private String elbBridgeName;
        private IpAddress elbIp;
        private IpAddress elbGwIp;
        private MacAddress elbGwMac;

        private Builder() {
        }

        @Override
        public KubernetesExternalLbInterface build() {
            checkArgument(elbBridgeName != null, NOT_NULL_MSG, "externalLbBridgeName");
            checkArgument(elbIp != null, NOT_NULL_MSG, "externalLbIp");
            checkArgument(elbGwIp != null, NOT_NULL_MSG, "externalLbGwIp");

            return new DefaultKubernetesExternalLbInterface(elbBridgeName, elbIp, elbGwIp, elbGwMac);
        }

        @Override
        public Builder externalLbBridgeName(String elbBridgeName) {
            this.elbBridgeName = elbBridgeName;
            return this;
        }

        @Override
        public Builder externalLbIp(IpAddress elbIp) {
            this.elbIp = elbIp;
            return this;
        }

        @Override
        public Builder externallbGwIp(IpAddress elbGwIp) {
            this.elbGwIp = elbGwIp;
            return this;
        }

        @Override
        public Builder externalLbGwMac(MacAddress elbGwMac) {
            this.elbGwMac = elbGwMac;
            return this;
        }
    }
}
