package com.mtbw.movieticketbooking.repository;

import com.mtbw.movieticketbooking.dto.CityOption;
import com.mtbw.movieticketbooking.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByChainId(Long chainId);
    List<Cinema> findByCity(String city);
    @Query("SELECT new com.mtbw.movieticketbooking.dto.CityOption(c.city, COUNT(c)) " +
            "FROM Cinema c WHERE c.status = com.mtbw.movieticketbooking.enums.CinemaStatus.ACTIVE " +
            "GROUP BY c.city ORDER BY c.city ASC")
    List<CityOption> findCityOptions();
}
