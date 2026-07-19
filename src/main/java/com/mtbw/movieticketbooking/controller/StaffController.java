package com.mtbw.movieticketbooking.controller;
import com.mtbw.movieticketbooking.entity.BookingSeat;
import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.entity.Showtime;
import com.mtbw.movieticketbooking.entity.User;
import com.mtbw.movieticketbooking.service.BookingService;
import com.mtbw.movieticketbooking.service.ShowtimeService;
import com.mtbw.movieticketbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final ShowtimeService showtimeService;
    private final BookingService bookingService;
    private final UserService userService;

    // 1. Trang Soát Vé
    @GetMapping("/tickets")
    public String ticketValidationPage() {
        return "staff/tickets";
    }

    // 2. Trang Dashboard Suất Chiếu
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        List<Showtime> todayShowtimes = showtimeService.getTodayShowtimes();
        model.addAttribute("todayShowtimes", todayShowtimes);
        return "staff/dashboard";
    }

    // 3. Trang Danh Sách Booking chờ duyệt (Đọc dữ liệu thật)
    @GetMapping("/bookings")
    public String confirmBookingsPage(Model model) {
        List<Booking> pendingBookings = bookingService.getPendingBookings();
        model.addAttribute("pendingBookings", pendingBookings);
        return "staff/bookings";
    }

    // 4. Xử lý yêu cầu duyệt Booking (POST)
    @PostMapping("/bookings/confirm")
    public String confirmBooking(@RequestParam("bookingId") Long bookingId, Principal principal , RedirectAttributes redirectAttributes) {
        // Lấy email của tài khoản nhân viên đang đăng nhập hiện tại
        String email = principal.getName();

        // Tìm User (nhân viên) trong DB dựa trên email
        User staff = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản nhân viên!"));

        // Gọi service xử lý duyệt
        bookingService.confirmBooking(bookingId, staff);

        // 2. Lấy danh sách các vé con vừa duyệt thành công
        List<BookingSeat> seats = bookingService.getBookingSeats(bookingId);

        // 3. Gửi danh sách vé và thông báo thành công sang trang tiếp theo
        redirectAttributes.addFlashAttribute("successMessage", "Duyệt đơn đặt vé thành công!");
        redirectAttributes.addFlashAttribute("confirmedSeats", seats);

        // Quay lại trang danh sách sau khi duyệt thành công
        return "redirect:/staff/bookings?success";
    }
    @PostMapping("/tickets/validate")
    public String validateTicket(@RequestParam("ticketCode") String ticketCode, RedirectAttributes redirectAttributes) {
        try {
            // Gọi service thực hiện soát vé
            BookingSeat ticket = bookingService.validateTicket(ticketCode);

            // Trả về thông báo thành công và thông tin vé
            redirectAttributes.addFlashAttribute("successMessage", "Xác thực thành công! Cho phép khách vào phòng chiếu.");
            redirectAttributes.addFlashAttribute("ticket", ticket);
        } catch (Exception e) {
            // Trả về thông báo lỗi nếu có ngoại lệ xảy ra
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Quay trở lại trang soát vé ban đầu
        return "redirect:/staff/tickets";
    }
}