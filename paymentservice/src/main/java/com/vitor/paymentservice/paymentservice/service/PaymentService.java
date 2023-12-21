package com.vitor.paymentservice.paymentservice.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

	public String processPayment(String paymentMethod) {

		if (paymentMethod == null || paymentMethod.isEmpty()) {
			return "Payment method is required.";
		}

		if ("cartao".equals(paymentMethod)) {
			return "Payment processed successfully with credit card!";
		} else if ("dinheiro".equals(paymentMethod)) {
			return "Payment processed successfully with cash!";
		} else {
			return "Payment method was not identified!";
		}
	}
}
