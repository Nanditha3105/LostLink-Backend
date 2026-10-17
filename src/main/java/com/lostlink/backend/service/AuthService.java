
package com.lostlink.backend.service;

import com.lostlink.backend.dto.AuthResponse;
import com.lostlink.backend.dto.LoginRequest;
import com.lostlink.backend.dto.RegisterRequest;
import com.lostlink.backend.entity.User;
import com.lostlink.backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                "Registration successful"
        );
    }

    public AuthResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest) {

        String email = request.getEmail().trim().toLowerCase();

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.getPassword()
                            )
                    );

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository
                            .SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid email or password"
                    ));

            return new AuthResponse(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    "Login successful"
            );

        } catch (org.springframework.security.core.AuthenticationException e) {
            SecurityContextHolder.clearContext();

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }
    }
}
