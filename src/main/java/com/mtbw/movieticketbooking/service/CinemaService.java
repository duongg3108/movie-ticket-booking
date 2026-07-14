package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.CityOption;
import com.mtbw.movieticketbooking.entity.Cinema;
import com.mtbw.movieticketbooking.entity.CinemaChain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public interface CinemaService {

    List<CityOption> findCityOptions();

    List<Cinema> findAll();

    List<Cinema> findByCity(String city);

    List<Cinema> findByChainId(Long chainId);

    Optional<Cinema> findById(Long id);

    LinkedHashMap<CinemaChain, List<Cinema>> groupByChain(List<Cinema> cinemas);
}