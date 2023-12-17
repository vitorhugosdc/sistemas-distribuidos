package com.vitor.roomservice.roomservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.vitor.roomservice.roomservice.service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RoomService roomService;

	@GetMapping("/checkAvailability")
	public String checkAvailability(@RequestParam String roomNumber) {
		return roomService.checkRoomAvailability(roomNumber);
	}

	@GetMapping("/makeReservation")
	public String makeReservation(@RequestParam String clientName, @RequestParam String roomNumber,
			@RequestParam String paymentMethod) {
		if (paymentMethod == null || paymentMethod.isEmpty()) {
			return "Room service: Payment method is required for reservation.";
		}

		String availabilityResult = roomService.checkRoomAvailability(roomNumber);

		if (availabilityResult.contains("Room service: Room " + roomNumber + " is available for reservation.")) {
			String paymentResult = restTemplate.getForObject(
					"http://localhost:8082/payments/processPayment?method=" + paymentMethod, String.class);

			if (paymentResult.contains("Payment processed successfully!")) {

				String reservationId = "RES-" + System.currentTimeMillis();
				roomService.makeReservation(roomNumber, reservationId);

				return "Room service: Room " + roomNumber + " reserved successfully. Reservation ID: " + reservationId
						+ " | " + paymentResult + " | Reservation completed.";
			} else {

				return "Room service: Payment failed. " + paymentResult;
			}
		} else {

			return availabilityResult;
		}
	}

}
