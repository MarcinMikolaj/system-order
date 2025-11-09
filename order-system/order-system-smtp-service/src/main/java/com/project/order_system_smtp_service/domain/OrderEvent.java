package com.project.order_system_smtp_service.domain;

public record OrderEvent(

        String shipmentNumber,

        String receiverEmail,

        String receiverCountryCode,

        String senderCountryCode,

        int statusCode) {
}
