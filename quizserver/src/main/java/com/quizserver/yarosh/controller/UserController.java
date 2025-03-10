package com.quizserver.yarosh.controller;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signupUser(@RequestBody Users user){
        if (userService.hasUserWithEmail(user.getEmail())){
            return new ResponseEntity<>("User has already exists", HttpStatus.NOT_ACCEPTABLE);
        }

        Users createdUser = userService.createUser(user);
        if (createdUser == null){
            return new ResponseEntity<>("User cannot be created, come again later", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user){
        Users dbUser = userService.login(user);

        if (dbUser == null){
            return new ResponseEntity<>("Username or password is incorrect", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(dbUser, HttpStatus.OK);
    }
}
