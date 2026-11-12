package com.rctoyshop.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rctoyshop.backend.dto.AdminUserUpdateRequest;
import com.rctoyshop.backend.dto.UserCreateRequest;
import com.rctoyshop.backend.dto.UserResponse;
import com.rctoyshop.backend.dto.UserUpdateRequest;
import com.rctoyshop.backend.model.User;
import com.rctoyshop.backend.repository.UserRepository;

/**
 * 使用者服務：處理用戶註冊、登入、以及管理員 CRUD 邏輯。
 * 對外一律收送 DTO，Entity 只在這一層內部使用。
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* ─────────── Entity ↔ DTO 轉換 ─────────── */

    private UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setBirthday(user.getBirthday());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    /** 兩種更新共用的欄位。null 代表這次不改這一項。 */
    private void applyUpdatableFields(User user, UserUpdateRequest request) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
    }

    /* ─────────── 查詢 ─────────── */

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<UserResponse> findUserById(Integer id) {
        return userRepository.findById(id).map(this::toResponse);
    }

    /** 內部用（Spring Security 要比對密碼），不會變成 API 回應 */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /* ─────────── 新增 ─────────── */

    @Transactional
    public UserResponse registerNewUser(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setBirthday(request.getBirthday());

        // 以下由後端決定，不讓前端指定
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        return toResponse(userRepository.save(user));
    }

    /* ─────────── 更新 ─────────── */

    /** 一般使用者改自己的資料，改不到 status */
    @Transactional
    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        applyUpdatableFields(user, request);
        return toResponse(userRepository.save(user));
    }

    /** 管理員改使用者資料，多了 status */
    @Transactional
    public UserResponse adminUpdateUser(Integer id, AdminUserUpdateRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        applyUpdatableFields(user, request);
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        return toResponse(userRepository.save(user));
    }

    /* ─────────── 刪除 ─────────── */

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    /* ─────────── 登入驗證 ─────────── */

    public UserResponse authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        // Plain text comparison
        if (rawPassword.equals(user.getPassword())) {
            return toResponse(user);
        }
        return null;
    }
}
