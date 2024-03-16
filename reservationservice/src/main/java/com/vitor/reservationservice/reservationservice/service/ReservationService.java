package com.vitor.reservationservice.reservationservice.service;

import com.vitor.reservationservice.reservationservice.model.Reservation;
import com.vitor.reservationservice.reservationservice.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @RabbitListener(queues = "reservation-finalization-queue")
    public void finalizeReservation(Map<String, Object> finalizationDetails) {
        if (!isValidFinalizationDetails(finalizationDetails)) {
            logger.error("Invalid reservation details");
            return;
        }
        String paymentStatus = (String) finalizationDetails.get("paymentStatus");
        
        if (isPaymentConfirmed(paymentStatus)) {
            createAndSaveReservation(finalizationDetails);
            logReservationConfirmation((String) finalizationDetails.get("roomNumber"));
        } else {
            logPaymentFailure((String) finalizationDetails.get("roomNumber"));
        }
    }

    private boolean isPaymentConfirmed(String paymentStatus) {
        return "confirmed".equals(paymentStatus);
    }

    private void createAndSaveReservation(Map<String, Object> finalizationDetails) {
        Reservation reservation = new Reservation();
        reservation.setClientName((String) finalizationDetails.get("clientName"));
        reservation.setRoomNumber((String) finalizationDetails.get("roomNumber"));
        reservation.setPaymentMethod((String) finalizationDetails.get("paymentMethod"));
        reservation.setReservationDate(LocalDateTime.now());

        reservationRepository.save(reservation);
    }

    private void logReservationConfirmation(String roomNumber) {
        logger.info("Reservation confirmed for room " + roomNumber + " after payment confirmation.");
    }

    private void logPaymentFailure(String roomNumber) {
        logger.info("Reservation for room " + roomNumber + " was not confirmed due to payment failure.");
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationByRoom(String roomNumber) {
        return reservationRepository.findByRoomNumber(roomNumber);
    }
    
    private boolean isValidFinalizationDetails(Map<String, Object> finalizationDetails) {
        return finalizationDetails != null &&
               finalizationDetails.containsKey("clientName") &&
               finalizationDetails.containsKey("roomNumber") &&
               finalizationDetails.containsKey("paymentMethod") &&
               finalizationDetails.containsKey("paymentStatus") &&
               finalizationDetails.get("clientName") != null &&
               finalizationDetails.get("roomNumber") != null &&
               finalizationDetails.get("paymentMethod") != null &&
               finalizationDetails.get("paymentStatus") != null;
    }
}
