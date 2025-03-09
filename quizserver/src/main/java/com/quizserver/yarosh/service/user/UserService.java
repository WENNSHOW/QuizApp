package com.quizserver.yarosh.service.user;

import com.quizserver.yarosh.entities.Users;

public interface UserService {

    Users createUser(Users user);

    Boolean hasUserWithEmail(String email);
}
