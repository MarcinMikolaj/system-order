package com.project.order_system_smtp_service.service.impl;

import com.project.order_system_smtp_service.domain.OrderEvent;
import com.project.order_system_smtp_service.domain.SmtpMessage;
import com.project.order_system_smtp_service.service.SenderService;
import com.project.order_system_smtp_service.service.SmtpService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SenderServiceImpl implements SenderService {

    SmtpService smtpService;

    @Override
    @CircuitBreaker(name = "order-group-circuit-breaker")
    public SmtpMessage sendSync(OrderEvent orderEvent) throws MessagingException {
        SmtpMessage smtpMessage = buildSmtpMessage(orderEvent);
        smtpService.send(smtpMessage);
        return smtpMessage;
    }

    private SmtpMessage buildSmtpMessage(OrderEvent orderEvent) {
        return SmtpMessage.builder()
                .to(orderEvent.receiverEmail())
                .from("sender.service@dpd.pl")
                .subject(orderEvent.shipmentNumber())
                .content(orderEvent.toString())
                .asHtml(Boolean.FALSE)
                .build();
    }

}
