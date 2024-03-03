package com.vitor.reservationservice.reservationservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitor.reservationservice.reservationservice.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

	@Autowired
	private ReservationService reservationService;

	@GetMapping("/getAllReservations")
	public List<Map<String, String>> getAllReservations() {
		return reservationService.getAllReservations();
	}

	@GetMapping("/getReservationByRoom")
	public String getReservationByRoom(@RequestParam String roomNumber) {
		return reservationService.getReservationByRoom(roomNumber);
	}
}
