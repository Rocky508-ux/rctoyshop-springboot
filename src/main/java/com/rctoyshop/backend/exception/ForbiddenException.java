package com.rctoyshop.backend.exception;

/**
 * 帳號被停用等「身分正確但不准做」的情況，由 GlobalExceptionHandler 接住並回 403。
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
