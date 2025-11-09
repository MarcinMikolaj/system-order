package com.project.order_system_producer_service.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConfig {

    @Value("${spring.kafka.topic-name}")
    String topicName;

    @Bean
    public NewTopic buildEventListenerTopic(){
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .build();
    }

}
