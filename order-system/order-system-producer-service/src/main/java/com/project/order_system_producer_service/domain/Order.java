package com.project.order_system_producer_service.domain;


public record Order(
        //@NotBlank
        String shipmentNumber,
        //@Email @NotBlank
        String receiverEmail,
        //@NotBlank @Size(min=2, max=2)
        String receiverCountryCode,
        //@NotBlank @Size(min=2, max=2)
        String senderCountryCode,
        //@Min(0) @Max(100)
        int statusCode) {
}
