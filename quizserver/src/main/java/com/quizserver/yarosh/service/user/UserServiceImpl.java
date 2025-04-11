package com.quizserver.yarosh.service.user;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.enums.UserRole;
import com.quizserver.yarosh.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    @Transactional
    protected void createAdminUser() {
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

    @Transactional
    public Users createUser(Users user){
        if (user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
            return null;
        }
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Users login(Users user){
        Optional<Users> optionalUser = userRepository.findByEmail(user.getEmail());

        if (optionalUser.isPresent() && user.getPassword().equals(optionalUser.get().getPassword())) {
            return optionalUser.get();
        }

        return null;
    }
}
