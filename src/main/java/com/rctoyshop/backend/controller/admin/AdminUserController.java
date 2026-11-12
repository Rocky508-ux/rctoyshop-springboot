package com.rctoyshop.backend.controller.admin;

import com.rctoyshop.backend.dto.AdminUserUpdateRequest;
import com.rctoyshop.backend.dto.UserCreateRequest;
import com.rctoyshop.backend.dto.UserResponse;
import com.rctoyshop.backend.exception.NotFoundException;
import com.rctoyshop.backend.model.User;
import com.rctoyshop.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 專門處理管理者對用戶的操作 (Admin Panel)
 * Base Path: /api/admin/users
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/admin/users : 獲取所有用戶
     */
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    /**
     * POST /api/admin/users : 新增用戶
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.registerNewUser(request);
    }

    /**
     * PUT /api/admin/users/{id} : 更新用戶
     */
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Integer id,
                                   @Valid @RequestBody AdminUserUpdateRequest request) {
        UserResponse updated = userService.adminUpdateUser(id, request);
        if (updated == null) {
            throw new NotFoundException("找不到使用者 " + id);
        }
        return updated;
    }

    /**
     * DELETE /api/admin/users/{id} : 刪除用戶
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        // 安全檢查：防止刪除自己
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            User currentUser = userService.findUserByEmail(auth.getName());
            if (currentUser != null && currentUser.getId().equals(id)) {
                throw new IllegalArgumentException("無法刪除自己的帳號");
            }
        }

        userService.deleteUser(id);
    }
}
