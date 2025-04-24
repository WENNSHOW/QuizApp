package com.quizserver.yarosh.controller;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testSignupUser_whenUserAlreadyExists() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(userService.hasUserWithEmail(user.getEmail())).thenReturn(true);

        ResponseEntity<?> response = userController.signupUser(user);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("{\"error\": \"User has already exists\"}", response.getBody());
    }

    @Test
    void testSignupUser_whenUserCannotBeCreated() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(userService.hasUserWithEmail(user.getEmail())).thenReturn(false);
        when(userService.createUser(user)).thenReturn(null);

        ResponseEntity<?> response = userController.signupUser(user);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("{\"error\": \"User cannot be created, try to fill all fields\"}", response.getBody());
    }

    @Test
    void testSignupUser_whenUserCreatedSuccessfully() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("test");

        when(userService.hasUserWithEmail(user.getEmail())).thenReturn(false);
        when(userService.createUser(user)).thenReturn(user);

        ResponseEntity<?> response = userController.signupUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Users returnedUser = (Users) response.getBody();
        assertEquals(1L, returnedUser.getId());
        assertEquals("test", returnedUser.getPassword());
        assertEquals("test@test.com", returnedUser.getEmail());
    }

    @Test
    void testLogin_whenUserNotFound() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(userService.login(user)).thenReturn(null);

        ResponseEntity<?> response = userController.login(user);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("{\"error\": \"Username or password is incorrect\"}", response.getBody());
    }

    @Test
    void testLogin_whenUserFound() {
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("test");

        when(userService.login(user)).thenReturn(user);

        ResponseEntity<?> response = userController.login(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Users returnedUser = (Users) response.getBody();
        assertEquals(1L, returnedUser.getId());
        assertEquals("test", returnedUser.getPassword());
        assertEquals("test@test.com", returnedUser.getEmail());
    }
}
