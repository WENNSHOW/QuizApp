package com.quizserver.yarosh.repository;

import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByRole(UserRole role);

    Users findFirsByEmail(String email);
}
