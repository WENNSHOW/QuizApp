package com.quizserver.yarosh.service.user;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.enums.UserRole;
import com.quizserver.yarosh.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void createAdminUser() {
        Users optionalUser = userRepository.findByRole(UserRole.ADMIN);
        if (optionalUser == null) {
            Users user = new Users();

            user.setName("Admin");
            user.setEmail("admin@gmail.com");
            user.setRole(UserRole.ADMIN);
            user.setPassword("admin");

            userRepository.save(user);
        }

    }


    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirsByEmail(email) != null;
    }
}
