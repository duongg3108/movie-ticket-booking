package com.mtbw.movieticketbooking.service.impl;

import com.mtbw.movieticketbooking.dto.UserRegisterDto;
import com.mtbw.movieticketbooking.entity.User;
import com.mtbw.movieticketbooking.enums.Role;
import com.mtbw.movieticketbooking.enums.UserStatus;
import com.mtbw.movieticketbooking.repository.UserRepository;
import com.mtbw.movieticketbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerCustomer(UserRegisterDto registerDto) {
        User user = User.builder()
                .fullName(registerDto.getFullName())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .phone(registerDto.getPhone())
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
