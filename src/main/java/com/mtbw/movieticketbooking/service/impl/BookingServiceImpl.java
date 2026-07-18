package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.entity.BookingSeat;
import com.mtbw.movieticketbooking.entity.User;
import com.mtbw.movieticketbooking.enums.BookingSeatStatus;
import com.mtbw.movieticketbooking.enums.BookingStatus;
import com.mtbw.movieticketbooking.repository.BookingRepository;
import com.mtbw.movieticketbooking.repository.BookingSeatRepository;
import com.mtbw.movieticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Override
    public List<Booking> getPendingBookings() {
        // Lấy tất cả booking có trạng thái PAID để duyệt
        return bookingRepository.findByStatusOrderByCreatedAtDesc(BookingStatus.PAID);
    }

    @Override
    @Transactional // Đảm bảo tính toàn vẹn dữ liệu khi cập nhật nhiều bảng
    public void confirmBooking(Long bookingId, User staff) {
        // 1. Tìm booking theo ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã đặt vé này!"));

        // 2. Kiểm tra nếu booking đang ở trạng thái PAID thì mới duyệt
        if (booking.getStatus() == BookingStatus.PAID) {
            // Cập nhật trạng thái booking sang CONFIRMED
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setConfirmedBy(staff);
            booking.setConfirmedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            // 3. Cập nhật toàn bộ vé liên quan trong bảng booking_seats sang trạng thái BOOKED
            List<BookingSeat> seats = bookingSeatRepository.findByBookingId(bookingId);
            for (BookingSeat seat : seats) {
                seat.setStatus(BookingSeatStatus.BOOKED);
                bookingSeatRepository.save(seat);
            }
        }
    }
    @Override
    @Transactional
    public BookingSeat validateTicket(String ticketCode) {
        // 1. Tìm vé theo mã ticket_code
        BookingSeat ticket = bookingSeatRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new IllegalArgumentException("Mã vé không tồn tại trên hệ thống!"));
        // 2. Kiểm tra trạng thái của đơn đặt vé cha (Booking)
        Booking booking = ticket.getBooking();
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Đơn hàng chứa vé này chưa được thanh toán hoặc xác nhận!");
        }
        // 3. Kiểm tra trạng thái của chính chiếc vé đó (BookingSeat)
        switch (ticket.getStatus()) {
            case USED:
                throw new IllegalStateException("Vé này đã được sử dụng để vào phòng chiếu trước đó!");
            case CANCELLED:
                throw new IllegalStateException("Vé này đã bị hủy!");
            case EXPIRED:
                throw new IllegalStateException("Vé này đã hết hạn!");
            case HELD:
                throw new IllegalStateException("Vé này chưa được thanh toán hoàn tất!");
            case BOOKED:
                // Trạng thái hợp lệ -> Cập nhật sang USED (Đã sử dụng)
                ticket.setStatus(BookingSeatStatus.USED);
                return bookingSeatRepository.save(ticket);
            default:
                throw new IllegalStateException("Trạng thái vé không hợp lệ!");
        }
    }
}