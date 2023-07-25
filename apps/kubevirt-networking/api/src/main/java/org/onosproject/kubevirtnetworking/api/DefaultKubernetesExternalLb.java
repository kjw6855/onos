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
import com.google.common.collect.ImmutableSet;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;

import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Default implementation of kubernetes external load balancer.
 */
public final class DefaultKubernetesExternalLb implements KubernetesExternalLb {

    private static final String NOT_NULL_MSG = "External Loadbalancer % cannot be null";

    private final String serviceName;
    private final IpAddress loadbalancerIp;
    private final Set<KubernetesServicePort> servicePorts;
    private final Set<String> endpointSet;
    private final String electedGateway;
    private final IpAddress loadbalancerGwIp;
    private final MacAddress loadbalancerGwMac;
    private final String electedWorker;

    public DefaultKubernetesExternalLb(String serviceName, IpAddress loadbalancerIp,
                                      Set<KubernetesServicePort> servicePorts,
                                       Set<String> endpointSet, String electedGateway,
                                       String electedWorker,
                                       IpAddress loadbalancerGwIp, MacAddress loadbalancerGwMac) {
        this.serviceName = serviceName;
        this.loadbalancerIp = loadbalancerIp;
        this.servicePorts = servicePorts;
        this.endpointSet = endpointSet;
        this.electedGateway = electedGateway;
        this.electedWorker = electedWorker;
        this.loadbalancerGwIp = loadbalancerGwIp;
        this.loadbalancerGwMac = loadbalancerGwMac;
    }

    @Override
    public String serviceName() {
        return serviceName;
    }

    @Override
    public IpAddress loadBalancerIp() {
        return loadbalancerIp;
    }

    @Override
    public Set<KubernetesServicePort> servicePorts() {
        return ImmutableSet.copyOf(servicePorts);
    }

    @Override
    public Set<String> endpointSet() {
        return ImmutableSet.copyOf(endpointSet);
    }

    @Override
    public String electedGateway() {
        return electedGateway;
    }

    @Override
    public String electedWorker() {
        return electedWorker;
    }

    @Override
    public IpAddress loadBalancerGwIp() {
        return loadbalancerGwIp;
    }

    @Override
    public MacAddress loadBalancerGwMac() {
        return loadbalancerGwMac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultKubernetesExternalLb that = (DefaultKubernetesExternalLb) o;
        return serviceName.equals(that.serviceName) && loadbalancerIp.equals(that.loadbalancerIp) &&
                Objects.equals(servicePorts, that.servicePorts) &&
                Objects.equals(endpointSet, that.endpointSet) &&
                Objects.equals(electedGateway, that.electedGateway) &&
                Objects.equals(electedWorker, that.electedWorker) &&
                Objects.equals(loadbalancerGwIp, that.loadbalancerGwIp) &&
                Objects.equals(loadbalancerGwMac, that.loadbalancerGwMac);
    }

    @Override
    public KubernetesExternalLb updateElectedGateway(String electedGateway) {
        return DefaultKubernetesExternalLb.builder()
                .serviceName(serviceName)
                .loadBalancerIp(loadbalancerIp)
                .servicePorts(servicePorts)
                .endpointSet(endpointSet)
                .electedGateway(electedGateway)
                .electedWorker(electedWorker)
                .loadBalancerGwIp(loadbalancerGwIp)
                .loadBalancerGwMac(loadbalancerGwMac)
                .build();
    }

    @Override
    public KubernetesExternalLb updateElectedWorker(String electedWorker) {
        return DefaultKubernetesExternalLb.builder()
                .serviceName(serviceName)
                .loadBalancerIp(loadbalancerIp)
                .servicePorts(servicePorts)
                .endpointSet(endpointSet)
                .electedGateway(electedGateway)
                .electedWorker(electedWorker)
                .loadBalancerGwIp(loadbalancerGwIp)
                .loadBalancerGwMac(loadbalancerGwMac)
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, loadbalancerIp.hashCode());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceName", serviceName)
                .add("loadbalancerIp", loadbalancerIp)
                .add("servucePorts", servicePorts)
                .add("endpointSet", endpointSet)
                .add("electedGateway", electedGateway)
                .add("electedWorker", electedWorker)
                .add("loadbalancer gateway ip", loadbalancerGwIp)
                .add("loadbalancer gateway Mac", loadbalancerGwMac)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements KubernetesExternalLb.Builder {
        private String serviceName;
        private IpAddress loadbalancerIp;
        private Set<KubernetesServicePort> servicePorts;
        private Set<String> endpointSet;
        private String electedGateway;
        private String electedWorker;
        private IpAddress loadbalancerGwip;
        private MacAddress loadbalancerGwMac;

        private Builder() {
        }

        @Override
        public KubernetesExternalLb build() {
            checkArgument(serviceName != null, NOT_NULL_MSG, "serviceName");
            checkArgument(loadbalancerIp != null, NOT_NULL_MSG, "loadbalancerIp");
            checkArgument(!servicePorts.isEmpty(), NOT_NULL_MSG, "servicePorts");

            return new DefaultKubernetesExternalLb(serviceName, loadbalancerIp,
                    servicePorts, endpointSet, electedGateway, electedWorker,
                    loadbalancerGwip, loadbalancerGwMac);
        }

        @Override
        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        @Override
        public Builder loadBalancerIp(IpAddress loadBalancerIp) {
            this.loadbalancerIp = loadBalancerIp;
            return this;
        }

        @Override
        public Builder servicePorts(Set<KubernetesServicePort> servicePorts) {
            this.servicePorts = servicePorts;
            return this;
        }

        @Override
        public Builder endpointSet(Set<String> endpointSet) {
            this.endpointSet = endpointSet;
            return this;
        }

        @Override
        public Builder electedGateway(String electedGateway) {
            this.electedGateway = electedGateway;
            return this;
        }

        @Override
        public Builder electedWorker(String electedWorker) {
            this.electedWorker = electedWorker;
            return this;
        }

        @Override
        public Builder loadBalancerGwIp(IpAddress loadbalancerGwip) {
            this.loadbalancerGwip = loadbalancerGwip;
            return this;
        }

        @Override
        public Builder loadBalancerGwMac(MacAddress loadbalancerGwMac) {
            this.loadbalancerGwMac = loadbalancerGwMac;
            return this;
        }
    }
}
