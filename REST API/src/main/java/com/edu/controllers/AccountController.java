package com.edu.controllers;

import com.edu.exceptions.RequestError;
import com.edu.models.entities.Account;
import com.edu.repositories.AccountRepository;
import com.edu.utils.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtils jwtTokenUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/updateLogin/{id}")
    public ResponseEntity<?> updateLogin(@PathVariable(value = "id") int id, @RequestBody Map<String, Object> request) {
        String login = request.get("login").toString();
        var account = accountRepository.findById(id).orElseThrow();

        if (accountRepository.findByLogin(login).isPresent()) {
            return new ResponseEntity<>(new RequestError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным логином уже существует"), HttpStatus.BAD_REQUEST);
        }

        account.setLogin(login);
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkPassword/{id}")
    public ResponseEntity<?> checkPassword(@PathVariable(value = "id") int id, @RequestBody Map<String, Object> request) {
        String password = request.get("password").toString();
        var account = accountRepository.findById(id).orElseThrow();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getLogin(), password));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new RequestError(HttpStatus.BAD_REQUEST.value(), "Неправильный пароль"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/updatePassword/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable(value = "id") int id, @RequestBody Map<String, Object> request) {
        String password = passwordEncoder.encode(request.get("password").toString());
        var account = accountRepository.findById(id).orElseThrow();

        account.setPassword(password);
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserAccount(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String accessToken = authHeader.substring(7);
        String login = jwtTokenUtils.getUsername(accessToken);

        var account = accountRepository.findByLogin(login).orElseThrow();
        var user = account.getUser();

        Map<String, Object> responseBody = Map.of(
                "userId", user.getId(),
                "lastName", user.getLastName(),
                "firstName", user.getFirstName(),
                "patronymic", user.getPatronymic(),
                "phoneNumber", user.getPhoneNumber(),
                "role", user.getRole().toString(),
                "accountId", account.getId(),
                "login", account.getLogin(),
                "password", account.getPassword()
        );

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
