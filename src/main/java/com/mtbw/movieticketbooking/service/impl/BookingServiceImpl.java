package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.dto.SeatCellDto;
import com.mtbw.movieticketbooking.enums.SeatCellStatus;
import com.mtbw.movieticketbooking.dto.SeatMapPageDto;
import com.mtbw.movieticketbooking.dto.SeatMapRowDto;
import com.mtbw.movieticketbooking.entity.*;
import com.mtbw.movieticketbooking.enums.BookingSeatStatus;
import com.mtbw.movieticketbooking.enums.BookingStatus;
import com.mtbw.movieticketbooking.enums.PaymentMethod;
import com.mtbw.movieticketbooking.enums.PaymentStatus;
import com.mtbw.movieticketbooking.enums.ScreenType;
import com.mtbw.movieticketbooking.repository.*;
import com.mtbw.movieticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public List<Booking> getBookingHistory(Long userId) {
        return null;
    }

    @Override
    public boolean cancelBooking(Long bookingId) {
        return false;
    }

    @Override
    public void updatePaymentSuccess(Long bookingId) {

    }

    @Override
    public Booking getBookingById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public SeatMapPageDto buildSeatMap(Long showtimeId) {
        // Don rac: cac ghe HELD nhung da qua expiresAt phai duoc "tha" that su trong DB
        // (khong chi coi la trong ve mat logic), neu khong unique index se chan viec giu lai ghe do.
        releaseExpiredHolds(showtimeId);

        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay suat chieu"));

        Room room = showtime.getRoom();
        List<Seat> seats = seatRepository.findByRoomId(room.getId());

        // Gom ghe theo rowLabel THAT SU co trong DB (khong bia them hang), sap xep theo
        // rowLabel roi theo seatNumber de ra dung luoi hang/cot.
        Map<String, Map<Integer, Seat>> grid = new TreeMap<>();
        for (Seat s : seats) {
            grid.computeIfAbsent(s.getRowLabel(), k -> new TreeMap<>()).put(s.getSeatNumber(), s);
        }

        Set<Long> takenSeatIds = findTakenSeatIds(showtimeId);

        int totalColumns = room.getTotalColumns();
        List<SeatMapRowDto> rows = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Seat>> rowEntry : grid.entrySet()) {
            Map<Integer, Seat> seatsInRow = rowEntry.getValue();
            List<SeatCellDto> cells = new ArrayList<>();
            for (int col = 1; col <= totalColumns; col++) {
                Seat seat = seatsInRow.get(col);
                if (seat == null) {
                    cells.add(new SeatCellDto(null, rowEntry.getKey(), col, null, null, SeatCellStatus.EMPTY));
                    continue;
                }
                SeatCellStatus status;
                if (!seat.isActive()) {
                    status = SeatCellStatus.UNAVAILABLE;
                } else if (takenSeatIds.contains(seat.getId())) {
                    status = SeatCellStatus.SOLD;
                } else {
                    status = SeatCellStatus.AVAILABLE;
                }
                cells.add(new SeatCellDto(seat.getId(), seat.getRowLabel(), seat.getSeatNumber(),
                        seat.getSeatCode(), seat.getSeatType(), status));
            }
            rows.add(new SeatMapRowDto(rowEntry.getKey(), cells));
        }

        Cinema cinema = room.getCinema();
        return new SeatMapPageDto(
                showtime.getId(),
                showtime.getMovie().getTitle(),
                showtime.getMovie().getPosterUrl(),
                cinema.getName(),
                cinema.getAddress(),
                room.getName(),
                screenTypeLabel(room.getScreenType()),
                showtime.getStartTime().format(DATE_FMT),
                showtime.getStartTime().format(TIME_FMT),
                rows,
                totalColumns,
                showtime.getPrice(),
                formatPrice(showtime.getPrice())
        );
    }

    @Override
    @Transactional
    public Booking holdSeats(Long showtimeId, List<Long> seatIds, Long userId) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Vui long chon it nhat 1 ghe");
        }

        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay suat chieu"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay nguoi dung"));

        // Don rac truoc khi kiem tra/giu ghe - xem ghi chu o buildSeatMap().
        releaseExpiredHolds(showtimeId);

        // Kiem tra lai (race condition): co ghe nao vua bi nguoi khac giu/mua truoc trong luc
        // nguoi dung dang xem seat map khong.
        Set<Long> taken = findTakenSeatIds(showtimeId);
        for (Long seatId : seatIds) {
            if (taken.contains(seatId)) {
                throw new IllegalStateException("Mot so ghe ban chon vua co nguoi khac dat truoc, vui long chon lai");
            }
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("Danh sach ghe khong hop le");
        }

        BigDecimal total = showtime.getPrice().multiply(BigDecimal.valueOf(seats.size()));
        String bookingCode = "BK" + System.currentTimeMillis();

        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .bookingCode(bookingCode)
                .totalAmount(total)
                .status(BookingStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        booking = bookingRepository.save(booking);

        for (Seat seat : seats) {
            BookingSeat bs = BookingSeat.builder()
                    .booking(booking)
                    .showtime(showtime)
                    .seat(seat)
                    .ticketCode(bookingCode + "-" + seat.getSeatCode())
                    .price(showtime.getPrice())
                    .status(BookingSeatStatus.HELD)
                    .build();
            bookingSeatRepository.save(bs);
        }

        return booking;
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay don dat ve"));
    }

    @Override
    public List<BookingSeat> getBookingSeats(Long bookingId) {
        return bookingSeatRepository.findByBookingId(bookingId);
    }

    @Override
    @Transactional
    public Booking payBooking(Long bookingId, PaymentMethod method) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay don dat ve"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Don dat ve nay khong o trang thai cho thanh toan (trang thai hien tai: " + booking.getStatus() + ")");
        }

        // Qua han giu ghe: khong cho thanh toan nua, danh dau EXPIRED.
        if (booking.getExpiresAt() != null && booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new IllegalStateException("Da het thoi gian giu ghe, vui long chon lai ghe va dat ve moi");
        }

        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(bookingId);
        if (bookingSeats.isEmpty()) {
            throw new IllegalStateException("Don dat ve khong co ghe nao, khong the thanh toan");
        }

        LocalDateTime now = LocalDateTime.now();

        for (BookingSeat bs : bookingSeats) {
            bs.setStatus(BookingSeatStatus.BOOKED);
            bookingSeatRepository.save(bs);
        }

        // Mo phong thanh toan: chua tich hop cong thanh toan that (Momo/VNPay...) nen
        // luon tra ve SUCCESS ngay lap tuc.
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentMethod(method)
                .status(PaymentStatus.SUCCESS)
                .paidAt(now)
                .build();
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.PAID);
        booking.setPaidAt(now);
        return bookingRepository.save(booking);
    }

    @Override
    public Payment getPayment(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElse(null);
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay don dat ve"));

        // Kiem tra booking thuoc ve user hien tai
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Ban khong co quyen huy don dat ve nay");
        }

        // Chi cho phep huy khi dang PENDING
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Chi co the huy don dat ve dang cho thanh toan (trang thai hien tai: " + booking.getStatus() + ")");
        }

        // Kiem tra chua toi gio chieu
        if (booking.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Khong the huy ve khi suat chieu da bat dau hoac da ket thuc");
        }

        // Cap nhat trang thai cac BookingSeat -> CANCELLED
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(bookingId);
        for (BookingSeat bs : bookingSeats) {
            bs.setStatus(BookingSeatStatus.CANCELLED);
            bookingSeatRepository.save(bs);
        }

        // Cap nhat trang thai Booking -> CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    // Ghe duoc coi la "khong the chon" doi voi nguoi dung khac neu: da BOOKED/USED,
    // hoac dang HELD ma booking cha van con han giu (chua qua expiresAt).
    private Set<Long> findTakenSeatIds(Long showtimeId) {
        LocalDateTime now = LocalDateTime.now();
        Set<Long> taken = new HashSet<>();
        for (BookingSeat bs : bookingSeatRepository.findByShowtimeId(showtimeId)) {
            BookingSeatStatus st = bs.getStatus();
            boolean isTaken = st == BookingSeatStatus.BOOKED || st == BookingSeatStatus.USED
                    || (st == BookingSeatStatus.HELD
                    && bs.getBooking().getExpiresAt() != null
                    && bs.getBooking().getExpiresAt().isAfter(now));
            if (isTaken) {
                taken.add(bs.getSeat().getId());
            }
        }
        return taken;
    }

    // Chuyen cac BookingSeat dang HELD nhung da qua han giu (expiresAt) sang EXPIRED,
    // va Booking cha (neu con PENDING) cung sang EXPIRED. Phai lam that su trong DB
    // (khong chi bo qua o tang logic) vi index unique 'ux_booking_seats_active' chi biet
    // doc cot status, khong biet gio (expiresAt) - neu khong "tha" that, insert lai se bi
    // loi DataIntegrityViolationException (duplicate key) khi ai do giu lai dung ghe do.
    private void releaseExpiredHolds(Long showtimeId) {
        LocalDateTime now = LocalDateTime.now();
        Set<Booking> expiredBookings = new HashSet<>();

        for (BookingSeat bs : bookingSeatRepository.findByShowtimeId(showtimeId)) {
            Booking booking = bs.getBooking();
            boolean expired = bs.getStatus() == BookingSeatStatus.HELD
                    && booking.getExpiresAt() != null
                    && booking.getExpiresAt().isBefore(now);
            if (expired) {
                bs.setStatus(BookingSeatStatus.EXPIRED);
                bookingSeatRepository.save(bs);
                expiredBookings.add(booking);
            }
        }

        for (Booking booking : expiredBookings) {
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);
            }
        }
    }

    private String formatPrice(BigDecimal price) {
        long thousand = price.longValue() / 1000;
        return thousand + "K";
    }

    private String screenTypeLabel(ScreenType type) {
        return switch (type) {
            case STANDARD -> "Phòng chiếu 2D";
            case IMAX -> "Phòng chiếu IMAX";
            case VIP -> "Phòng chiếu VIP";
        };
    }
}
