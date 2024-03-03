package com.vitor.reservationservice.reservationservice.service;

import com.vitor.reservationservice.reservationservice.model.Reservation;
import com.vitor.reservationservice.reservationservice.repository.ReservationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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
            Reservation reservation = new Reservation();
            reservation.setClientName(clientName);
            reservation.setRoomNumber(roomNumber);
            reservation.setPaymentMethod("unknown"); // Ajuste conforme necessário
            reservation.setReservationDate(LocalDateTime.now());

            reservationRepository.save(reservation);
            System.out.println("Reservation confirmed for room " + roomNumber + " after payment confirmation.");
        } else {
            // Lógica para lidar com pagamentos não confirmados
            System.out.println("Reservation for room " + roomNumber + " was not confirmed due to payment failure.");
        }
    }

    // Métodos auxiliares para gerenciar reservas

    // Retorna todas as reservas
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Retorna a reserva para um determinado número de quarto
    public String getReservationByRoom(String roomNumber) {
        List<Reservation> matchingReservations = reservationRepository.findAll().stream()
            .filter(reservation -> reservation.getRoomNumber().equals(roomNumber))
            .collect(Collectors.toList());

        if (!matchingReservations.isEmpty()) {
            // Assumindo que pode haver mais de uma reserva por quarto,
            // esta chamada obtém a primeira. Ajuste conforme necessário.
            return matchingReservations.get(0).getId().toString();
        }
        return "There is not a reservation for this room";
    }
}
