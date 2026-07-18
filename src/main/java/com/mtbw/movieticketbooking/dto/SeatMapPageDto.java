package com.mtbw.movieticketbooking.dto;

import java.math.BigDecimal;
import java.util.List;

// Toan bo du lieu can thiet de render trang chon ghe (SeatMap.html) cho 1 suat chieu.
public record SeatMapPageDto(
        Long showtimeId,
        String movieTitle,
        String posterUrl,
        String cinemaName,
        String cinemaAddress,
        String roomName,
        String screenTypeLabel,
        String showDateLabel,
        String showTimeLabel,
        List<SeatMapRowDto> rows,
        Integer totalColumns,
        BigDecimal seatPrice,
        String seatPriceLabel
) {
}
