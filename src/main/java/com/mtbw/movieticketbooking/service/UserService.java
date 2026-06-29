package com.mtbw.movieticketbooking.service;

import com.mtbw.movieticketbooking.dto.UserRegisterDto;
import com.mtbw.movieticketbooking.entity.User;

import java.util.Optional;

public interface UserService {
    User registerCustomer(UserRegisterDto registerDto);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
