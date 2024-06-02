package com.edu.controllers;

import com.edu.exceptions.RequestError;
import com.edu.repositories.UserRepository;
import com.edu.services.AccountService;
import com.edu.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AccountService accountService;
    private final UserRepository userRepository;

    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> loginRequest(@RequestBody Map<String, Object> request) {
        String login = request.get("login").toString();
        String password = request.get("password").toString();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new RequestError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = accountService.loadUserByUsername(login);
        Map<String, Object> responseBody = jwtTokenUtils.generateTokens(userDetails);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerRequest(@RequestBody Map<String, Object> request) {
        String phoneNumber = request.get("phoneNumber").toString();
        String login = request.get("login").toString();

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            return new ResponseEntity<>(new RequestError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным номером телефона уже существует"), HttpStatus.BAD_REQUEST);
        }

        if (accountService.findByLogin(login).isPresent()) {
            return new ResponseEntity<>(new RequestError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным логином уже существует"), HttpStatus.BAD_REQUEST);
        }

        accountService.createNewAccount(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String username;
        String refreshToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        refreshToken = authHeader.substring(7);

        try {
            username = jwtTokenUtils.getUsername(refreshToken);
        } catch (ExpiredJwtException | SignatureException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (username != null) {
            UserDetails userDetails = accountService.loadUserByUsername(username);
            Map<String, Object> responseBody = jwtTokenUtils.generateTokens(userDetails);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}