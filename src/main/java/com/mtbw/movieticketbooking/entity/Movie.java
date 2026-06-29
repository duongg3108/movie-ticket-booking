package com.mtbw.movieticketbooking.entity;

import com.mtbw.movieticketbooking.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(length = 80)
    private String genre;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "age_rating", length = 20)
    private String ageRating;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MovieStatus status = MovieStatus.NOW_SHOWING;
}
