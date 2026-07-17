package com.mtbw.movieticketbooking.dto;

import java.util.List;

// Nhom cac suat chieu theo loai phong chieu THAT SU CO trong DB (rooms.screen_type:
// STANDARD / IMAX / VIP). Khong bia them dinh dang "2D Long Tieng"/"2D Phu De Viet"
// vi bang rooms/showtimes hien tai khong luu thong tin ngon ngu long tieng - phu de.
public record ScreenGroup(String screenTypeLabel, List<ShowtimeSlot> slots) {
}