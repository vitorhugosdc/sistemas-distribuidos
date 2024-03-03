package com.vitor.roomservice.roomservice.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final Map<String, String> reservations = new HashMap<>();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String checkRoomAvailability(String roomNumber) {
        if (reservations.containsKey(roomNumber)) {
            return "Room service: Room " + roomNumber + " is not available for reservation.";
        } else {
            return "Room service: Room " + roomNumber + " is available for reservation.";
        }
    }

    public String makeReservation(String clientName, String roomNumber, String paymentMethod) {
        String availabilityResult = checkRoomAvailability(roomNumber);
        if (!availabilityResult.contains("available")) {
            return availabilityResult; // Room not available
        }

        // Preparar a mensagem para o serviço de pagamento
        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("clientName", clientName);
        paymentInfo.put("roomNumber", roomNumber);
        paymentInfo.put("paymentMethod", paymentMethod);

        // Publicar mensagem para a fila de processamento de pagamento
        rabbitTemplate.convertAndSend("payments-exchange", "process.payment", paymentInfo);

        // Retorno imediato, assumindo que o processamento será assíncrono
        return "Payment processing initiated. Please wait for confirmation.";
    }
}
