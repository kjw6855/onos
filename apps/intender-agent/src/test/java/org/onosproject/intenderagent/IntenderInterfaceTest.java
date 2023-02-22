package org.onosproject.intenderagent;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;
import org.onosproject.grpc.net.flow.models.FlowRuleProtoOuterClass.FlowRuleProto;
import org.onosproject.incubator.protobuf.models.net.flow.FlowRuleProtoTranslator;
import org.onosproject.net.flow.FlowRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class IntenderInterfaceTest {
    private static final Logger logger = LoggerFactory.getLogger(
            IntenderInterfaceTest.class);

    @Test
    public void addRuleTest() {
        String dataStr = "entities : [\n" +
                "  # Table FabricEgress.dscp_rewriter.rewriter\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 49970092\n" +
                "      table_name: \"FabricEgress.dscp_rewriter.rewriter\"\n" +
                "      # Match field eg_port\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"eg_port\"\n" +
                "        exact {\n" +
                "          value: \"\\x01\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricEgress.dscp_rewriter.clear\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 24120545\n" +
                "          action_name: \"FabricEgress.dscp_rewriter.clear\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricEgress.egress_next.egress_vlan\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 49262446\n" +
                "      table_name: \"FabricEgress.egress_next.egress_vlan\"\n" +
                "      # Match field eg_port\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"eg_port\"\n" +
                "        exact {\n" +
                "          value: \"\\x01\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field vlan_id\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"vlan_id\"\n" +
                "        exact {\n" +
                "          value: \"\\x00\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricEgress.egress_next.pop_vlan\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 17183246\n" +
                "          action_name: \"FabricEgress.egress_next.pop_vlan\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.acl.acl\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 44104738\n" +
                "      table_name: \"FabricIngress.acl.acl\"\n" +
                "      priority: 1\n" +
                "      # Match field eth_dst\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"eth_dst\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\x7F\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\x7F\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field eth_src\n" +
                "      match {\n" +
                "        field_id: 3\n" +
                "        field_name: \"eth_src\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field eth_type\n" +
                "      match {\n" +
                "        field_id: 5\n" +
                "        field_name: \"eth_type\"\n" +
                "        ternary {\n" +
                "          value: \"\\x88\\x47\"\n" +
                "          mask: \"\\x88\\x47\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field icmp_code\n" +
                "      match {\n" +
                "        field_id: 10\n" +
                "        field_name: \"icmp_code\"\n" +
                "        ternary {\n" +
                "          value: \"\\x00\"\n" +
                "          mask: \"\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field icmp_type\n" +
                "      match {\n" +
                "        field_id: 9\n" +
                "        field_name: \"icmp_type\"\n" +
                "        ternary {\n" +
                "          value: \"\\x00\"\n" +
                "          mask: \"\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ig_port\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"ig_port\"\n" +
                "        ternary {\n" +
                "          value: \"\\x01\\xFF\"\n" +
                "          mask: \"\\x01\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ip_proto\n" +
                "      match {\n" +
                "        field_id: 8\n" +
                "        field_name: \"ip_proto\"\n" +
                "        ternary {\n" +
                "          value: \"\\x06\"\n" +
                "          mask: \"\\x06\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ipv4_dst\n" +
                "      match {\n" +
                "        field_id: 7\n" +
                "        field_name: \"ipv4_dst\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ipv4_src\n" +
                "      match {\n" +
                "        field_id: 6\n" +
                "        field_name: \"ipv4_src\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field l4_dport\n" +
                "      match {\n" +
                "        field_id: 12\n" +
                "        field_name: \"l4_dport\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field l4_sport\n" +
                "      match {\n" +
                "        field_id: 11\n" +
                "        field_name: \"l4_sport\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field port_type\n" +
                "      match {\n" +
                "        field_id: 13\n" +
                "        field_name: \"port_type\"\n" +
                "        ternary {\n" +
                "          value: \"\\x03\"\n" +
                "          mask: \"\\x03\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field vlan_id\n" +
                "      match {\n" +
                "        field_id: 4\n" +
                "        field_name: \"vlan_id\"\n" +
                "        ternary {\n" +
                "          value: \"\\x0F\\xFF\"\n" +
                "          mask: \"\\x0F\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.acl.punt_to_cpu\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 23579892\n" +
                "          action_name: \"FabricIngress.acl.punt_to_cpu\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.filtering.fwd_classifier\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 49718154\n" +
                "      table_name: \"FabricIngress.filtering.fwd_classifier\"\n" +
                "      priority: 1\n" +
                "      # Match field ig_port\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"ig_port\"\n" +
                "        exact {\n" +
                "          value: \"\\x01\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ip_eth_type\n" +
                "      match {\n" +
                "        field_id: 4\n" +
                "        field_name: \"ip_eth_type\"\n" +
                "        exact {\n" +
                "          value: \"\\x08\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field eth_dst\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"eth_dst\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field eth_type\n" +
                "      match {\n" +
                "        field_id: 3\n" +
                "        field_name: \"eth_type\"\n" +
                "        ternary {\n" +
                "          value: \"\\x08\\x47\"\n" +
                "          mask: \"\\x08\\x47\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.filtering.set_forwarding_type\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 25032921\n" +
                "          action_name: \"FabricIngress.filtering.set_forwarding_type\"\n" +
                "          # Param fwd_type\n" +
                "          params {\n" +
                "            param_id: 1\n" +
                "            param_name: \"fwd_type\"\n" +
                "            value: \"\\x00\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.filtering.ingress_port_vlan\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 43310977\n" +
                "      table_name: \"FabricIngress.filtering.ingress_port_vlan\"\n" +
                "      priority: 1\n" +
                "      # Match field ig_port\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"ig_port\"\n" +
                "        exact {\n" +
                "          value: \"\\x01\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field vlan_is_valid\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"vlan_is_valid\"\n" +
                "        exact {\n" +
                "          value: \"\\x01\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field vlan_id\n" +
                "      match {\n" +
                "        field_id: 3\n" +
                "        field_name: \"vlan_id\"\n" +
                "        ternary {\n" +
                "          value: \"\\x0B\\x9D\"\n" +
                "          mask: \"\\x00\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.filtering.permit\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 24158268\n" +
                "          action_name: \"FabricIngress.filtering.permit\"\n" +
                "          # Param port_type\n" +
                "          params {\n" +
                "            param_id: 1\n" +
                "            param_name: \"port_type\"\n" +
                "            value: \"\\x03\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.forwarding.bridging\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 43623757\n" +
                "      table_name: \"FabricIngress.forwarding.bridging\"\n" +
                "      priority: 1\n" +
                "      # Match field vlan_id\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"vlan_id\"\n" +
                "        exact {\n" +
                "          value: \"\\x0F\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field eth_dst\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"eth_dst\"\n" +
                "        ternary {\n" +
                "          value: \"\\x00\\x00\\x3F\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\x00\\x00\\x3F\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.forwarding.set_next_id_bridging\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 21791748\n" +
                "          action_name: \"FabricIngress.forwarding.set_next_id_bridging\"\n" +
                "          # Param next_id\n" +
                "          params {\n" +
                "            param_id: 1\n" +
                "            param_name: \"next_id\"\n" +
                "            value: \"\\x00\\x00\\x00\\x00\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.pre_next.next_vlan\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 48011802\n" +
                "      table_name: \"FabricIngress.pre_next.next_vlan\"\n" +
                "      # Match field next_id\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"next_id\"\n" +
                "        exact {\n" +
                "          value: \"\\x00\\x00\\x00\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.pre_next.set_vlan\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 33475378\n" +
                "          action_name: \"FabricIngress.pre_next.set_vlan\"\n" +
                "          # Param vlan_id\n" +
                "          params {\n" +
                "            param_id: 1\n" +
                "            param_name: \"vlan_id\"\n" +
                "            value: \"\\x00\\x00\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.qos.queues\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 36435258\n" +
                "      table_name: \"FabricIngress.qos.queues\"\n" +
                "      priority: 1\n" +
                "      # Match field slice_id\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"slice_id\"\n" +
                "        exact {\n" +
                "          value: \"\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field tc\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"tc\"\n" +
                "        exact {\n" +
                "          value: \"\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field color\n" +
                "      match {\n" +
                "        field_id: 3\n" +
                "        field_name: \"color\"\n" +
                "        ternary {\n" +
                "          value: \"\\x00\"\n" +
                "          mask: \"\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.qos.meter_drop\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 28214351\n" +
                "          action_name: \"FabricIngress.qos.meter_drop\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  # Table FabricIngress.slice_tc_classifier.classifier\n" +
                "  {\n" +
                "    table_entry {\n" +
                "      table_id: 34606298\n" +
                "      table_name: \"FabricIngress.slice_tc_classifier.classifier\"\n" +
                "      priority: 1\n" +
                "      # Match field ig_port\n" +
                "      match {\n" +
                "        field_id: 1\n" +
                "        field_name: \"ig_port\"\n" +
                "        ternary {\n" +
                "          value: \"\\x00\\x00\"\n" +
                "          mask: \"\\x00\\x00\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ip_proto\n" +
                "      match {\n" +
                "        field_id: 4\n" +
                "        field_name: \"ip_proto\"\n" +
                "        ternary {\n" +
                "          value: \"\\x06\"\n" +
                "          mask: \"\\x06\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ipv4_dst\n" +
                "      match {\n" +
                "        field_id: 3\n" +
                "        field_name: \"ipv4_dst\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field ipv4_src\n" +
                "      match {\n" +
                "        field_id: 2\n" +
                "        field_name: \"ipv4_src\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field l4_dport\n" +
                "      match {\n" +
                "        field_id: 6\n" +
                "        field_name: \"l4_dport\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Match field l4_sport\n" +
                "      match {\n" +
                "        field_id: 5\n" +
                "        field_name: \"l4_sport\"\n" +
                "        ternary {\n" +
                "          value: \"\\xFF\\xFF\"\n" +
                "          mask: \"\\xFF\\xFF\"\n" +
                "        }\n" +
                "      }\n" +
                "      # Action FabricIngress.slice_tc_classifier.trust_dscp\n" +
                "      action {\n" +
                "        action {\n" +
                "          action_id: 25983516\n" +
                "          action_name: \"FabricIngress.slice_tc_classifier.trust_dscp\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "]";
        try {
            FlowRule flowRule = FlowRuleProtoTranslator.translate(FlowRuleProto.parseFrom(dataStr.getBytes(StandardCharsets.UTF_8)));
            assertNotNull(flowRule);
            logger.info(flowRule.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            fail();
        }
    }
}