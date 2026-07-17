package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.CityOption;
import com.mtbw.movieticketbooking.entity.Cinema;
import com.mtbw.movieticketbooking.entity.CinemaChain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public interface CinemaService {
    List<CityOption> findCityOptions();
    List<Cinema> findByCity(String city);
    Optional<Cinema> findById(Long id);

    // Gom danh sach rap theo he thong rap (chain) that su co trong DB, sap xep theo ten chain
    // -> dung de render cot "Rap chieu" giong Moveek (nhom theo Beta Cinemas, CGV, ...)
    LinkedHashMap<CinemaChain, List<Cinema>> groupByChain(List<Cinema> cinemas);
}