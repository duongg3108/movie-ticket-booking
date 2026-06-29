package com.mtbw.movieticketbooking.repository;

import com.mtbw.movieticketbooking.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByChainId(Long chainId);
    List<Cinema> findByCity(String city);
}
