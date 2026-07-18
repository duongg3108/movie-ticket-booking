package com.mtbw.movieticketbooking.service;
import com.mtbw.movieticketbooking.entity.BookingSeat;
import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.entity.User;
import java.util.List;

public interface BookingService {
    // Lấy danh sách booking đang ở trạng thái PAID
    List<Booking> getPendingBookings();

    // Duyệt booking sang trạng thái CONFIRMED bởi nhân viên (staff)
    void confirmBooking(Long bookingId, User staff);
    BookingSeat validateTicket(String ticketCode);
}