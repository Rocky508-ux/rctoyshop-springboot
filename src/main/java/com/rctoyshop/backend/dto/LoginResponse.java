package com.rctoyshop.backend.dto;

import lombok.Data;

/**
 * 登入成功時回傳給前端的資料。
 */
@Data
public class LoginResponse {

    private String token;
    private UserResponse user;
}
