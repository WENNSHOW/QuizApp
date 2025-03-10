package com.quizserver.yarosh.repository;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByRole(UserRole role);

    Users findFirsByEmail(String email);

    Optional<Users> findByEmail(String email);
}
