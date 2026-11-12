package com.rctoyshop.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 註冊 / 管理員新增使用者時，前端送進來的資料。
 * 刻意不含 id、role、status、createdAt —— 這些由後端決定，不讓前端指定。
 */
@Data
public class UserCreateRequest {

    @NotBlank(message = "Email 不可空白")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "密碼不可空白")
    private String password;

    @NotBlank(message = "姓名不可空白")
    private String name;

    @Pattern(regexp = "^09\\d{8}$|^0\\d{1,2}-?\\d{6,8}$", message = "Phone must be a valid Taiwan phone number")
    private String phone;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;
}
