package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.entity.Showtime;
import com.mtbw.movieticketbooking.repository.ShowtimeRepository;
import com.mtbw.movieticketbooking.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    @Override
    public List<Showtime> getTodayShowtimes() {
        // Thiết lập mốc thời gian bắt đầu ngày hôm nay (00:00:00)
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);

        // Thiết lập mốc thời gian kết thúc ngày hôm nay (23:59:59)
        LocalDateTime endOfToday = LocalDateTime.now().with(LocalTime.MAX);

        // Gọi Repo thực hiện truy vấn và trả về kết quả
        return showtimeRepository.findByStartTimeBetweenOrderByStartTimeAsc(startOfToday, endOfToday);
    }
}