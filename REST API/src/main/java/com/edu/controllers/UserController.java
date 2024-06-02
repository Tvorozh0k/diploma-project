package com.edu.controllers;

import com.edu.exceptions.RequestError;
import com.edu.models.entities.User;
import com.edu.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(value = "id") int id, @RequestBody Map<String, Object> request) {
        User user = userRepository.findById(id).orElseThrow();

        for (var pair : request.entrySet()) {
            String key = pair.getKey();
            String value = pair.getValue().toString();

            switch (key) {
                case "lastName" -> user.setLastName(value);
                case "firstName" -> user.setFirstName(value);
                case "patronymic" -> user.setPatronymic(value);
                case "phoneNumber" -> {
                    if (userRepository.findByPhoneNumber(value).isPresent()) {
                        return new ResponseEntity<>(new RequestError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным номером телефона уже существует"), HttpStatus.BAD_REQUEST);
                    } else {
                        user.setPhoneNumber(value);
                    }
                }
            }
        }

        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }
}