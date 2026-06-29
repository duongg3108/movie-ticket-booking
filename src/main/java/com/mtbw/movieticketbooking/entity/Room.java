package com.mtbw.movieticketbooking.entity;

import com.mtbw.movieticketbooking.enums.RoomStatus;
import com.mtbw.movieticketbooking.enums.ScreenType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_type", nullable = false, length = 20)
    @Builder.Default
    private ScreenType screenType = ScreenType.STANDARD;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "total_columns", nullable = false)
    private Integer totalColumns;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoomStatus status = RoomStatus.ACTIVE;
}
