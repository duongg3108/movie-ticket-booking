package com.mtbw.movieticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cinema_chains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaChain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(length = 255)
    private String website;
}
