package com.vitor.roomservice.roomservice.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RoomService {

	private final Map<String, String> reservations = new HashMap<>();

	public String checkRoomAvailability(String roomNumber) {
		if (reservations.containsKey(roomNumber)) {
			return "Room service: Room " + roomNumber + " is not available for reservation.";
		} else {
			return "Room service: Room " + roomNumber + " is available for reservation.";
		}
	}

	public void makeReservation(String roomNumber, String reservationId) {
		reservations.put(roomNumber, reservationId);
	}

	public String getReservation(String roomNumber) {
		return reservations.get(roomNumber);
	}
}
