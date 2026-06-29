package com.mtbw.movieticketbooking.controller;

import com.mtbw.movieticketbooking.dto.UserRegisterDto;
import com.mtbw.movieticketbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserRegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserRegisterDto registerDto,
                               BindingResult result, Model model) {
        if (userService.existsByEmail(registerDto.getEmail())) {
            result.rejectValue("email", "error.userDto", "Email này đã được đăng ký!");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        userService.registerCustomer(registerDto);
        return "redirect:/login?registered";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}
