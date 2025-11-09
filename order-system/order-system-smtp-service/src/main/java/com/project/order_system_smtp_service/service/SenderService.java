package com.project.order_system_smtp_service.service;

import com.project.order_system_smtp_service.domain.OrderEvent;
import com.project.order_system_smtp_service.domain.SmtpMessage;
import jakarta.mail.MessagingException;

public interface SenderService {

    SmtpMessage sendSync(OrderEvent orderEvent) throws MessagingException;

}
