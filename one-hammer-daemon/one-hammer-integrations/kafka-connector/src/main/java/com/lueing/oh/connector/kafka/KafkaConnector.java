package com.lueing.oh.connector.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

public class KafkaConnector {
    public static Consumer<String, String> createConsumer(String base, Connector connector, String topic, String group) {
        Properties props = new Properties();
        if (connector.isEnableKerberos()) {
            System.setProperty("java.security.auth.login.config", Paths.get(base, "conf", "kafka-jaas.conf").toString());
            System.setProperty("java.security.krb5.conf", Paths.get(base, "conf", "krb5.conf").toString());
            props.put("security.protocol", "SASL_PLAINTEXT");
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connector.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 我们需要处理大消息
        // 512KB * 8 = 4MB 一次采集的最大消息大小, 相对带宽可以在1s内返回
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 8 * 1024 * 1024);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 8 * 1024 * 1024);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    public static Producer<String, String> createProducer(String base, Connector connector, String txId) {
        Properties props = new Properties();
        if (connector.isEnableKerberos()) {
            System.setProperty("java.security.auth.login.config", Paths.get(base, "conf", "kafka-jaas.conf").toString());
            System.setProperty("java.security.krb5.conf", Paths.get(base, "conf", "krb5.conf").toString());
            props.put("security.protocol", "SASL_PLAINTEXT");
        }
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, connector.getBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        // 事务
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, txId);
        // 性能方面的参数
        // 单次请求最大的bytes: 8L * 1024 * 1024 * 1024 == 8MB
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 8 * 1024 * 1024);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        // 会话期间, 开启事务
        producer.initTransactions();

        return producer;
    }
}
