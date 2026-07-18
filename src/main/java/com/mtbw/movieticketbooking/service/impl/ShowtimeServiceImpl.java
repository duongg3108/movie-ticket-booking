package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.dto.MovieSchedule;
import com.mtbw.movieticketbooking.dto.ScreenGroup;
import com.mtbw.movieticketbooking.dto.ShowtimeSlot;
import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.entity.Showtime;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import com.mtbw.movieticketbooking.enums.ScreenType;
import com.mtbw.movieticketbooking.enums.ShowtimeStatus;
import com.mtbw.movieticketbooking.repository.MovieRepository;
import com.mtbw.movieticketbooking.repository.ShowtimeRepository;
import com.mtbw.movieticketbooking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    @Override
    public List<Showtime> findAll() {
        return showtimeRepository.findAll();
    }

    @Override
    public Optional<Showtime> findById(Long id) {
        return showtimeRepository.findById(id);
    }

    @Override
    public List<Showtime> findByMovieId(Long movieId) {
        return showtimeRepository.findByMovieId(movieId);
    }

    @Override
    public List<Showtime> findByCinemaId(Long cinemaId) {
        return showtimeRepository.findByRoomCinemaId(cinemaId);
    }

    @Override
    public List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime start, LocalDateTime end) {
        return showtimeRepository.findByMovieIdAndStartTimeBetween(movieId, start, end);
    }

    @Override
    public Showtime save(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }

    @Override
    public void deleteById(Long id) {
        showtimeRepository.deleteById(id);
    }

    @Override
    public List<Showtime> getTodayShowtimes() {
        // Thiết lập mốc thời gian bắt đầu ngày hôm nay (00:00:00)
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        // Thiết lập mốc thời gian kết thúc ngày hôm nay (23:59:59)
        LocalDateTime endOfToday = LocalDateTime.now().with(LocalTime.MAX);
        // Gọi Repo thực hiện truy vấn và trả về kết quả
        return showtimeRepository.findByStartTimeBetweenOrderByStartTimeAsc(startOfToday, endOfToday);
    }

    @Override
    public List<MovieSchedule> getScheduleByCinemaAndDate(Long cinemaId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Showtime> showtimes = showtimeRepository
                .findByRoom_Cinema_IdAndStartTimeBetweenOrderByStartTimeAsc(cinemaId, start, end);

        // Group showtimes by movieId
        LinkedHashMap<Long, List<Showtime>> byMovieId = new LinkedHashMap<>();
        for (Showtime s : showtimes) {
            if (s.getStatus() == ShowtimeStatus.CANCELLED) continue;
            Long movieId = s.getMovie().getId();
            byMovieId.computeIfAbsent(movieId, k -> new ArrayList<>()).add(s);
        }

        // Fetch all active movies (NOW_SHOWING)
        List<Movie> activeMovies = movieRepository.findByStatus(MovieStatus.NOW_SHOWING);

        // Union of active movies + any other movie that has showtimes on this date
        LinkedHashMap<Long, Movie> moviesToInclude = new LinkedHashMap<>();
        for (Movie m : activeMovies) {
            moviesToInclude.put(m.getId(), m);
        }
        for (Showtime s : showtimes) {
            if (s.getStatus() == ShowtimeStatus.CANCELLED) continue;
            Movie m = s.getMovie();
            if (m.getStatus() != MovieStatus.ENDED && m.getStatus() != MovieStatus.HIDDEN) {
                moviesToInclude.putIfAbsent(m.getId(), m);
            }
        }

        List<MovieSchedule> result = new ArrayList<>();
        for (Long movieId : moviesToInclude.keySet()) {
            Movie movie = moviesToInclude.get(movieId);
            List<Showtime> movieShowtimes = byMovieId.getOrDefault(movieId, new ArrayList<>());

            LinkedHashMap<ScreenType, List<ShowtimeSlot>> byScreenType = new LinkedHashMap<>();
            for (Showtime s : movieShowtimes) {
                ScreenType type = s.getRoom().getScreenType();
                ShowtimeSlot slot = new ShowtimeSlot(
                        s.getId(),
                        s.getStartTime().format(TIME_FMT),
                        formatPrice(s.getPrice())
                );
                byScreenType.computeIfAbsent(type, k -> new ArrayList<>()).add(slot);
            }

            List<ScreenGroup> groups = new ArrayList<>();
            for (ScreenType type : byScreenType.keySet()) {
                groups.add(new ScreenGroup(screenTypeLabel(type), byScreenType.get(type)));
            }

            result.add(new MovieSchedule(movie, formatDuration(movie.getDurationMinutes()), groups));
        }

        return result;
    }

    @Override
    public List<Showtime> findUpcomingByMovie(Long movieId) {
        return showtimeRepository.findByMovie_IdAndStartTimeAfterAndStatusNotOrderByStartTimeAsc(
                movieId, LocalDateTime.now(), ShowtimeStatus.CANCELLED
        );
    }

    private String formatPrice(java.math.BigDecimal price) {
        long thousand = price.longValue() / 1000;
        return thousand + "K";
    }

    private String formatDuration(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return h > 0 ? String.format("%dh%02d'", h, m) : String.format("%d'", m);
    }

    // Nhãn hiển thị cho từng loại phòng - dịch từ enum THẬT trong DB (rooms.screen_type)
    private String screenTypeLabel(ScreenType type) {
        return switch (type) {
            case STANDARD -> "Phòng chiếu 2D";
            case IMAX -> "Phòng chiếu IMAX";
            case VIP -> "Phòng chiếu VIP";
        };
    }
}
