package com.project.order_system_smtp_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.order_system_smtp_service.domain.OrderEvent;
import com.project.order_system_smtp_service.service.SenderService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderConsumer {

    final ObjectMapper objectMapper;

    final SenderService senderService;

    @Value("${spring.kafka.topic-name}")
    String topic;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 200, multiplier = 2.0, maxDelay = 5000),
            dltTopicSuffix = ".dlt",
            include = {
                    org.springframework.mail.MailSendException.class,
                    jakarta.mail.MessagingException.class,
                    java.net.SocketTimeoutException.class,
                    javax.net.ssl.SSLException.class,
                    io.github.resilience4j.circuitbreaker.CallNotPermittedException.class
            }
    )
    @KafkaListener(topics = "${spring.kafka.topic-name}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acks) {
        try {
            var orderEvent = objectMapper.readValue(consumerRecord.value(), OrderEvent.class);
            acks.acknowledge();
            log_event_on_successful(consumerRecord, orderEvent);
            senderService.sendSync(orderEvent);
        } catch (MessagingException | JsonProcessingException e) {
            log_event_on_error(consumerRecord, e);
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            log_event_on_error(consumerRecord, e);
            throw e;
        }
    }

    private void log_event_on_successful(ConsumerRecord<Integer, String> consumerRecord, OrderEvent orderEvent) {
        log.info("Processed record topic={}, partition={}, offset={}, order={}",
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                orderEvent);
    }

    private void log_event_on_error(ConsumerRecord<Integer, String> consumerRecord, Exception e) {
        log.error("Error processing record, will be retried. topic={}, partition={}, offset={}, err={}",
                consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                e.toString(),
                e);
    }

}
