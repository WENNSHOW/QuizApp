package com.quizserver.yarosh.repository;

import com.quizserver.yarosh.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
