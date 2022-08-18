/*
 * Copyright 2014-present Open Networking Foundation
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
package org.onosproject.net.flow;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.GroupId;
import org.onosproject.net.DeviceId;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.net.flow.TableId.Type.INDEX;

/**
 * Default flow rule.
 */
public class DefaultFlowRule implements FlowRule {

    private final DeviceId deviceId;
    private final int priority;
    private final TrafficSelector selector;
    private final TrafficTreatment treatment;
    private final long created;

    private final FlowId id;

    private final Short appId;

    private final int timeout;
    private final boolean permanent;
    private final int hardTimeout;
    private final FlowRemoveReason reason;
    private final GroupId groupId;

    private final TableId tableId;

    private final int verifyPortId;
    private final int verifyRuleId;

    /**
     * Creates a new flow rule from an existing rule.
     *
     * @param rule new flow rule
     */
    public DefaultFlowRule(FlowRule rule) {
        this.deviceId = rule.deviceId();
        this.priority = rule.priority();
        this.selector = rule.selector();
        this.treatment = rule.treatment();
        this.appId = rule.appId();
        this.groupId = rule.groupId();
        this.id = rule.id();
        this.timeout = rule.timeout();
        this.hardTimeout = rule.hardTimeout();
        this.reason = rule.reason();
        this.permanent = rule.isPermanent();
        this.created = System.currentTimeMillis();
        this.tableId = rule.table();
        this.verifyPortId = rule.verifyPortId();
        this.verifyRuleId = rule.verifyRuleId();
    }

    private DefaultFlowRule(DeviceId deviceId, TrafficSelector selector,
                            TrafficTreatment treatment, Integer priority,
                            FlowId flowId, Boolean permanent, Integer timeout, Integer hardTimeout,
                            FlowRemoveReason reason, TableId tableId,
                            int verifyPortId, int verifyRuleId) {

        this.deviceId = deviceId;
        this.selector = selector;
        this.treatment = treatment;
        this.priority = priority;
        this.appId = (short) (flowId.value() >>> 48);
        this.id = flowId;
        this.permanent = permanent;
        this.timeout = timeout;
        this.hardTimeout = hardTimeout;
        this.reason = reason;
        this.tableId = tableId;
        this.created = System.currentTimeMillis();
        this.verifyPortId = verifyPortId;
        this.verifyRuleId = verifyRuleId;


        //FIXME: fields below will be removed.
        this.groupId = new GroupId(0);
    }

    @Override
    public FlowId id() {
        return id;
    }

    @Override
    public short appId() {
        return appId;
    }

