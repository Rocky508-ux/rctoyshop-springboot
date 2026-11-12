package com.rctoyshop.backend.exception;

/**
 * 帳號或密碼錯誤時丟出，由 GlobalExceptionHandler 接住並回 401。
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
