package com.mtbw.movieticketbooking.repository;

import com.mtbw.movieticketbooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBookingId(Long bookingId);
    List<BookingSeat> findByShowtimeId(Long showtimeId);
    Optional<BookingSeat> findByTicketCode(String ticketCode);
    boolean existsByShowtimeIdAndSeatId(Long showtimeId, Long seatId);
}
