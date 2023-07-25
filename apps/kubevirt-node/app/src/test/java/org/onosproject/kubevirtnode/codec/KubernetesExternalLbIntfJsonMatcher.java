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

import com.fasterxml.jackson.databind.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbInterface;

/**
 * Hamcrest matcher for KubernetesExternalLbInterface.
 */
public final class KubernetesExternalLbIntfJsonMatcher extends TypeSafeDiagnosingMatcher<JsonNode> {

    private final KubernetesExternalLbInterface externalLbInterface;

    private static final String ELB_BRIDGE_NAME = "externalLbBridgeName";
    private static final String ELB_IP = "externalLbIp";
    private static final String ELB_GW_IP = "externalLbGwIp";
    private static final String ELB_GW_MAC = "externalLbGwMac";

    private KubernetesExternalLbIntfJsonMatcher(KubernetesExternalLbInterface externalLbInterface) {
        this.externalLbInterface = externalLbInterface;
    }

    @Override
    protected boolean matchesSafely(JsonNode jsonNode, Description description) {
        // check externalLbBridgeName
        String jsonElbBridgeName = jsonNode.get(ELB_BRIDGE_NAME).asText();
        String elbBridgeName = externalLbInterface.externalLbBridgeName();
        if (!jsonElbBridgeName.equals(elbBridgeName)) {
            description.appendText("externalLbBridgeName was " + elbBridgeName);
            return false;
        }

        // check externalLbIp
        String jsonElbIp = jsonNode.get(ELB_IP).asText();
        String elbIp = externalLbInterface.externalLbIp().toString();
        if (!jsonElbIp.equals(elbIp)) {
            description.appendText("externalLbIp was " + elbIp);
            return false;
        }

        // check externalLbGwIp
        String jsonElbGwIp = jsonNode.get(ELB_GW_IP).asText();
        String elbGwIp = externalLbInterface.externalLbGwIp().toString();
        if (!jsonElbGwIp.equals(elbGwIp)) {
            description.appendText("externalLbGwIp was " + elbGwIp);
            return false;
        }

        // check externalLbGwMac
        String jsonElbGwMac = jsonNode.get(ELB_GW_MAC).asText();
        String elbGwMac = externalLbInterface.externalLbGwMac().toString();
        if (!jsonElbGwMac.equals(elbGwMac)) {
            description.appendText("externalLbGwMac was " + elbGwMac);
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(externalLbInterface.toString());
    }

    /**
     * Factory to allocate a kubernetes external lb interface matcher.
     *
     * @param externalLbInterface kubernetes external lb interface we are looking for
     * @return matcher
     */
    public static KubernetesExternalLbIntfJsonMatcher matchesKubernetesElbIntf(
            KubernetesExternalLbInterface externalLbInterface) {
        return new KubernetesExternalLbIntfJsonMatcher(externalLbInterface);
    }
}
