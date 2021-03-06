/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
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
package io.zeebe.broker.it.clustering;

import static org.assertj.core.api.Assertions.assertThat;

import io.zeebe.broker.it.GrpcClientRule;
import io.zeebe.client.api.commands.BrokerInfo;
import io.zeebe.client.api.commands.PartitionBrokerRole;
import io.zeebe.client.api.commands.Topology;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.Timeout;

public class TopologyClusterTest {

  public Timeout testTimeout = Timeout.seconds(60);
  public ClusteringRule clusteringRule = new ClusteringRule();
  public GrpcClientRule clientRule = new GrpcClientRule(clusteringRule);

  @Rule
  public RuleChain ruleChain =
      RuleChain.outerRule(testTimeout).around(clusteringRule).around(clientRule);

  @Test
  public void shouldContainAllBrokers() {

    // when
    final Topology topology = clientRule.getClient().newTopologyRequest().send().join();

    // then
    final List<BrokerInfo> brokers = topology.getBrokers();

    assertThat(brokers.size()).isEqualTo(3);
    assertThat(brokers)
        .extracting(brokerInfo -> brokerInfo.getNodeId())
        .containsExactlyInAnyOrder(0, 1, 2);
  }

  @Test
  public void shouldContainAllPartitions() {
    // when
    final Topology topology = clientRule.getClient().newTopologyRequest().send().join();

    // then
    final List<BrokerInfo> brokers = topology.getBrokers();

    assertThat(brokers)
        .flatExtracting(brokerInfo -> brokerInfo.getPartitions())
        .filteredOn(p -> p.isLeader())
        .extracting(partitionInfos -> partitionInfos.getPartitionId())
        .containsExactlyInAnyOrder(0, 1, 2);

    assertPartitionInTopology(brokers, 0);
    assertPartitionInTopology(brokers, 1);
    assertPartitionInTopology(brokers, 2);
  }

  public void assertPartitionInTopology(List<BrokerInfo> brokers, int partition) {
    assertThat(brokers)
        .flatExtracting(brokerInfo -> brokerInfo.getPartitions())
        .filteredOn(p -> p.getPartitionId() == partition)
        .extracting(partitionInfos -> partitionInfos.getRole())
        .containsExactlyInAnyOrder(
            PartitionBrokerRole.LEADER, PartitionBrokerRole.FOLLOWER, PartitionBrokerRole.FOLLOWER);
  }

  @Test
  public void shouldExposeClusterSettings() {
    // when
    final Topology topology = clientRule.getClient().newTopologyRequest().send().join();

    // then
    assertThat(topology.getClusterSize()).isEqualTo(clusteringRule.getClusterSize());
    assertThat(topology.getPartitionsCount()).isEqualTo(clusteringRule.getPartitionCount());
    assertThat(topology.getReplicationFactor()).isEqualTo(clusteringRule.getReplicationFactor());
  }
}
