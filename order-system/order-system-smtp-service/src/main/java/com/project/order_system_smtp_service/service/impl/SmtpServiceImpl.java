package com.project.order_system_smtp_service.service.impl;

import com.project.order_system_smtp_service.domain.SmtpMessage;
import com.project.order_system_smtp_service.service.SmtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SmtpServiceImpl implements SmtpService {

    JavaMailSender javaMailSender;

    @Override
    public void send(SmtpMessage smtpMessage) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, Boolean.TRUE);
        helper.setTo(smtpMessage.to());
        if (Objects.nonNull(smtpMessage.bcc()))
            helper.setBcc(asArray(smtpMessage.bcc()));
        helper.setSubject(smtpMessage.subject());
        helper.setFrom(smtpMessage.from());
        helper.setText(smtpMessage.content(), smtpMessage.asHtml());
        if(Objects.nonNull(smtpMessage.inlineAttachments())){
            for (Map.Entry<String, Resource> entry : smtpMessage.inlineAttachments().entrySet())
                helper.addInline(entry.getKey(), entry.getValue());
        }
        javaMailSender.send(message);
    }

    private String[] asArray(Collection<String> strings) {
        return strings.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
    }

}