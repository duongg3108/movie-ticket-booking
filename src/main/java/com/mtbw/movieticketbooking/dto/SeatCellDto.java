package com.mtbw.movieticketbooking.dto;

import com.mtbw.movieticketbooking.enums.SeatCellStatus;
import com.mtbw.movieticketbooking.enums.SeatType;

// 1 o ghe tren seat map. seatId = null khi status = EMPTY (khong co ghe that o vi tri nay).
public record SeatCellDto(
        Long seatId,
        String rowLabel,
        Integer seatNumber,
        String seatCode,
        SeatType seatType,
        SeatCellStatus status
) {
}
