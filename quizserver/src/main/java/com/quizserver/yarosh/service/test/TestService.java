package com.quizserver.yarosh.service.test;

import com.quizserver.yarosh.dto.*;
import com.quizserver.yarosh.util.UserNotFoundException;

import java.util.List;

public interface TestService {
    TestDTO createTest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);

    List<TestDTO> getAllTests();

    TestDetailsDTO getAllQuestionsByTest(Long id);

    TestResultDTO submitTest(SubmitTestDTO request);

    List<TestResultDTO> getAllTestResults();

    List<TestResultDTO> getAllTestResultsOfUser(Long userId) throws UserNotFoundException;
}
