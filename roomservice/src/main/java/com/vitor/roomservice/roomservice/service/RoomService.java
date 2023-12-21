package com.vitor.roomservice.roomservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RoomService {

	private final Map<String, String> reservations = new HashMap<>();

	@Autowired
	private RestTemplate restTemplate;

	public String checkRoomAvailability(String roomNumber) {
		if (reservations.containsKey(roomNumber)) {
			return "Room service: Room " + roomNumber + " is not available for reservation.";
		} else {
			return "Room service: Room " + roomNumber + " is available for reservation.";
		}
	}

	public String makeReservation(String clientName, String roomNumber, String paymentMethod) {

		String availabilityResult = checkRoomAvailability(roomNumber);

		if (availabilityResult.contains("Room service: Room " + roomNumber + " is available for reservation.")) {
			String paymentResult = restTemplate.getForObject(
					"http://localhost:8082/payments/processPayment?method=" + paymentMethod, String.class);

			if (paymentResult.contains("Payment processed successfully")) {

				ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {
				};
				ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
						"http://localhost:8081" + "/reservations/makeReservation" + "?clientName=" + clientName
								+ "&roomNumber=" + roomNumber + "&paymentMethod=" + paymentMethod,
						HttpMethod.GET, null, responseType);

				Map<String, String> reservation = responseEntity.getBody();

				if (reservation != null && reservation.containsKey("reservationId")
						&& reservation.containsKey("clientName")) {

					reservations.put(roomNumber, reservation.get("reservationId"));

					return "Room service: Room " + roomNumber + " reserved successfully. Reservation ID: "
							+ reservation.get("reservationId") + " | " + paymentResult + " | Reservation completed.";
				} else {

					return "Room service: Reservation failed. Invalid reservation response.";
				}
			} else {

				return "Room service: Payment failed. " + paymentResult;
			}
		} else {
			return availabilityResult;
		}
	}
}