package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.enums.MovieStatus;
import com.mtbw.movieticketbooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MovieService movieService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("nowShowingMovies", movieService.findByStatus(MovieStatus.NOW_SHOWING));
        model.addAttribute("comingSoonMovies", movieService.findByStatus(MovieStatus.COMING_SOON));
        return "index";
    }
}
