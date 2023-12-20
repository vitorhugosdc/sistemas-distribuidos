package com.vitor.roomservice.roomservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RoomService {

    private final Map<String, String> reservations = new HashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    public String checkRoomAvailability(String roomNumber) {
        if (reservations.containsKey(roomNumber)) {
            return "Room service: Room " + roomNumber + " is not available for reservation.";
        } else {
            return "Room service: Room " + roomNumber + " is available for reservation.";
        }
    }

    public String makeReservation(String clientName, String roomNumber, String paymentMethod) {
		if (paymentMethod == null || paymentMethod.isEmpty()) {
			return "Room service: Payment method is required for reservation.";
		}
        // Verifique a disponibilidade
        String availabilityResult = checkRoomAvailability(roomNumber);

        if (availabilityResult.contains("Room service: Room " + roomNumber + " is available for reservation.")) {
            // Verifique o pagamento
            String paymentResult = restTemplate.getForObject(
                    "http://localhost:8082/payments/processPayment?method=" + paymentMethod, String.class);

            if (paymentResult.contains("Payment processed successfully!")) {
                // Faça a reserva
                String reservationId = "RES-" + System.currentTimeMillis();
                reservations.put(roomNumber, reservationId);
                return "Room service: Room " + roomNumber + " reserved successfully. Reservation ID: " + reservationId
                        + " | " + paymentResult + " | Reservation completed.";
            } else {
                // Trate falha no pagamento
                return "Room service: Payment failed. " + paymentResult;
            }
        } else {
            // Trate quarto não disponível
            return availabilityResult;
        }
    }

    public String getReservation(String roomNumber) {
        return reservations.get(roomNumber);
    }
}

