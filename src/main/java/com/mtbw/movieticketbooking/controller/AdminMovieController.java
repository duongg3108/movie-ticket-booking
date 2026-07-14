package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import com.mtbw.movieticketbooking.service.FileStorageService;
import com.mtbw.movieticketbooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;
    private final FileStorageService fileStorageService;

    @ModelAttribute("activePage")
    public String activePage() {
        return "movies";
    }

    // ==================== DANH SÁCH PHIM + FILTER ====================

    @GetMapping
    public String listMovies(@RequestParam(value = "status", required = false) String status, Model model) {
        List<Movie> movies;

        if (status != null && !status.isEmpty()) {
            try {
                MovieStatus movieStatus = MovieStatus.valueOf(status);
                movies = movieService.findByStatus(movieStatus);
            } catch (IllegalArgumentException e) {
                movies = movieService.findAll();
            }
        } else {
            movies = movieService.findAll();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("currentStatus", status);
        model.addAttribute("statuses", MovieStatus.values());
        return "admin/movie-list";
    }

    // ==================== FORM THÊM PHIM ====================

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("statuses", MovieStatus.values());
        model.addAttribute("isEdit", false);
        return "admin/movie-form";
    }

    // ==================== FORM SỬA PHIM ====================

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phim với ID: " + id);
            return "redirect:/admin/movies";
        }

        model.addAttribute("movie", movieOpt.get());
        model.addAttribute("statuses", MovieStatus.values());
        model.addAttribute("isEdit", true);
        return "admin/movie-form";
    }

    // ==================== LƯU PHIM (CREATE / UPDATE) + UPLOAD POSTER ====================

    @PostMapping("/save")
    public String saveMovie(@ModelAttribute("movie") Movie movie,
                            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // --- Validation thủ công ---
        boolean hasError = false;
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            model.addAttribute("titleError", "Tên phim không được để trống!");
            hasError = true;
        }
        if (movie.getDurationMinutes() == null || movie.getDurationMinutes() <= 0) {
            model.addAttribute("durationError", "Thời lượng phải lớn hơn 0 phút!");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("statuses", MovieStatus.values());
            model.addAttribute("isEdit", movie.getId() != null);
            return "admin/movie-form";
        }

        // --- Upload poster nếu có file mới ---
        if (posterFile != null && !posterFile.isEmpty()) {
            try {
                String posterPath = fileStorageService.savePosterFile(posterFile);
                movie.setPosterUrl(posterPath);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi upload poster: " + e.getMessage());
                model.addAttribute("statuses", MovieStatus.values());
                model.addAttribute("isEdit", movie.getId() != null);
                return "admin/movie-form";
            }
        } else if (movie.getId() != null) {
            // Khi edit, giữ poster cũ nếu không upload mới
            Optional<Movie> existingMovie = movieService.findById(movie.getId());
            if (existingMovie.isPresent() && (movie.getPosterUrl() == null || movie.getPosterUrl().isEmpty())) {
                movie.setPosterUrl(existingMovie.get().getPosterUrl());
            }
        }

        try {
            boolean isNew = (movie.getId() == null);
            movieService.save(movie);
            redirectAttributes.addFlashAttribute("successMessage",
                    isNew ? "Thêm phim mới thành công!" : "Cập nhật thông tin phim thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu phim: " + e.getMessage());
        }

        return "redirect:/admin/movies";
    }

    // ==================== ẨN PHIM (SOFT DELETE: chuyển status → HIDDEN) ====================

    @PostMapping("/hide/{id}")
    public String hideMovie(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phim với ID: " + id);
            return "redirect:/admin/movies";
        }

        Movie movie = movieOpt.get();
        movie.setStatus(MovieStatus.HIDDEN);
        movieService.save(movie);
        redirectAttributes.addFlashAttribute("successMessage", "Đã ẩn phim \"" + movie.getTitle() + "\" khỏi trang khách hàng!");
        return "redirect:/admin/movies";
    }

    // ==================== XÓA PHIM (HARD DELETE) ====================

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Movie> movieOpt = movieService.findById(id);
            String title = movieOpt.map(Movie::getTitle).orElse("ID " + id);
            movieService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa phim \"" + title + "\" thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa phim này (có thể do phim đang có lịch chiếu hoặc dữ liệu liên quan)!");
        }
        return "redirect:/admin/movies";
    }
}
