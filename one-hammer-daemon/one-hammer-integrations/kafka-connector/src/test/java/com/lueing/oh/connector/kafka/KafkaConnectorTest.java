package com.lueing.oh.connector.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;

@Ignore
public class KafkaConnectorTest {
    @Test
    public void testProducer() {
        Producer<String, String> producer = KafkaConnector.createProducer("",
                Connector.builder().enableKerberos(false).bootstrapServers("192.168.0.16:9092").build(), "tx-01");
//        StringBuilder builder = new StringBuilder();

//        for (int i = 0; i < 8 * 1024 * 1024; i++) {
//            builder.append('a');
//        }

        producer.beginTransaction();
        for (int i = 0; i < 64; i++) {
            producer.send(new ProducerRecord<>("topic-1", "k-" + i, "v-1-1"));
        }
//        producer.send(new ProducerRecord<>("topic-1", "k-1", "v-1-1"));
//        producer.send(new ProducerRecord<>("topic-1", "k-2", "v-2-2"));
//        producer.send(new ProducerRecord<>("topic-1", "k-3", builder.toString()));
        producer.commitTransaction();
    }

    @Test
    public void testConsumer() {
        Consumer<String, String> consumer = KafkaConnector.createConsumer("", Connector.builder().enableKerberos(false)
                .bootstrapServers("192.168.0.16:9092").txId("tx-01").build(), "topic-1", "group-1");

        for (int i = 0; i < 8; i++) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

            System.out.println("....." + i);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.key() + ": " + record.value());
            }
            consumer.commitSync();
        }
    }
}
