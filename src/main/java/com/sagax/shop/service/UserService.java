package com.sagax.shop.service;

import com.sagax.shop.model.dto.UserDto;
import com.sagax.shop.model.entity.User;
import com.sagax.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // CASE 2: == vs equals on String.
    // Works sometimes due to String pool (literal "ADMIN" is interned),
    // but fails when role comes from DB (new String instance).
    public boolean isAdmin(User user) {
        return user.getRole() == "ADMIN"; // BUG: should be .equals("ADMIN")
    }

    // CASE 18: LazyInitializationException.
    // This method loads a User and returns it. The 'orders' collection is lazy-loaded.
    // When the controller (outside transactional context) accesses user.getOrders(),
    // it throws LazyInitializationException.
    @Transactional(readOnly = true)
    public User getUserWithOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // NOTE: orders are NOT initialized here — they are still a lazy proxy.
        // Returning this entity outside the transactional boundary will cause
        // LazyInitializationException when orders are accessed.
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword("default123"); // CASE 27 related: plain text password
        user.setRole("USER");
        return userRepository.save(user);
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }
}
