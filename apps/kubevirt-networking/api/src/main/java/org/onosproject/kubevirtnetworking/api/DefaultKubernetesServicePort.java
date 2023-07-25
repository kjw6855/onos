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

import com.google.common.base.MoreObjects;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultKubernetesServicePort implements KubernetesServicePort {
    private static final String NOT_EMPTY_MSG = "KubernetesServicePort % cannot be empty";

    private Integer port;
    private Integer nodePort;

    public DefaultKubernetesServicePort(Integer port, Integer nodePort) {
        this.port = port;
        this.nodePort = nodePort;
    }

    @Override
    public Integer port() {
        return port;
    }

    @Override
    public Integer nodePort() {
        return nodePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultKubernetesServicePort that = (DefaultKubernetesServicePort) o;

        return Objects.equals(port, that.port) &&
                Objects.equals(nodePort, that.nodePort);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("port", port.toString())
                .add("nodePort", nodePort.toString())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, nodePort);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements KubernetesServicePort.Builder {

        private Integer port;
        private Integer nodePort;

        private Builder() {
        }

        @Override
        public KubernetesServicePort build() {
            checkArgument(port != null, NOT_EMPTY_MSG, "port");
            checkArgument(nodePort != null, NOT_EMPTY_MSG, "nodePort");

            return new DefaultKubernetesServicePort(port, nodePort);
        }

        @Override
        public KubernetesServicePort.Builder port(Integer port) {
            this.port = port;
            return this;
        }

        @Override
        public KubernetesServicePort.Builder nodePort(Integer nodePort) {
            this.nodePort = nodePort;
            return this;
        }
    }
}
