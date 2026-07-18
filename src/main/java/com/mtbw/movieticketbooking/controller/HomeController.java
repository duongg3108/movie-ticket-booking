package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import com.mtbw.movieticketbooking.service.MovieService;
import com.mtbw.movieticketbooking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MovieService movieService;

    private final ShowtimeService showtimeService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("nowShowingMovies", movieService.findByStatus(MovieStatus.NOW_SHOWING));
        model.addAttribute("comingSoonMovies", movieService.findByStatus(MovieStatus.COMING_SOON));
        return "index";
    }
    @GetMapping("/movies/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phim không tồn tại"));

        model.addAttribute("movie", movie);
        model.addAttribute("upcomingShowtimes", showtimeService.findUpcomingByMovie(id));
        return "MovieDetail";
    }



}
