package com.quizserver.yarosh.controller;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя, если email не занят. При успешной регистрации возвращается объект пользователя в формате JSON, в противном случае – сообщение об ошибке в формате JSON."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Users.class),
                            examples = {
                                    @ExampleObject(
                                            name = "UserSuccessResponse",
                                            value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"John Doe\", \"role\": \"USER\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "Пользователь с таким email уже существует или данные некорректны",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"User has already exists\"}"),
                            examples = {
                                    @ExampleObject(
                                            name = "UserErrorResponse",
                                            value = "{\"error\": \"User has already exists\"}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/sign-up")
    public ResponseEntity<?> signupUser(
            @RequestBody(
                    description = "Данные пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Users.class),
                            examples = {
                                    @ExampleObject(
                                            name = "UsersExample",
                                            value = "{\"id\": 1, \"email\": \"user@example.com\", " +
                                                    "\"password\": \"secret\", \"name\": \"John Doe\", " +
                                                    "\"role\": \"USER\"}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Users user) {

        if (userService.hasUserWithEmail(user.getEmail())) {
            return new ResponseEntity<>("{\"error\": \"User has already exists\"}", HttpStatus.NOT_ACCEPTABLE);
        }

        Users createdUser = userService.createUser(user);
        if (createdUser == null) {
            return new ResponseEntity<>("{\"error\": \"User cannot be created, try to fill all fields\"}", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет данные пользователя (email и пароль). При успешной аутентификации возвращается объект пользователя в формате JSON, иначе – сообщение об ошибке в формате JSON."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Users.class),
                            examples = {
                                    @ExampleObject(
                                            name = "UserLoginSuccessResponse",
                                            value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"John Doe\", \"role\": \"USER\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "Неверные учетные данные",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"error\": \"Username or password is incorrect\"}"),
                            examples = {
                                    @ExampleObject(
                                            name = "UserLoginErrorResponse",
                                            value = "{\"error\": \"Username or password is incorrect\"}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody(
                    description = "Данные для аутентификации пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Users.class),
                            examples = {
                                    @ExampleObject(
                                            name = "UsersLoginExample",
                                            value = "{\"email\": \"user@example.com\", " +
                                                    "\"password\": \"secret\"}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Users user) {
        Users dbUser = userService.login(user);

        if (dbUser == null) {
            return new ResponseEntity<>("{\"error\": \"Username or password is incorrect\"}", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(dbUser, HttpStatus.OK);
    }
}
