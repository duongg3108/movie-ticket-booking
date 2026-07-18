package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.entity.Booking;
import com.mtbw.movieticketbooking.enums.BookingStatus;
import com.mtbw.movieticketbooking.enums.PaymentMethod;
import com.mtbw.movieticketbooking.security.CustomUserDetails;
import com.mtbw.movieticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * UC04 - Chon ghe cho 1 suat chieu (seat map) + giu ghe tam thoi (HELD) truoc khi thanh toan.
 * UC05 - Chon phuong thuc thanh toan, hien QR chuyen khoan (mo phong) va xac nhan don dat ve.
 */
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Value("${app.payment.bank-bin}")
    private String bankBin;

    @Value("${app.payment.bank-name}")
    private String bankName;

    @Value("${app.payment.account-no}")
    private String accountNo;

    @Value("${app.payment.account-name}")
    private String accountName;

    @GetMapping("/{showtimeId}")
    public String seatMap(@PathVariable Long showtimeId, Model model) {
        model.addAttribute("seatMap", bookingService.buildSeatMap(showtimeId));
        return "SeatMap";
    }

    @PostMapping("/{showtimeId}/hold")
    public String hold(@PathVariable Long showtimeId,
                       @RequestParam String seatIds,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
        List<Long> ids = Arrays.stream(seatIds.split(","))
                .filter(s -> !s.isBlank())
                .map(Long::valueOf)
                .toList();

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        try {
            Booking booking = bookingService.holdSeats(showtimeId, ids, principal.getUser().getId());
            return "redirect:/booking/checkout/" + booking.getId();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/" + showtimeId;
        }
    }

    @GetMapping("/checkout/{bookingId}")
    public String checkout(@PathVariable Long bookingId, Model model) {
        model.addAttribute("booking", bookingService.getBooking(bookingId));
        model.addAttribute("bookingSeats", bookingService.getBookingSeats(bookingId));
        return "Checkout";
    }

    @GetMapping("/payment/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId, Model model, RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getBooking(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error",
                    "Đơn đặt vé không còn ở trạng thái chờ thanh toán, vui lòng đặt lại");
            return "redirect:/booking/checkout/" + bookingId;
        }

        model.addAttribute("booking", booking);
        model.addAttribute("bookingSeats", bookingService.getBookingSeats(bookingId));
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "Payment";
    }

    // Buoc 1: nguoi dung chon phuong thuc. Tien mat -> xu ly ngay (khong can QR).
    // Cac phuong thuc con lai -> sang man hinh chuyen khoan QR.
    @PostMapping("/payment/{bookingId}")
    public String selectMethod(@PathVariable Long bookingId,
                               @RequestParam PaymentMethod method,
                               RedirectAttributes redirectAttributes) {
        if (method == PaymentMethod.CASH) {
            try {
                Booking booking = bookingService.payBooking(bookingId, method);
                return "redirect:/booking/success/" + booking.getId();
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/booking/checkout/" + bookingId;
            }
        }
        return "redirect:/booking/payment/" + bookingId + "/transfer?method=" + method;
    }

    // Buoc 2 (mo phong): hien thong tin chuyen khoan + QR VietQR cho don dang PENDING.
    @GetMapping("/payment/{bookingId}/transfer")
    public String transferPage(@PathVariable Long bookingId,
                               @RequestParam PaymentMethod method,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Booking booking = bookingService.getBooking(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error",
                    "Đơn đặt vé không còn ở trạng thái chờ thanh toán, vui lòng đặt lại");
            return "redirect:/booking/checkout/" + bookingId;
        }

        String content = booking.getBookingCode();
        long amount = booking.getTotalAmount().longValue();
        String qrUrl = buildVietQrUrl(amount, content);

        model.addAttribute("booking", booking);
        model.addAttribute("bookingSeats", bookingService.getBookingSeats(bookingId));
        model.addAttribute("method", method);
        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("bankName", bankName);
        model.addAttribute("accountNo", accountNo);
        model.addAttribute("accountName", accountName);
        model.addAttribute("transferContent", content);
        return "Transfer";
    }

    // Buoc 3 (mo phong): nguoi dung bam "Toi da chuyen khoan" -> coi nhu giao dich thanh cong ngay.
    @PostMapping("/payment/{bookingId}/confirm-transfer")
    public String confirmTransfer(@PathVariable Long bookingId,
                                  @RequestParam PaymentMethod method,
                                  RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.payBooking(bookingId, method);
            return "redirect:/booking/success/" + booking.getId();
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/booking/checkout/" + bookingId;
        }
    }

    @GetMapping("/success/{bookingId}")
    public String success(@PathVariable Long bookingId, Model model) {
        model.addAttribute("booking", bookingService.getBooking(bookingId));
        model.addAttribute("bookingSeats", bookingService.getBookingSeats(bookingId));
        model.addAttribute("payment", bookingService.getPayment(bookingId));
        return "BookingSuccess";
    }

    // VietQR "quick link" API - khong can API key, tra ve thang anh PNG de nhung vao <img>.
    // Doc them: https://www.vietqr.io/danh-sach-api/link-tao-nhanh-ma-vietqr/
    private String buildVietQrUrl(long amount, String content) {
        try {
            String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
            String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8.toString());
            return "https://img.vietqr.io/image/" + bankBin + "-" + accountNo + "-compact2.png"
                    + "?amount=" + amount
                    + "&addInfo=" + encodedContent
                    + "&accountName=" + encodedAccountName;
        } catch (UnsupportedEncodingException e) {
            return "https://img.vietqr.io/image/" + bankBin + "-" + accountNo + "-compact2.png";
        }
    }
}
