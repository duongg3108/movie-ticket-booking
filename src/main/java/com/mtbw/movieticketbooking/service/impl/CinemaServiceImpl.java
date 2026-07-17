package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.dto.CityOption;
import com.mtbw.movieticketbooking.entity.Cinema;
import com.mtbw.movieticketbooking.entity.CinemaChain;
import com.mtbw.movieticketbooking.repository.CinemaRepository;
import com.mtbw.movieticketbooking.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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
    public List<CityOption> findCityOptions() {
        return cinemaRepository.findCityOptions();
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

    @Override
    public LinkedHashMap<CinemaChain, List<Cinema>> groupByChain(List<Cinema> cinemas) {
        // Gom thu cong theo chainId (khong dung CinemaChain lam key truc tiep trong
        // Collectors.groupingBy vi entity chua override equals()/hashCode()).
        LinkedHashMap<Long, List<Cinema>> byChainId = new LinkedHashMap<>();
        LinkedHashMap<Long, CinemaChain> chainCache = new LinkedHashMap<>();

        for (Cinema c : cinemas) {
            Long chainId = c.getChain().getId();
            byChainId.computeIfAbsent(chainId, k -> new java.util.ArrayList<>()).add(c);
            chainCache.putIfAbsent(chainId, c.getChain());
        }

        LinkedHashMap<CinemaChain, List<Cinema>> result = new LinkedHashMap<>();
        for (Long chainId : byChainId.keySet()) {
            result.put(chainCache.get(chainId), byChainId.get(chainId));
        }
        return result;
    }
}
