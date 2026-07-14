package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.entity.Cinema;
import com.mtbw.movieticketbooking.repository.CinemaRepository;
import com.mtbw.movieticketbooking.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

    private final CinemaRepository cinemaRepository;

    @Override
    public List<Cinema> findAll() {
        return cinemaRepository.findAll();
    }

    @Override
    public List<Cinema> findByCity(String city) {
        return cinemaRepository.findByCity(city);
    }

    @Override
    public List<Cinema> findByChainId(Long chainId) {
        return cinemaRepository.findByChainId(chainId);
    }

    @Override
    public Optional<Cinema> findById(Long id) {
        return cinemaRepository.findById(id);
    }
}
