package com.mtbw.movieticketbooking.dto;

import java.util.List;

// 1 hang ghe (vd hang "A") gom danh sach cac o ghe theo dung thu tu cot trong phong.
public record SeatMapRowDto(String rowLabel, List<SeatCellDto> cells) {
}
