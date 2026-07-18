package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.entity.Cinema;
import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.entity.Room;
import com.mtbw.movieticketbooking.entity.Showtime;
import com.mtbw.movieticketbooking.enums.ShowtimeStatus;
import com.mtbw.movieticketbooking.service.CinemaService;
import com.mtbw.movieticketbooking.service.MovieService;
import com.mtbw.movieticketbooking.service.RoomService;
import com.mtbw.movieticketbooking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class AdminShowtimeController {

    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final CinemaService cinemaService;
    private final RoomService roomService;

    @ModelAttribute("activePage")
    public String activePage() {
        return "showtimes";
    }

    // ==================== DANH SÁCH LỊCH CHIẾU + FILTER ====================
    @GetMapping
    public String listShowtimes(@RequestParam(value = "movieId", required = false) Long movieId,
                                @RequestParam(value = "cinemaId", required = false) Long cinemaId,
                                @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                Model model) {
        List<Showtime> showtimes = showtimeService.findAll();

        if (movieId != null) {
            showtimes = showtimes.stream()
                    .filter(s -> s.getMovie().getId().equals(movieId))
                    .collect(Collectors.toList());
        }

        if (cinemaId != null) {
            showtimes = showtimes.stream()
                    .filter(s -> s.getRoom().getCinema().getId().equals(cinemaId))
                    .collect(Collectors.toList());
        }

        if (date != null) {
            showtimes = showtimes.stream()
                    .filter(s -> s.getStartTime().toLocalDate().equals(date))
                    .collect(Collectors.toList());
        }

        // Sort by start time descending
        showtimes.sort((s1, s2) -> s2.getStartTime().compareTo(s1.getStartTime()));

        model.addAttribute("showtimes", showtimes);
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("cinemas", cinemaService.findAll());
        model.addAttribute("selectedMovieId", movieId);
        model.addAttribute("selectedCinemaId", cinemaId);
        model.addAttribute("selectedDate", date);

        return "admin/showtime-list";
    }

    // ==================== FORM THÊM LỊCH CHIẾU ====================
    @GetMapping("/new")
    public String showAddForm(Model model) {
        Showtime showtime = new Showtime();
        showtime.setStatus(ShowtimeStatus.OPEN);

        model.addAttribute("showtime", showtime);
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("cinemas", cinemaService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("statuses", ShowtimeStatus.values());
        model.addAttribute("isEdit", false);

        return "admin/showtime-form";
    }

    // ==================== FORM SỬA LỊCH CHIẾU ====================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Showtime> showtimeOpt = showtimeService.findById(id);
        if (showtimeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy lịch chiếu với ID: " + id);
            return "redirect:/admin/showtimes";
        }

        Showtime showtime = showtimeOpt.get();
        model.addAttribute("showtime", showtime);
        model.addAttribute("selectedCinemaId", showtime.getRoom().getCinema().getId());
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("cinemas", cinemaService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("statuses", ShowtimeStatus.values());
        model.addAttribute("isEdit", true);

        return "admin/showtime-form";
    }

    // ==================== LƯU LỊCH CHIẾU ====================
    @PostMapping("/save")
    public String saveShowtime(@ModelAttribute("showtime") Showtime showtime,
                               @RequestParam("movieId") Long movieId,
                               @RequestParam("roomId") Long roomId,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        Optional<Movie> movieOpt = movieService.findById(movieId);
        Optional<Room> roomOpt = roomService.findById(roomId);

        boolean hasError = false;

        if (movieOpt.isEmpty()) {
            model.addAttribute("movieError", "Phim được chọn không hợp lệ!");
            hasError = true;
        }

        if (roomOpt.isEmpty()) {
            model.addAttribute("roomError", "Phòng chiếu được chọn không hợp lệ!");
            hasError = true;
        }

        if (showtime.getStartTime() == null) {
            model.addAttribute("startTimeError", "Thời gian bắt đầu không được để trống!");
            hasError = true;
        }

        if (showtime.getEndTime() == null) {
            model.addAttribute("endTimeError", "Thời gian kết thúc không được để trống!");
            hasError = true;
        }

        if (showtime.getStartTime() != null && showtime.getEndTime() != null 
                && !showtime.getStartTime().isBefore(showtime.getEndTime())) {
            model.addAttribute("timeError", "Thời gian bắt đầu phải trước thời gian kết thúc!");
            hasError = true;
        }

        if (showtime.getPrice() == null || showtime.getPrice().doubleValue() <= 0) {
            model.addAttribute("priceError", "Giá vé phải lớn hơn 0 VNĐ!");
            hasError = true;
        }

        // Nếu có lỗi nhập liệu thì trả về form
        if (hasError) {
            model.addAttribute("movies", movieService.findAll());
            model.addAttribute("cinemas", cinemaService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("statuses", ShowtimeStatus.values());
            model.addAttribute("isEdit", showtime.getId() != null);
            if (roomOpt.isPresent()) {
                model.addAttribute("selectedCinemaId", roomOpt.get().getCinema().getId());
            }
            return "admin/showtime-form";
        }

        // Gán movie và room
        showtime.setMovie(movieOpt.get());
        showtime.setRoom(roomOpt.get());

        // Kiểm tra trùng lịch chiếu (overlap) trong cùng một phòng chiếu
        List<Showtime> roomShowtimes = showtimeService.findAll().stream()
                .filter(s -> s.getRoom().getId().equals(roomId))
                .filter(s -> showtime.getId() == null || !s.getId().equals(showtime.getId()))
                .collect(Collectors.toList());

        boolean hasOverlap = roomShowtimes.stream().anyMatch(s -> 
            (showtime.getStartTime().isBefore(s.getEndTime()) && showtime.getEndTime().isAfter(s.getStartTime()))
        );

        if (hasOverlap) {
            model.addAttribute("timeError", "Phòng chiếu đã có lịch chiếu khác trong khoảng thời gian này!");
            model.addAttribute("movies", movieService.findAll());
            model.addAttribute("cinemas", cinemaService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("statuses", ShowtimeStatus.values());
            model.addAttribute("isEdit", showtime.getId() != null);
            model.addAttribute("selectedCinemaId", roomOpt.get().getCinema().getId());
            return "admin/showtime-form";
        }

        try {
            boolean isNew = (showtime.getId() == null);
            showtimeService.save(showtime);
            redirectAttributes.addFlashAttribute("successMessage",
                    isNew ? "Thêm lịch chiếu mới thành công!" : "Cập nhật thông tin lịch chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu lịch chiếu: " + e.getMessage());
        }

        return "redirect:/admin/showtimes";
    }

    // ==================== XÓA LỊCH CHIẾU ====================
    @PostMapping("/delete/{id}")
    public String deleteShowtime(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            showtimeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa lịch chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa lịch chiếu này (có thể do đã có khách đặt vé)!");
        }
        return "redirect:/admin/showtimes";
    }
}
