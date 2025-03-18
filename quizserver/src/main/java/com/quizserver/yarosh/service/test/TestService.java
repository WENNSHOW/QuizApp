package com.quizserver.yarosh.service.test;

import com.quizserver.yarosh.dto.QuestionDTO;
import com.quizserver.yarosh.dto.TestDTO;

import java.util.List;

public interface TestService {
    TestDTO createTest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);

    List<TestDTO> getAllTests();
}
