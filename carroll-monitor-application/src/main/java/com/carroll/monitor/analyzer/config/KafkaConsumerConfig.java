package com.carroll.monitor.analyzer.config;

import com.carroll.monitor.common.dto.KafkaTopic;
import com.carroll.monitor.data.collector.config.MonitorKafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka消费者配置
 *
 * @author: carroll
 * @date 2019/9/9
 **/
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Autowired
    private MonitorKafkaConfig kafkaConfig;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        Map<String, Object> config= consumerFactoryConfig();
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroup());
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(config));
        factory.getContainerProperties().setPollTimeout(kafkaConfig.getPollTimeout());
        factory.setConcurrency(kafkaConfig.getConcurrencey());
        return factory;
    }

    @Bean("timeContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory2() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        Map<String, Object> config= consumerFactoryConfig();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaTopic.MONITOR_TIME_GROUP);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(config));
        factory.getContainerProperties().setPollTimeout(kafkaConfig.getPollTimeout());
        factory.setConcurrency(kafkaConfig.getConcurrencey());
        return factory;
    }

    public Map<String, Object> consumerFactoryConfig() {
        Map<String, Object> properties = new HashMap();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokers());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaConfig.getAutoCommitIntervalMs());
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConfig.getSessionTimeoutMs());
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConfig.getHeartbeatIntervalMs());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroup());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset());
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConfig.getMaxPollRecords());
        return properties;
    }
}
