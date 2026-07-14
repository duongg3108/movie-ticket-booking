package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.entity.Showtime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeService {
    List<Showtime> findAll();
    Optional<Showtime> findById(Long id);
    List<Showtime> findByMovieId(Long movieId);
    List<Showtime> findByCinemaId(Long cinemaId);
    List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime start, LocalDateTime end);
    Showtime save(Showtime showtime);
    void deleteById(Long id);
}
