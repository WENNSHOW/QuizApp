package com.quizserver.yarosh.service.user;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.enums.UserRole;
import com.quizserver.yarosh.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAdminUserCreation() {
        // После инициализации контекста метод @PostConstruct должен создать администратора
        Users admin = userRepository.findByRole(UserRole.ADMIN);
        Assertions.assertNotNull(admin, "Admin user should be created by postConstruct");
        Assertions.assertEquals("admin@gmail.com", admin.getEmail());
        Assertions.assertEquals("Admin", admin.getName());
        Assertions.assertEquals(UserRole.ADMIN, admin.getRole());
    }

    @Test
    void testHasUserWithEmail() {
        // Создаем нового пользователя
        Users user = new Users();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        userService.createUser(user);

        // Проверяем наличие пользователя по email
        Assertions.assertTrue(userService.hasUserWithEmail("john@example.com"));
        Assertions.assertFalse(userService.hasUserWithEmail("nonexistent@example.com"));
    }

    @Test
    void testCreateUser_InvalidFields() {
        // Попытка создать пользователя с отсутствующими обязательными полями
        Users user = new Users();
        user.setEmail("invalid@example.com");
        // name и password не установлены

        Users createdUser = userService.createUser(user);
        Assertions.assertNull(createdUser, "User should not be created with missing fields");
    }

    @Test
    void testCreateUser_Success() {
        // Создаем корректного пользователя
        Users user = new Users();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");

        Users createdUser = userService.createUser(user);
        Assertions.assertNotNull(createdUser);
        Assertions.assertNotNull(createdUser.getId(), "Created user should have an ID");
        Assertions.assertEquals(UserRole.USER, createdUser.getRole());
    }

    @Test
    void testLogin_Success() {
        // Создаем пользователя
        Users user = new Users();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("mypassword");
        userService.createUser(user);

        // Пытаемся войти с корректными данными
        Users loginAttempt = new Users();
        loginAttempt.setEmail("bob@example.com");
        loginAttempt.setPassword("mypassword");

        Users loggedInUser = userService.login(loginAttempt);
        Assertions.assertNotNull(loggedInUser);
        Assertions.assertEquals("Bob", loggedInUser.getName());
    }

    @Test
    void testLogin_Failure() {
        // Создаем пользователя
        Users user = new Users();
        user.setName("Charlie");
        user.setEmail("charlie@example.com");
        user.setPassword("charliepass");
        userService.createUser(user);

        // Пытаемся войти с неверным паролем
        Users loginAttempt = new Users();
        loginAttempt.setEmail("charlie@example.com");
        loginAttempt.setPassword("wrongpass");

        Users loggedInUser = userService.login(loginAttempt);
        Assertions.assertNull(loggedInUser, "Login should fail with wrong password");
    }
}
