package com.project.order_system_smtp_service.domain;

import lombok.Builder;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.Map;

@Builder
public record SmtpMessage(

        String to,

        Collection<String> bcc,

        String from,

        String subject,

        String content,

        Boolean asHtml,

        Map<String, Resource> inlineAttachments) {
}