package com.rctoyshop.backend.exception;

/**
 * 查不到資料時丟出，由 GlobalExceptionHandler 接住並回 404。
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
