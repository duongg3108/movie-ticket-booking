package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.entity.Cinema;
import java.util.List;
import java.util.Optional;

public interface CinemaService {
    List<Cinema> findAll();
    List<Cinema> findByCity(String city);
    List<Cinema> findByChainId(Long chainId);
    Optional<Cinema> findById(Long id);
}
