package com.mtbw.movieticketbooking.dto;

import com.mtbw.movieticketbooking.entity.Movie;

import java.util.List;

// 1 the phim tren man hinh lich chieu: thong tin phim + thoi luong da format ("1h30")
// + danh sach nhom suat chieu theo loai phong.
public record MovieSchedule(Movie movie, String durationLabel, List<ScreenGroup> screenGroups) {
}