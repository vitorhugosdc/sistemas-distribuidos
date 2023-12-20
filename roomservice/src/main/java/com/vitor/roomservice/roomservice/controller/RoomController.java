package com.vitor.roomservice.roomservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitor.roomservice.roomservice.service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/checkAvailability")
    public String checkAvailability(@RequestParam String roomNumber) {
        return roomService.checkRoomAvailability(roomNumber);
    }

    @GetMapping("/makeReservation")
    public String makeReservation(@RequestParam String clientName, @RequestParam String roomNumber,
            @RequestParam String paymentMethod) {
        // Use o método atualizado em RoomService
        return roomService.makeReservation(clientName, roomNumber, paymentMethod);
    }
}
