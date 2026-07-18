package com.mtbw.movieticketbooking.repository;

import com.mtbw.movieticketbooking.entity.Showtime;
import com.mtbw.movieticketbooking.enums.ShowtimeStatus;
import  com.mtbw.movieticketbooking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByMovieId(Long movieId);
    List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime start, LocalDateTime end);
    List<Showtime> findByRoomCinemaId(Long cinemaId);
    List<Showtime> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);
    List<Showtime> findByRoom_Cinema_IdAndStartTimeBetweenOrderByStartTimeAsc(
            Long cinemaId, LocalDateTime start, LocalDateTime end
    );
    List<Showtime> findByMovie_IdAndStartTimeAfterAndStatusNotOrderByStartTimeAsc(
            Long movieId, LocalDateTime from, ShowtimeStatus excludedStatus
    );
}
