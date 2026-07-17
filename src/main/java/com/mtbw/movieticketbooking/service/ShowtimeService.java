package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.MovieSchedule;
import com.mtbw.movieticketbooking.entity.Showtime;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {
    /**
     * Lich chieu cua 1 rap trong 1 ngay, da gom theo phim va theo loai phong (screenType),
     * gio va gia da duoc format san (vd "18:00", "55K").
     */
    List<MovieSchedule> getScheduleByCinemaAndDate(Long cinemaId, LocalDate date);
    List<Showtime> findUpcomingByMovie(Long movieId);
}