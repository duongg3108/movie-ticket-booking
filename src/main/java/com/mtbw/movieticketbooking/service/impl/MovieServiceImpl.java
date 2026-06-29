package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import com.mtbw.movieticketbooking.repository.MovieRepository;
import com.mtbw.movieticketbooking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> findByStatus(MovieStatus status) {
        return movieRepository.findByStatus(status);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public Page<Movie> searchMovies(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return movieRepository.findAll(pageable);
        }
        return movieRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }
}
