package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.MovieSchedule;
import com.mtbw.movieticketbooking.entity.Showtime;

import java.time.LocalDate;
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

    /**
     * Lich chieu cua 1 rap trong 1 ngay, da gom theo phim va theo loai phong (screenType),
     * gio va gia da duoc format san (vd "18:00", "55K").
     */
    List<MovieSchedule> getScheduleByCinemaAndDate(Long cinemaId, LocalDate date);
    List<Showtime> findUpcomingByMovie(Long movieId);
}
