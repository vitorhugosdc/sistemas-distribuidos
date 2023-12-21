package com.vitor.reservationservice.reservationservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ReservationService {

	private final List<Map<String, String>> reservations = new ArrayList<>();

	public Map<String, String> makeReservation(String clientName, String roomNumber, String paymentMethod) {

		Map<String, String> reservation = new HashMap<>();
		reservation.put("reservationId", "RES-" + System.currentTimeMillis());
		reservation.put("clientName", clientName);
		reservation.put("roomNumber", roomNumber);
		reservation.put("paymentMethod", paymentMethod);

		reservations.add(reservation);
		return reservation;
	}

	public List<Map<String, String>> getAllReservations() {
		return new ArrayList<>(reservations);
	}

	public String getReservationByRoom(String roomNumber) {
		for (Map<String, String> reservation : reservations) {
			if (reservation.get("roomNumber").equals(roomNumber)) {
				return reservation.get("reservationId");
			}
		}
		return "There is not a reservation for this room";
	}
}
