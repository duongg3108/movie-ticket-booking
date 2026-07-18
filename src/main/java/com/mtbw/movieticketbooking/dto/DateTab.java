package com.mtbw.movieticketbooking.dto;

import java.time.LocalDate;

// record giúp tránh lỗi format ngày trong Thymeleaf, tính label ("Hôm nay", "Thứ 5"...) sẵn ở Controller
public record DateTab(LocalDate date, String dayLabel) {
}