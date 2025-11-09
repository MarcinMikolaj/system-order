package com.project.order_system_producer_service.producer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.order_system_producer_service.domain.Order;
import com.project.order_system_producer_service.producer.OrderProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProducerImpl implements OrderProducer {

    final KafkaTemplate<Integer, String> kafkaTemplate;

    final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic-name}")
    String topicName;

    @Override
    public void send(Order order) {
        System.out.println("Order created: " + order);
        try {
            sendAsync(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Asynchronous approach
    //@Override
    public CompletableFuture<SendResult<Integer, String>> sendAsync(Order order) throws JsonProcessingException {
        //var key = order.libraryEventId();
        var value = objectMapper.writeValueAsString(order);

        // other way: kafkaTemplate.send(topicName, key, value) etc.;
        var completableFuture = kafkaTemplate.send(buildProducerRecord(topicName, null, value));
        return completableFuture.whenComplete((sendResult, throwable) -> {
            if (Objects.nonNull(throwable)){
                handleFailure(null, value, throwable);
            }else {
                handleSuccess(null, value, sendResult);
            }
        });
    }


    private ProducerRecord<Integer, String> buildProducerRecord(String topicName, Integer key, String value){
        Set<Header> recordHeaders = Set.of(new RecordHeader("even-source", "scanner".getBytes()));
        return new ProducerRecord<>(topicName, null , key , value, recordHeaders);
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult){
        log.info("Message send successful for the key: {}, value: {}, partition: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable throwable){
        log.error("Error occur during send message: {}", throwable.getMessage(), throwable);
    }

}
