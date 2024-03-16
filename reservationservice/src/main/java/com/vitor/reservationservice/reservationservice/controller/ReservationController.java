package com.vitor.reservationservice.reservationservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitor.reservationservice.reservationservice.model.Reservation;
import com.vitor.reservationservice.reservationservice.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

	@Autowired
	private ReservationService reservationService;

	@GetMapping("/getAllReservations")
	public List<Reservation> getAllReservations() {
		return reservationService.getAllReservations();
	}

    @GetMapping("/getReservationByRoom")
    public ResponseEntity<?> getReservationByRoom(@RequestParam String roomNumber) {
        Optional<Reservation> reservation = reservationService.getReservationByRoom(roomNumber);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
