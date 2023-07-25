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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.codec.JsonCodec;
import org.onosproject.codec.impl.MockCodecContext;
import org.onosproject.core.CoreService;
import org.onosproject.kubevirtnode.api.DefaultKubernetesExternalLbInterface;
import org.onosproject.kubevirtnode.api.KubernetesExternalLbInterface;

import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.onosproject.kubevirtnode.codec.KubernetesExternalLbIntfJsonMatcher.matchesKubernetesElbIntf;
import static org.onosproject.net.NetTestTools.APP_ID;

/**
 * Unit tests for KubernetesExternalLbInterface codec.
 */
public class KubernetesExternalLbIntfCodecTest {
    MockCodecContext context;

    private static final String REST_APP_ID = "org.onosproject.rest";
    JsonCodec<KubernetesExternalLbInterface> kubernetesElbIntfCodec;

    final CoreService mockCoreService = createMock(CoreService.class);

    @Before
    public void setUp() {
        context = new MockCodecContext();
        kubernetesElbIntfCodec = new KubernetesExternalLbInterfaceCodec();

        assertThat(kubernetesElbIntfCodec, notNullValue());

        expect(mockCoreService.registerApplication(REST_APP_ID))
                .andReturn(APP_ID).anyTimes();
        replay(mockCoreService);
        context.registerService(CoreService.class, mockCoreService);
    }

    /**
     * Tests encoding.
     */
    @Test
    public void testEncode() {
        KubernetesExternalLbInterface externalLbInterface =
                DefaultKubernetesExternalLbInterface.builder()
                        .externalLbBridgeName("elbnetwork")
                        .externalLbIp(IpAddress.valueOf("10.10.10.2"))
                        .externallbGwIp(IpAddress.valueOf("10.10.10.1"))
                        .externalLbGwMac(MacAddress.valueOf("AA:BB:CC:DD:EE:FF"))
                        .build();

        ObjectNode nodeJson = kubernetesElbIntfCodec.encode(externalLbInterface, context);
        assertThat(nodeJson, matchesKubernetesElbIntf(externalLbInterface));
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecode() throws IOException {
        KubernetesExternalLbInterface externalLbInterface = getElbIntf("KubernetesExternalLbIntf.json");

        assertEquals("elbnet", externalLbInterface.externalLbBridgeName());
        assertEquals("10.10.10.2", externalLbInterface.externalLbIp().toString());
        assertEquals("10.10.10.1", externalLbInterface.externalLbGwIp().toString());
        assertEquals("AA:BB:CC:DD:EE:FF", externalLbInterface.externalLbGwMac().toString());
    }

    private KubernetesExternalLbInterface getElbIntf(String resourceName) throws IOException {
        InputStream jsonStream = KubernetesExternalLbIntfCodecTest.class.getResourceAsStream(resourceName);
        JsonNode json = context.mapper().readTree(jsonStream);
        MatcherAssert.assertThat(json, notNullValue());

        KubernetesExternalLbInterface externalLbInterface = kubernetesElbIntfCodec.decode(
                (ObjectNode) json, context);
        assertThat(externalLbInterface, notNullValue());

        return externalLbInterface;
    }

}
