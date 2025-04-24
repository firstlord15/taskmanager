package org.ithub.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.dto.UserDto;
import org.ithub.taskmanager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        log.info("Открыта страница входа");

        if (error != null) {
            model.addAttribute("errorMessage", "Неверные учетные данные");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы успешно вышли из системы");
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        log.info("Открыта страница регистрации");
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute UserDto userDto,
                                 @RequestParam String password,
                                 Model model) {
        log.info("Регистрация нового пользователя: {}", userDto.getEmail());

        try {
            userService.registerUser(userDto, password);
            log.info("Пользователь {} успешно зарегистрирован", userDto.getEmail());
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя: {}", e.getMessage());
            model.addAttribute("errorMessage", "Ошибка при регистрации: " + e.getMessage());
            return "register";
        }
    }
}
