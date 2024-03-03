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

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @RabbitListener(queues = "reservation-finalization-queue")
    public void finalizeReservation(Map<String, Object> finalizationDetails) {
        String clientName = (String) finalizationDetails.get("clientName");
        String roomNumber = (String) finalizationDetails.get("roomNumber");
        String paymentMethod = (String) finalizationDetails.get("paymentMethod");
        String paymentStatus = (String) finalizationDetails.get("paymentStatus");

        if ("confirmed".equals(paymentStatus)) {
            Reservation reservation = new Reservation();
            reservation.setClientName(clientName);
            reservation.setRoomNumber(roomNumber);
            reservation.setPaymentMethod(paymentMethod);
            reservation.setReservationDate(LocalDateTime.now());

            reservationRepository.save(reservation);
            System.out.println("Reservation confirmed for room " + roomNumber + " after payment confirmation.");
        } else {
            System.out.println("Reservation for room " + roomNumber + " was not confirmed due to payment failure.");
        }
    }
    
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationByRoom(String roomNumber) {
        List<Reservation> matchingReservations = reservationRepository.findAll().stream()
            .filter(reservation -> reservation.getRoomNumber().equals(roomNumber))
            .collect(Collectors.toList());

        if (!matchingReservations.isEmpty()) {
            return matchingReservations.get(0);
        }
        return null;
    }
}
