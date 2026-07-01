package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerBookingController {

    @Autowired
    private BookingService bookingService;

    // UC06: Xem lịch sử đặt vé
    @GetMapping("/history")
    public String viewHistory(Model model) {
        // Fix cứng userId = 1 để test, sau này ghép code với Người 1 thì lấy từ Spring Security
        Long currentUserId = 1L;
        model.addAttribute("bookings", bookingService.getBookingHistory(currentUserId));
        return "customer/booking-history";
    }

    // UC10: Khách hàng ấn nút Hủy vé
    @PostMapping("/cancel")
    public String cancelTicket(@RequestParam("bookingId") Long bookingId, RedirectAttributes redirectAttributes) {
        boolean isCancelled = bookingService.cancelBooking(bookingId);
        if (isCancelled) {
            redirectAttributes.addFlashAttribute("successMsg", "Hủy vé thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Không thể hủy vé. Phim đã sắp chiếu hoặc vé đã xử lý.");
        }
        return "redirect:/customer/history";
    }

    // UC05.1: Chuyển đến trang thanh toán (Được gọi sau khi Người 2 chọn ghế xong)
    @GetMapping("/payment/{id}")
    public String showPaymentPage(@PathVariable("id") Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "customer/payment";
    }

    //  Xử lý bấm nút "Thanh toán" (Mockup)
    @PostMapping("/pay")
    public String processPayment(@RequestParam("bookingId") Long bookingId, RedirectAttributes redirectAttributes) {
        // Ở đây code giả lập thanh toán thành công luôn cho dễ báo cáo.
        bookingService.updatePaymentSuccess(bookingId);
        redirectAttributes.addFlashAttribute("successMsg", "Thanh toán thành công! Đây là vé điện tử của bạn.");

        // Trả về trang vé điện tử
        return "redirect:/customer/ticket/" + bookingId;
    }

    // Hiển thị vé điện tử (E-ticket)
    @GetMapping("/ticket/{id}")
    public String showTicket(@PathVariable("id") Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "customer/ticket";
    }
}