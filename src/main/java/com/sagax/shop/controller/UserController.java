package com.sagax.shop.controller;

import com.sagax.shop.model.dto.UserDto;
import com.sagax.shop.model.entity.User;
import com.sagax.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CASE 27: Exposing entity directly — returns User with password field,
    // internal IDs, and lazy-loaded collections. No DTO layer.
    // Jackson will serialize the password to JSON response!
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // CASE 18 (triggered here): getUserWithOrders() returns User entity
    // with uninitialized lazy 'orders' collection. When Jackson tries to
    // serialize user.getOrders(), it throws LazyInitializationException
    // because the transactional context is closed.
    @GetMapping("/{id}/with-orders")
    public ResponseEntity<User> getUserWithOrders(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithOrders(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers().stream()
                        .map(userService::toDto)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = userService.createUser(userDto);
        return ResponseEntity.ok(userService.toDto(user));
    }

    @GetMapping("/{id}/is-admin")
    public ResponseEntity<Boolean> isAdmin(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userService.isAdmin(user));
    }
}
