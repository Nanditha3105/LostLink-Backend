package com.lostlink.backend.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.lostlink.backend.dto.AuthResponse;
import com.lostlink.backend.dto.LoginRequest;
import com.lostlink.backend.dto.RegisterRequest;
import com.lostlink.backend.entity.User;
import com.lostlink.backend.repository.UserRepository;
import com.lostlink.backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        AuthResponse response = authService.register(request);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(request.getEmail());
        loginRequest.setPassword(request.getPassword());
        authService.login(loginRequest, httpRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> currentUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Not authenticated"));

        return ResponseEntity.ok(new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                "Session active"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }
}
