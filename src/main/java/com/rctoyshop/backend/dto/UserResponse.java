package com.rctoyshop.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 回傳給前端的使用者資料。
 * 刻意不包含 password，避免密碼跟著 API 回應外洩。
 */
@Data
public class UserResponse {

    private Integer id;
    private String email;
    private String name;
    private String phone;
    private LocalDate birthday;
    private String role;
    private String status;
    private LocalDateTime createdAt;
}
