package com.mtbw.movieticketbooking.repository;

import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByStatus(MovieStatus status);
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
