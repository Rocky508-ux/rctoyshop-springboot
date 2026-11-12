package com.rctoyshop.backend.controller;

import com.rctoyshop.backend.dto.LoginRequest;
import com.rctoyshop.backend.dto.LoginResponse;
import com.rctoyshop.backend.dto.UserCreateRequest;
import com.rctoyshop.backend.dto.UserResponse;
import com.rctoyshop.backend.exception.ForbiddenException;
import com.rctoyshop.backend.exception.UnauthorizedException;
import com.rctoyshop.backend.security.JwtUtil;
import com.rctoyshop.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 前端登入與註冊的入口。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /** 註冊 */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.registerNewUser(request);
    }

    /** 登入 */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (DisabledException e) {
            throw new ForbiddenException("帳戶已被停用，請聯繫管理員");
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("帳號或密碼錯誤");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        UserResponse user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        String status = user.getStatus();
        if ("停用".equals(status) || "DISABLED".equalsIgnoreCase(status)) {
            throw new ForbiddenException("帳戶已被停用，請聯繫管理員");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);
        return response;
    }
}
