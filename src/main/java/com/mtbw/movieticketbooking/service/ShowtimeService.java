package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.MovieSchedule;
import com.mtbw.movieticketbooking.entity.Showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeService {

    // ===== Booking / Schedule =====
    List<MovieSchedule> getScheduleByCinemaAndDate(Long cinemaId, LocalDate date);

    List<Showtime> findUpcomingByMovie(Long movieId);

    // ===== Admin Showtime Management =====
    List<Showtime> findAll();

    Optional<Showtime> findById(Long id);

    List<Showtime> findByMovieId(Long movieId);

    List<Showtime> findByCinemaId(Long cinemaId);

    List<Showtime> findByMovieIdAndStartTimeBetween(
            Long movieId,
            LocalDateTime start,
            LocalDateTime end
    );

    Showtime save(Showtime showtime);

    void deleteById(Long id);
}