package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.entity.Room;
import com.mtbw.movieticketbooking.repository.RoomRepository;
import com.mtbw.movieticketbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> findByCinemaId(Long cinemaId) {
        return roomRepository.findByCinemaId(cinemaId);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }
}
