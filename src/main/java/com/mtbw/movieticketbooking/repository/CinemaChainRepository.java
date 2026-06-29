package com.mtbw.movieticketbooking.repository;

import com.mtbw.movieticketbooking.entity.CinemaChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaChainRepository extends JpaRepository<CinemaChain, Long> {
}
