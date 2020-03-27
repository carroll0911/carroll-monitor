package com.carroll.monitor.data.collector.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka生产者配置
 *
 * @author: carroll
 * @date 2019/9/9
 **/
@Configuration
@EnableKafka
public class MonitorKafkaProducerConfig {
    @Autowired
    private MonitorKafkaConfig kafkaConfig;

    @Bean("monitorKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        if (StringUtils.isEmpty(kafkaConfig.getBrokers())) {
            return null;
        }
        return new KafkaTemplate(producerFactory());
    }

    public ProducerFactory<String, String> producerFactory() {

        Map<String, Object> properties = new HashMap();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokers());
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfig.getBachSize());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, kafkaConfig.getLingerMs());
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaConfig.getBufferMemory());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, kafkaConfig.getMaxBlockMs());
        return new DefaultKafkaProducerFactory(properties);
    }
}
