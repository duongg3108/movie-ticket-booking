package com.mtbw.movieticketbooking.dto;

// Dung cho cot "Khu vuc": ten thanh pho + so luong rap ACTIVE trong thanh pho do (lay tu DB that,
// khong hardcode) de hien thi badge so giong Moveek (vd: Ha Noi 56)
public record CityOption(String city, Long cinemaCount) {
}