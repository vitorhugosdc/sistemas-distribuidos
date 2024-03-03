package com.vitor.paymentservice.paymentservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public PaymentService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Método modificado para ser usado com RabbitMQ
    @RabbitListener(queues = "process-payment-queue")
    public void receivePaymentRequest(Map<String, String> paymentInfo) {
        String clientName = paymentInfo.get("clientName");
        String roomNumber = paymentInfo.get("roomNumber");
        String paymentMethod = paymentInfo.get("paymentMethod");

        String result = processPayment(paymentMethod);

        // Preparar a resposta
        Map<String, Object> response = new HashMap<>();
        response.put("clientName", clientName);
        response.put("roomNumber", roomNumber);
        response.put("paymentMethod", paymentMethod);
        response.put("paymentStatus", result);

        // Publicar o resultado do processamento de pagamento
        rabbitTemplate.convertAndSend("payments-exchange", "payment.confirmation", response);
    }

    // Método de processamento de pagamento
    public String processPayment(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return "Payment method is required.";
        }
        switch (paymentMethod) {
            case "cartao":
                return "Payment processed successfully with credit card!";
            case "dinheiro":
                return "Payment processed successfully with cash!";
            default:
                return "Payment method was not identified!";
        }
    }
}
