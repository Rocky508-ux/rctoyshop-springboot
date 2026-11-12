package com.rctoyshop.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理員更新使用者資料。
 * 比 UserUpdateRequest 多一個 status（停用 / 啟用帳號）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUserUpdateRequest extends UserUpdateRequest {

    private String status;
}
