package org.ithub.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.dto.UserDto;
import org.ithub.taskmanager.entity.User;
import org.ithub.taskmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserDto getUserDtoEntityByEmail(String email) {
        log.info("Find user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return toDTO(user);
    }

    public User getUserEntityByEmail(String email) {
        log.info("Получение полного объекта пользователя по email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public UserDto getUserId(Long id) {
        log.info("Find user with id: {}", id);
        return toDTO(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't found user with id: " + id)));
    }

    public List<UserDto> getAll() {
        log.info("Find all user");
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public void deleteById(Long id) {
        log.info("Delete user with id: {}", id);
        userRepository.deleteById(id);
    }

    public UserDto registerUser(UserDto dto, String rawPassword) {
        String hashedPassword = encoder.encode(rawPassword);
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        return toDTO(savedUser);
    }

    public UserDto updateById(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Users not found"));

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        User updatedUser = userRepository.save(user);
        return toDTO(updatedUser);
    }

    private UserDto toDTO(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
