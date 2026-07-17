package com.mtbw.movieticketbooking.enums;

// Trang thai HIEN THI cua 1 o ghe tren seat map - khong luu DB, chi tinh toan
// tai thoi diem render dua tren Seat.active va cac BookingSeat da co cho suat chieu do.
public enum SeatCellStatus {
    EMPTY,        // vi tri khong co ghe (loi di / khoang trong trong phong)
    AVAILABLE,    // ghe con trong, co the chon
    UNAVAILABLE,  // ghe bi khoa (Seat.active = false), khong the chon
    SOLD          // ghe da ban hoac dang bi nguoi khac giu (chua het han)
}
