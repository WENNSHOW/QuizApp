package com.quizserver.yarosh.service.test;

import com.quizserver.yarosh.dto.QuestionDTO;
import com.quizserver.yarosh.dto.TestDTO;

public interface TestService {
    TestDTO createTest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);
}