    @Override
    public GroupId groupId() {
        return groupId;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public DeviceId deviceId() {
        return deviceId;
    }

    @Override
    public TrafficSelector selector() {
        return selector;
    }

    @Override
    public TrafficTreatment treatment() {
        return treatment;
    }

    /*
     * The priority and statistics can change on a given treatment and selector
     *
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public int hashCode() {
        return Objects.hash(deviceId, selector, tableId);
    }

    //FIXME do we need this method in addition to hashCode()?
    private int hash() {
        return Objects.hash(deviceId, selector, tableId);
    }

    /*
     * The priority and statistics can change on a given treatment and selector
     *
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultFlowRule) {
            DefaultFlowRule that = (DefaultFlowRule) obj;
            return Objects.equals(deviceId, that.deviceId) &&
                    Objects.equals(priority, that.priority) &&
                    Objects.equals(selector, that.selector) &&
                    Objects.equals(tableId, that.tableId);
        }
        return false;
    }

    @Override
    public boolean exactMatch(FlowRule rule) {
        return this.equals(rule) &&
                Objects.equals(this.id, rule.id()) &&
                Objects.equals(this.treatment, rule.treatment());
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", Long.toHexString(id.value()))
                .add("deviceId", deviceId)
                .add("priority", priority)
                .add("selector", selector.criteria())
                .add("treatment", treatment == null ? "N/A" : treatment)
                .add("tableId", tableId)
                .add("created", created)
                .toString();
    }

    @Override
    public int timeout() {
        return timeout;
    }

    @Override
    public int hardTimeout() {
        return hardTimeout;
    }

    @Override
    public FlowRemoveReason reason() {
        return reason;
    }

    @Override
    public boolean isPermanent() {
        return permanent;
    }

    @Override
    public int tableId() {
        // Workaround until we remove this method. Deprecated in Loon.
        return tableId.type() == INDEX ? ((IndexTableId) tableId).id() : tableId.hashCode();
    }

    @Override
    public TableId table() {
        return tableId;
    }

    @Override
    public int verifyPortId() {
        return verifyPortId;
    }

    @Override
    public int verifyRuleId() {
        return verifyRuleId;
    }

    /**
     * Returns the wallclock time that the flow was created.
     *
     * @return creation time in milliseconds since epoch
     */
    public long created() {
        return created;
    }

    /**
     * Returns a default flow rule builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Default flow rule builder.
     */
    public static final class Builder implements FlowRule.Builder {

        private FlowId flowId;
        private ApplicationId appId;
        private Integer priority;
        private DeviceId deviceId;
        private TableId tableId = DEFAULT_TABLE;
        private TrafficSelector selector = DefaultTrafficSelector.builder().build();
        private TrafficTreatment treatment = DefaultTrafficTreatment.builder().build();
        private Integer timeout;
        private Boolean permanent;
        private Integer hardTimeout = 0;
        private FlowRemoveReason reason = FlowRemoveReason.NO_REASON;
        private int verifyPortId = 0;       // 32 bit
        private int verifyRuleId = 0;       // 16 bit

        @Override
        public FlowRule.Builder withCookie(long cookie) {
            this.flowId = FlowId.valueOf(cookie);
            return this;
        }

        @Override
        public FlowRule.Builder fromApp(ApplicationId appId) {
            this.appId = appId;
            return this;
        }

        @Override
        public FlowRule.Builder withVerifyPortId(int verifyPortId) {
            this.verifyPortId = verifyPortId;
            return this;
        }

        @Override
        public FlowRule.Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public FlowRule.Builder forDevice(DeviceId deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        @Override
        public FlowRule.Builder forTable(int tableId) {
            this.tableId = IndexTableId.of(tableId);
            return this;
        }

        @Override
        public FlowRule.Builder forTable(TableId tableId) {
            this.tableId = tableId;
            return this;
        }

        @Override
        public FlowRule.Builder withSelector(TrafficSelector selector) {
            this.selector = selector;
            return this;
        }

        @Override
        public FlowRule.Builder withTreatment(TrafficTreatment treatment) {
            this.treatment = checkNotNull(treatment);
            return this;
        }

        @Override
        public FlowRule.Builder makePermanent() {
            this.timeout = 0;
            this.permanent = true;
            return this;
        }

        @Override
        public FlowRule.Builder makeTemporary(int timeout) {
            this.permanent = false;
            this.timeout = timeout;
            return this;
        }

        @Override
        public FlowRule.Builder withHardTimeout(int timeout) {
            this.permanent = false;
            this.hardTimeout = timeout;
            this.timeout = timeout;
            return this;
        }

        @Override
        public FlowRule.Builder withReason(FlowRemoveReason reason) {
            this.reason = reason;
            return this;
        }

        @Override
        public FlowRule build() {
            FlowId localFlowId;
            checkNotNull(tableId, "Table id cannot be null");
            checkArgument((flowId != null) ^ (appId != null), "Either an application" +
                    " id or a cookie must be supplied");
            checkNotNull(selector, "Traffic selector cannot be null");
            checkArgument(timeout != null || permanent != null, "Must either have " +
                    "a timeout or be permanent");
            checkNotNull(deviceId, "Must refer to a device");
            checkNotNull(priority, "Priority cannot be null");
            checkArgument(priority >= MIN_PRIORITY, "Priority cannot be less than " +
                    MIN_PRIORITY);
            checkArgument(priority <= MAX_PRIORITY, "Priority cannot be greater than " +
                    MAX_PRIORITY);
            // Computing a flow ID based on appId takes precedence over setting
            // the flow ID directly
            if (appId != null) {
                localFlowId = computeFlowId(appId);
            } else {
                localFlowId = flowId;
            }

            if (verifyPortId != 0) {
                verifyRuleId = crc16() & 0xffff;
            }

            return new DefaultFlowRule(deviceId, selector, treatment, priority,
                                       localFlowId, permanent, timeout, hardTimeout, reason, tableId,
                                       verifyPortId, verifyRuleId);
        }

        private FlowId computeFlowId(ApplicationId appId) {
            return FlowId.valueOf((((long) appId.id()) << 48)
                                   | (hash() & 0xffffffffL));
        }

        private int crc16() {
            CRC16 crc16 = new CRC16();

            selector.criteria().forEach(c -> crc16.update(c.toString().getBytes()));
            crc16.update(ByteBuffer.allocate(4).putInt(priority).array());

            return crc16.value;
        }

        private int hash() {
            // Guava documentation recommends using putUnencodedChars to hash raw character bytes within any encoding
            // unless cross-language compatibility is needed. See the Hasher.putString documentation for more info.
            Funnel<TrafficSelector> selectorFunnel = (from, into) -> from.criteria()
                    .forEach(c -> into.putUnencodedChars(c.toString()));

            HashFunction hashFunction = Hashing.murmur3_32();
            HashCode hashCode = hashFunction.newHasher()
                    .putUnencodedChars(deviceId.toString())
                    .putObject(selector, selectorFunnel)
                    .putInt(priority)
                    .putUnencodedChars(tableId.toString())
                    .hash();

            return hashCode.asInt();
        }
    }

    public static class CRC16 {

        private int[] crc16tab = {
            0x0000,0x1021,0x2042,0x3063,0x4084,0x50a5,0x60c6,0x70e7,
            0x8108,0x9129,0xa14a,0xb16b,0xc18c,0xd1ad,0xe1ce,0xf1ef,
            0x1231,0x0210,0x3273,0x2252,0x52b5,0x4294,0x72f7,0x62d6,
            0x9339,0x8318,0xb37b,0xa35a,0xd3bd,0xc39c,0xf3ff,0xe3de,
            0x2462,0x3443,0x0420,0x1401,0x64e6,0x74c7,0x44a4,0x5485,
            0xa56a,0xb54b,0x8528,0x9509,0xe5ee,0xf5cf,0xc5ac,0xd58d,
            0x3653,0x2672,0x1611,0x0630,0x76d7,0x66f6,0x5695,0x46b4,
            0xb75b,0xa77a,0x9719,0x8738,0xf7df,0xe7fe,0xd79d,0xc7bc,
            0x48c4,0x58e5,0x6886,0x78a7,0x0840,0x1861,0x2802,0x3823,
            0xc9cc,0xd9ed,0xe98e,0xf9af,0x8948,0x9969,0xa90a,0xb92b,
            0x5af5,0x4ad4,0x7ab7,0x6a96,0x1a71,0x0a50,0x3a33,0x2a12,
            0xdbfd,0xcbdc,0xfbbf,0xeb9e,0x9b79,0x8b58,0xbb3b,0xab1a,
            0x6ca6,0x7c87,0x4ce4,0x5cc5,0x2c22,0x3c03,0x0c60,0x1c41,
            0xedae,0xfd8f,0xcdec,0xddcd,0xad2a,0xbd0b,0x8d68,0x9d49,
            0x7e97,0x6eb6,0x5ed5,0x4ef4,0x3e13,0x2e32,0x1e51,0x0e70,
            0xff9f,0xefbe,0xdfdd,0xcffc,0xbf1b,0xaf3a,0x9f59,0x8f78,
            0x9188,0x81a9,0xb1ca,0xa1eb,0xd10c,0xc12d,0xf14e,0xe16f,
            0x1080,0x00a1,0x30c2,0x20e3,0x5004,0x4025,0x7046,0x6067,
            0x83b9,0x9398,0xa3fb,0xb3da,0xc33d,0xd31c,0xe37f,0xf35e,
            0x02b1,0x1290,0x22f3,0x32d2,0x4235,0x5214,0x6277,0x7256,
            0xb5ea,0xa5cb,0x95a8,0x8589,0xf56e,0xe54f,0xd52c,0xc50d,
            0x34e2,0x24c3,0x14a0,0x0481,0x7466,0x6447,0x5424,0x4405,
            0xa7db,0xb7fa,0x8799,0x97b8,0xe75f,0xf77e,0xc71d,0xd73c,
            0x26d3,0x36f2,0x0691,0x16b0,0x6657,0x7676,0x4615,0x5634,
            0xd94c,0xc96d,0xf90e,0xe92f,0x99c8,0x89e9,0xb98a,0xa9ab,
            0x5844,0x4865,0x7806,0x6827,0x18c0,0x08e1,0x3882,0x28a3,
            0xcb7d,0xdb5c,0xeb3f,0xfb1e,0x8bf9,0x9bd8,0xabbb,0xbb9a,
            0x4a75,0x5a54,0x6a37,0x7a16,0x0af1,0x1ad0,0x2ab3,0x3a92,
            0xfd2e,0xed0f,0xdd6c,0xcd4d,0xbdaa,0xad8b,0x9de8,0x8dc9,
            0x7c26,0x6c07,0x5c64,0x4c45,0x3ca2,0x2c83,0x1ce0,0x0cc1,
            0xef1f,0xff3e,0xcf5d,0xdf7c,0xaf9b,0xbfba,0x8fd9,0x9ff8,
            0x6e17,0x7e36,0x4e55,0x5e74,0x2e93,0x3eb2,0x0ed1,0x1ef0
        };

        /** value contains the currently computed CRC, set it to 0 initally */
        public int value;

        public CRC16() {
            value = 0;
        }

        private int crc16_ccitt(byte[] buf)
        {
            int crc = 0;
            for (int counter = 0; counter < buf.length; counter++){
                crc = (crc<<8) ^ crc16tab[((crc>>8) ^ buf[counter])&0x00FF];
            }

            return crc & 0xFFFF;
        }

        public void update(byte[] buf){
            value = crc16_ccitt(buf) ^ value;
        }

        /** reset CRC value to 0 */
        public void reset() {
            value = 0;
        }
    }
}
