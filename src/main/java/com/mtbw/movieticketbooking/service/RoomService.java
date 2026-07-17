package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.entity.Room;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> findAll();
    List<Room> findByCinemaId(Long cinemaId);
    Optional<Room> findById(Long id);
}
