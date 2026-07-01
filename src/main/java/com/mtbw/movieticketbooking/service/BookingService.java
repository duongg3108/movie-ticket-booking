package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.enums.BookingStatus;
import com.mtbw.movieticketbooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    //Lấy danh sách lịch sử đặt vé của 1 user
    public List<Booking> getBookingHistory(Long userId) {
        // Gọi đúng tên hàm mới
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Xử lý hủy vé PENDING
    public boolean cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking != null && BookingStatus.PENDING.equals(booking.getStatus())) {
            LocalDateTime startTime = booking.getShowtime().getStartTime();

            // Hủy trước 1 tiếng
            if (LocalDateTime.now().isBefore(startTime.minusHours(1))) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                return true;
            }
        }
        return false;
    }

    // Xử lý thanh toán thành công
    public void updatePaymentSuccess(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking != null && BookingStatus.PENDING.equals(booking.getStatus())) {
            booking.setStatus(BookingStatus.PAID);
            // Cập nhật thêm giờ thanh toán
            booking.setPaidAt(LocalDateTime.now());
            bookingRepository.save(booking);
        }
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }
}