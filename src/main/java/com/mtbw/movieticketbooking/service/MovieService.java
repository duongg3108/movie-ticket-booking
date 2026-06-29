package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<Movie> findAll();
    List<Movie> findByStatus(MovieStatus status);
    Optional<Movie> findById(Long id);
    Movie save(Movie movie);
    void deleteById(Long id);
    Page<Movie> searchMovies(String keyword, Pageable pageable);
}
