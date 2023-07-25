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
package org.onosproject.kubevirtnode.codec;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.kubevirtnode.api.DefaultKubernetesExternalLbInterface;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbInterface;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.onlab.util.Tools.nullIsIllegal;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Kubernetes external load balancer interface codec used for serializing and de-serializing JSON string.
 */
public class KubernetesExternalLbInterfaceCodec extends JsonCodec<KubernetesExternalLbInterface> {
    private final Logger log = getLogger(getClass());

    private static final String ELB_BRIDGE_NAME = "externalLbBridgeName";
    private static final String ELB_IP = "externalLbIp";
    private static final String ELB_GW_IP = "externalLbGwIp";
    private static final String ELB_GW_MAC = "externalLbGwMac";

    private static final String MISSING_MESSAGE = " is required in KubernetesExternalLbInterfaceCodec";

    @Override
    public ObjectNode encode(KubernetesExternalLbInterface externalLbInterface, CodecContext context) {
        checkNotNull(externalLbInterface, "checkNotNull cannot be null");

        ObjectNode result = context.mapper().createObjectNode()
                .put(ELB_BRIDGE_NAME, externalLbInterface.externalLbBridgeName())
                .put(ELB_IP, externalLbInterface.externalLbIp().toString())
                .put(ELB_GW_IP, externalLbInterface.externalLbGwIp().toString());

        if (externalLbInterface.externalLbGwMac() != null) {
            result.put(ELB_GW_MAC, externalLbInterface.externalLbGwMac().toString());
        }
        return result;
    }

    @Override
    public KubernetesExternalLbInterface decode(ObjectNode json, CodecContext context) {
        if (json == null || !json.isObject()) {
            return null;
        }

        String elbBridgeName = nullIsIllegal(json.get(ELB_BRIDGE_NAME).asText(),
                ELB_BRIDGE_NAME + MISSING_MESSAGE);

        String elbIp = nullIsIllegal(json.get(ELB_IP).asText(),
                ELB_IP + MISSING_MESSAGE);

        String elbGwIp = nullIsIllegal(json.get(ELB_GW_IP).asText(),
                ELB_GW_IP + MISSING_MESSAGE);

        KubernetesExternalLbInterface.Builder externalLbInterfaceBuilder = DefaultKubernetesExternalLbInterface
                .builder()
                .externalLbBridgeName(elbBridgeName)
                .externallbGwIp(IpAddress.valueOf(elbGwIp))
                .externalLbIp(IpAddress.valueOf(elbIp));

        if (json.get(ELB_GW_MAC) != null) {
            externalLbInterfaceBuilder.externalLbGwMac(MacAddress.valueOf(json.get(ELB_GW_MAC).asText()));
        }

        return externalLbInterfaceBuilder.build();
    }
}
