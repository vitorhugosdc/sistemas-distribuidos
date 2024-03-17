package com.vitor.paymentservice.paymentservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private static final String PAYMENT_PROCESSED_SUCCESSFULLY_CREDIT_CARD = "Payment processed successfully with credit card!";
    private static final String PAYMENT_PROCESSED_SUCCESSFULLY_CASH = "Payment processed successfully with cash!";
    private static final String PAYMENT_METHOD_REQUIRED = "Payment method is required.";
    private static final String PAYMENT_METHOD_NOT_IDENTIFIED = "Payment method was not identified!";
    private static final String PAYMENTS_EXCHANGE = "payments-exchange";
    private static final String PAYMENT_CONFIRMATION_ROUTING_KEY = "payment.confirmation";
    private static final String RESERVATIONS_EXCHANGE = "reservations-exchange";
    private static final String FINALIZE_RESERVATION_ROUTING_KEY = "finalize.reservation";
    
    private RabbitTemplate rabbitTemplate;

    public PaymentService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "process-payment-queue")
    public void receivePaymentRequest(Map<String, String> paymentInfo) {
        String clientName = paymentInfo.get("clientName");
        String roomNumber = paymentInfo.get("roomNumber");
        String paymentMethod = paymentInfo.get("paymentMethod");

        String result = processPayment(paymentMethod);

        Map<String, Object> response = new HashMap<>();
        response.put("clientName", clientName);
        response.put("roomNumber", roomNumber);
        response.put("paymentMethod", paymentMethod);
        response.put("paymentStatus", result);

        rabbitTemplate.convertAndSend(PAYMENTS_EXCHANGE, PAYMENT_CONFIRMATION_ROUTING_KEY, response);

        Map<String, Object> finalizationInfo = new HashMap<>();
        finalizationInfo.put("clientName", clientName);
        finalizationInfo.put("roomNumber", roomNumber);
        finalizationInfo.put("paymentMethod", paymentMethod);
        finalizationInfo.put("paymentStatus", result.equals(PAYMENT_PROCESSED_SUCCESSFULLY_CREDIT_CARD) || result.equals(PAYMENT_PROCESSED_SUCCESSFULLY_CASH) ? "confirmed" : "declined");

        rabbitTemplate.convertAndSend(RESERVATIONS_EXCHANGE, FINALIZE_RESERVATION_ROUTING_KEY, finalizationInfo);
    }

    public String processPayment(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return PAYMENT_METHOD_REQUIRED;
        }
        switch (paymentMethod) {
            case "cartao":
                return PAYMENT_PROCESSED_SUCCESSFULLY_CREDIT_CARD;
            case "dinheiro":
                return PAYMENT_PROCESSED_SUCCESSFULLY_CASH;
            default:
                return PAYMENT_METHOD_NOT_IDENTIFIED;
        }
    }
}
