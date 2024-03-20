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

    public PaymentService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "process-payment-queue")
    //2. Método longo (Long method)
    public void receivePaymentRequest(Map<String, String> paymentInfo) {
    	//3. Ausência de validação (Missing Checks)
        String clientName = paymentInfo.get("clientName");
        String roomNumber = paymentInfo.get("roomNumber");
        String paymentMethod = paymentInfo.get("paymentMethod");

        String result = processPayment(paymentMethod);

        Map<String, Object> response = new HashMap<>();
        response.put("clientName", clientName);
        response.put("roomNumber", roomNumber);
        response.put("paymentMethod", paymentMethod);
        response.put("paymentStatus", result);

        rabbitTemplate.convertAndSend("payments-exchange", "payment.confirmation", response);

        Map<String, Object> finalizationInfo = new HashMap<>();
        finalizationInfo.put("clientName", clientName);
        finalizationInfo.put("roomNumber", roomNumber);
        finalizationInfo.put("paymentMethod", paymentMethod);
        //1. Hardcoding de Strings (Magic numbers/strings)
        finalizationInfo.put("paymentStatus", result.equals("Payment processed successfully with credit card!") || result.equals("Payment processed successfully with cash!") ? "confirmed" : "declined");
        //4. O método interage diretamente com o template (Dispensers)
        rabbitTemplate.convertAndSend("reservations-exchange", "finalize.reservation", finalizationInfo);
    }

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
