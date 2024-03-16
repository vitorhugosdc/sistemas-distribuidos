package com.vitor.reservationservice.reservationservice.repository;

import com.vitor.reservationservice.reservationservice.model.Reservation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	    Optional<Reservation> findByRoomNumber(String roomNumber);
}
