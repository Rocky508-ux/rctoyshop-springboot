package com.rctoyshop.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 一般使用者更新自己的資料。
 * 沒有 status / role —— 使用者不能改自己的帳號狀態或權限。
 * 欄位都允許 null，代表「這次不改這一項」。
 */
@Data
public class UserUpdateRequest {

    @Email(message = "Email format is invalid")
    private String email;

    private String name;

    @Pattern(regexp = "^09\\d{8}$|^0\\d{1,2}-?\\d{6,8}$", message = "Phone must be a valid Taiwan phone number")
    private String phone;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    /** 留空代表不改密碼 */
    private String password;
}
