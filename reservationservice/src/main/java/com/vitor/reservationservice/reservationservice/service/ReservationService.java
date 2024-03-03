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

    @RabbitListener(queues = "make-reservation-queue")
    public void receiveReservationRequest(Map<String, String> reservationDetails) {
        String clientName = reservationDetails.get("clientName");
        String roomNumber = reservationDetails.get("roomNumber");
        String paymentMethod = reservationDetails.get("paymentMethod");
        // Aqui, você poderia adicionar lógica para verificar a confirmação de pagamento antes de prosseguir

        // Simula a criação de uma reserva após confirmação de pagamento
        Map<String, String> reservation = makeReservation(clientName, roomNumber, paymentMethod);

        // Em um cenário real, você também publicaria uma mensagem para uma fila para notificar
        // outros serviços de que a reserva foi feita, se necessário
    }

    // Método para criar uma reserva
    public Map<String, String> makeReservation(String clientName, String roomNumber, String paymentMethod) {
        Map<String, String> reservation = new HashMap<>();
        reservation.put("reservationId", "RES-" + System.currentTimeMillis());
        reservation.put("clientName", clientName);
        reservation.put("roomNumber", roomNumber);
        reservation.put("paymentMethod", paymentMethod);

        reservations.add(reservation);
        return reservation;
    }

    // Métodos auxiliares para gerenciar reservas
    public List<Map<String, String>> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public String getReservationByRoom(String roomNumber) {
        for (Map<String, String> reservation : reservations) {
            if (reservation.get("roomNumber").equals(roomNumber)) {
                return reservation.get("reservationId");
            }
        }
        return "There is not a reservation for this room";
    }
}
