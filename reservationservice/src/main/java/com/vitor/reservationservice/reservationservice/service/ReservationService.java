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
    //1. Método longo (Long method)
    public void finalizeReservation(Map<String, Object> finalizationDetails) {
    	//5. Ausência de validação (Missing checks)
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
            //2. Uso de println para logging (Dispensers)
            System.out.println("Reservation confirmed for room " + roomNumber + " after payment confirmation.");
        } else {
            System.out.println("Reservation for room " + roomNumber + " was not confirmed due to payment failure.");
        }
    }
    
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationByRoom(String roomNumber) {
    	//3. Acesso direto ao Repositório (Inappropriate Intimacy)
        List<Reservation> matchingReservations = reservationRepository.findAll().stream()
            .filter(reservation -> reservation.getRoomNumber().equals(roomNumber))
            .collect(Collectors.toList());

        if (!matchingReservations.isEmpty()) {
            return matchingReservations.get(0);
        }
        //4. Retorno de null (Error Handling)
        return null;
    }
}
