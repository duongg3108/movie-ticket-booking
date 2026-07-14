package com.mtbw.movieticketbooking.config;

import com.mtbw.movieticketbooking.entity.Movie;
import com.mtbw.movieticketbooking.entity.User;
import com.mtbw.movieticketbooking.enums.MovieStatus;
import com.mtbw.movieticketbooking.enums.Role;
import com.mtbw.movieticketbooking.enums.UserStatus;
import com.mtbw.movieticketbooking.repository.MovieRepository;
import com.mtbw.movieticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final MovieRepository movieRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                // Data is seeded via SQL script by user, but fallback if empty:
                if (userRepository.count() == 0) {
                        User admin = User.builder()
                                        .fullName("System Administrator")
                                        .email("admin@movie.test")
                                        .password(passwordEncoder.encode("123456"))
                                        .role(Role.ADMIN)
                                        .status(UserStatus.ACTIVE)
                                        .build();
                        userRepository.save(admin);
                }
        }
}
