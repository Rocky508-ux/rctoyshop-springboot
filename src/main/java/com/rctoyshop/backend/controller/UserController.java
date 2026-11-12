package com.rctoyshop.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rctoyshop.backend.dto.UserResponse;
import com.rctoyshop.backend.dto.UserUpdateRequest;
import com.rctoyshop.backend.exception.NotFoundException;
import com.rctoyshop.backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 獲取個人資料
    @GetMapping("/{id}")
    public UserResponse getUserProfile(@PathVariable Integer id) {
        return userService.findUserById(id)
                .orElseThrow(() -> new NotFoundException("找不到使用者 " + id));
    }

    // 更新個人資料
    @PutMapping("/{id}")
    public UserResponse updateUserProfile(@PathVariable Integer id,
                                          @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        if (user == null) {
            throw new NotFoundException("找不到使用者 " + id);
        }
        return user;
    }
}
