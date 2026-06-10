package com.library.service;

import com.library.dto.UserDto;
import com.library.exception.DuplicateEmailException;
import com.library.exception.UserNotFoundException;
import com.library.model.User;
import com.library.model.User.UserStatus;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return toDto(findUserById(id));
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return toDto(user);
    }

    public List<UserDto> searchByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + userDto.getEmail());
        }
        User user = toEntity(userDto);
        user.setStatus(UserStatus.ACTIVE);
        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = findUserById(id);
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        
        if (userDto.getStatus() != null) {
            // CORRECCIÓN LÍNEA 66: Convertimos el String del DTO al Enum de la entidad
            user.setStatus(UserStatus.valueOf(userDto.getStatus().toUpperCase()));
        }
        return toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    private UserDto toDto(User user) {
        String statusString = null;
        if (user.getStatus() != null) {
            statusString = user.getStatus().name();
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(statusString)
                .build();
    }

    private User toEntity(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}