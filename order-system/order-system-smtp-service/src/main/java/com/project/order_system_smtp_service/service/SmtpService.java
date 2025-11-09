package com.project.order_system_smtp_service.service;

import com.project.order_system_smtp_service.domain.SmtpMessage;
import jakarta.mail.MessagingException;

public interface SmtpService {

    void send(SmtpMessage smtpMessage) throws MessagingException;

}
