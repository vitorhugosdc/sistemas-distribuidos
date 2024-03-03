package com.vitor.reservationservice.reservationservice.repository;

import com.vitor.reservationservice.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Aqui, você pode adicionar métodos de busca personalizados, se necessário.
    // Por exemplo:
    // Optional<Reservation> findByRoomNumber(String roomNumber);
}
