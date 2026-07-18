package com.mtbw.movieticketbooking.dto;

// 1 nut gio chieu tren man hinh, da format san (vd time="18:00", priceLabel="55K")
// de template khong phai lam toan/format ngay - tranh loi va de doc hon.
public record ShowtimeSlot(Long showtimeId, String time, String priceLabel) {
}