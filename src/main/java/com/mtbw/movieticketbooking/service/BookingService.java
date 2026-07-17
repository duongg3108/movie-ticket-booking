package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.SeatMapPageDto;
import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.entity.BookingSeat;
import com.mtbw.movieticketbooking.entity.Payment;
import com.mtbw.movieticketbooking.enums.PaymentMethod;

import java.util.List;

public interface BookingService {

    // ===== Các hàm của teammate =====
    List<Booking> getBookingHistory(Long userId);

    boolean cancelBooking(Long bookingId);

    void updatePaymentSuccess(Long bookingId);

    Booking getBookingById(Long id);

    // ===== Các hàm của bạn =====
    SeatMapPageDto buildSeatMap(Long showtimeId);

    Booking holdSeats(Long showtimeId, List<Long> seatIds, Long userId);

    Booking getBooking(Long bookingId);

    List<BookingSeat> getBookingSeats(Long bookingId);

    Booking payBooking(Long bookingId, PaymentMethod method);

    Payment getPayment(Long bookingId);
}