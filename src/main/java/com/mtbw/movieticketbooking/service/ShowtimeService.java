package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.entity.Showtime;
import java.util.List;

public interface ShowtimeService {
    // Hàm lấy danh sách các suất chiếu diễn ra trong ngày hôm nay
    List<Showtime> getTodayShowtimes();
}