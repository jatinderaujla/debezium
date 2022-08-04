/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.compaction.tool.service.compaction;

import io.debezium.config.Configuration;
import io.debezium.connector.mysql.MySqlOffsetContext;
import io.debezium.kafka.KafkaCluster;
import io.debezium.pipeline.spi.Offsets;
import io.debezium.pipeline.spi.Partition;
import io.debezium.util.Collect;
import io.debezium.util.Testing;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

class KafkaDatabaseHistoryCompactionTest {

    private static KafkaCluster kafka;
    private Offsets<Partition, MySqlOffsetContext> offsets;
    private MySqlOffsetContext position;

    private static final int PARTITION_NO = 0;

    @BeforeClass
    public static void startKafka() throws Exception {
        File dataDir = Testing.Files.createTestingDirectory("history_cluster");
        Testing.Files.delete(dataDir);

        // Configure the extra properties to
        kafka = new KafkaCluster().usingDirectory(dataDir)
                .deleteDataPriorToStartup(true)
                .deleteDataUponShutdown(true)
                .addBrokers(1)
                .withKafkaConfiguration(Collect.propertiesOf(
                        "auto.create.topics.enable", "false",
                        "zookeeper.session.timeout.ms", "20000"))
                .startup();
    }

    @AfterClass
    public static void stopKafka() {
        if (kafka != null) {
            kafka.shutdown();
        }
    }

    @Test
    void testConfiguration() {
        KafkaDatabaseHistoryCompaction databaseHistoryCompaction = new KafkaDatabaseHistoryCompaction("", "", "");

        Configuration dbHistoryConfig = Configuration.create().build();

        databaseHistoryCompaction.configure(dbHistoryConfig, null, DatabaseHistoryListener.NOOP, false);

    }
}
