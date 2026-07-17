package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.SeatMapPageDto;
import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.entity.BookingSeat;
import com.mtbw.movieticketbooking.entity.Payment;
import com.mtbw.movieticketbooking.enums.PaymentMethod;

import java.util.List;

public interface BookingService {

    /**
     * Dung du lieu that trong Room/Seat + BookingSeat cua suat chieu de dung seat map:
     * ghe nao trong, ghe nao dang bi giu/da ban, vi tri nao khong co ghe (loi di).
     */
    SeatMapPageDto buildSeatMap(Long showtimeId);

    /**
     * Tao 1 Booking (PENDING) + cac BookingSeat (HELD, het han sau 10 phut) cho danh sach ghe da chon.
     * Kiem tra lai (race condition) truoc khi giu de tranh 2 nguoi cung chon trung ghe.
     */
    Booking holdSeats(Long showtimeId, List<Long> seatIds, Long userId);

    Booking getBooking(Long bookingId);

    List<BookingSeat> getBookingSeats(Long bookingId);

    /**
     * UC05 - Thanh toan don dat ve: kiem tra booking dang PENDING va chua het han giu ghe,
     * tao Payment (mo phong luon SUCCESS vi chua tich hop cong thanh toan that), chuyen
     * BookingSeat HELD -> BOOKED va Booking PENDING -> PAID.
     */
    Booking payBooking(Long bookingId, PaymentMethod method);

    Payment getPayment(Long bookingId);
}
