package com.vitor.reservationservice.reservationservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    private final List<Map<String, String>> reservations = new ArrayList<>();

    @Autowired
    public ReservationService() {
    }

    // Método para receber a finalização da reserva após o pagamento
    @RabbitListener(queues = "reservation-finalization-queue")
    public void finalizeReservation(Map<String, Object> finalizationDetails) {
        String clientName = (String) finalizationDetails.get("clientName");
        String roomNumber = (String) finalizationDetails.get("roomNumber");
        String paymentStatus = (String) finalizationDetails.get("paymentStatus");

        // Verifica o status do pagamento antes de finalizar a reserva
        if ("confirmed".equals(paymentStatus)) {
            // Processo para finalizar a reserva
            Map<String, String> reservation = new HashMap<>();
            reservation.put("reservationId", "RES-" + System.currentTimeMillis());
            reservation.put("clientName", clientName);
            reservation.put("roomNumber", roomNumber);
            reservation.put("paymentStatus", paymentStatus);

            reservations.add(reservation);
            System.out.println("Reservation confirmed for room " + roomNumber + " after payment confirmation.");
        } else {
            // Lógica para lidar com pagamentos não confirmados
            System.out.println("Reservation for room " + roomNumber + " was not confirmed due to payment failure.");
        }
    }

    // Métodos auxiliares para gerenciar reservas

    // Retorna todas as reservas
    public List<Map<String, String>> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    // Retorna a reserva para um determinado número de quarto
    public String getReservationByRoom(String roomNumber) {
        for (Map<String, String> reservation : reservations) {
            if (reservation.get("roomNumber").equals(roomNumber)) {
                return reservation.get("reservationId");
            }
        }
        return "There is not a reservation for this room";
    }
}
