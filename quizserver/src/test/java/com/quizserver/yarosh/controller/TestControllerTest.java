package com.quizserver.yarosh.controller;

import com.quizserver.yarosh.dto.*;
import com.quizserver.yarosh.service.test.TestService;
import com.quizserver.yarosh.util.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    @Mock
    private TestService testService;

    @InjectMocks
    private TestController testController;

    @Test
    void testCreateTest_success() {
        // Создаём тестовый DTO с произвольными данными
        TestDTO dto = new TestDTO();
        dto.setTitle("Sample Test");
        dto.setDescription("This is a sample test");

        // Фиктивный объект, который возвращает сервис
        TestDTO createdTest = new TestDTO();
        createdTest.setTitle("Sample Test");
        createdTest.setDescription("This is a sample test");
        createdTest.setId(1L);

        when(testService.createTest(dto)).thenReturn(createdTest);

        ResponseEntity<?> response = testController.createTest(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TestDTO returnedTest = (TestDTO) response.getBody();
        assertEquals("Sample Test", returnedTest.getTitle());
        assertEquals("This is a sample test", returnedTest.getDescription());
        assertEquals(1L, returnedTest.getId());
    }

    @Test
    void testCreateTest_failure() {
        TestDTO dto = new TestDTO();
        dto.setTitle("Faulty Test");

        when(testService.createTest(dto)).thenThrow(new RuntimeException("Creation error"));

        ResponseEntity<?> response = testController.createTest(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Creation error", response.getBody());
    }

    @Test
    void testAddQuestionInTest_success() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(1L);
        questionDTO.setQuestionText("What is the capital of France?");
        questionDTO.setCorrectOption("Paris");

        QuestionDTO returnedQuestion = new QuestionDTO();
        returnedQuestion.setId(1L);
        returnedQuestion.setQuestionText("What is the capital of France?");
        returnedQuestion.setCorrectOption("Paris");

        when(testService.addQuestionInTest(questionDTO)).thenReturn(returnedQuestion);

        ResponseEntity<?> response = testController.addQuestionInTest(questionDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        QuestionDTO result = (QuestionDTO) response.getBody();
        assertEquals("What is the capital of France?", result.getQuestionText());
        assertEquals("Paris", result.getCorrectOption());
        assertEquals(1L, result.getId());
    }

    @Test
    void testAddQuestionInTest_failure() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(1L);
        questionDTO.setQuestionText("Faulty question?");

        when(testService.addQuestionInTest(questionDTO)).thenThrow(new RuntimeException("Question error"));

        ResponseEntity<?> response = testController.addQuestionInTest(questionDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Question error", response.getBody());
    }

    @Test
    void testGetAllTests_success() {
        TestDTO test1 = new TestDTO();
        test1.setId(1L);
        test1.setTitle("Test 1");
        test1.setDescription("Description 1");

        List<TestDTO> testList = Collections.singletonList(test1);
        when(testService.getAllTests()).thenReturn(testList);

        ResponseEntity<?> response = testController.getAllTests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> returnedList = (List<?>) response.getBody();
        assertEquals(1, returnedList.size());
    }

    @Test
    void testGetAllTests_failure() {
        when(testService.getAllTests()).thenThrow(new RuntimeException("Get tests error"));

        ResponseEntity<?> response = testController.getAllTests();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Get tests error", response.getBody());
    }

    @Test
    void testGetAllQuestions_success() {
        TestDTO testDTO = new TestDTO();
        testDTO.setId(1L);
        testDTO.setTitle("Test 1");
        testDTO.setDescription("Description 1");
        testDTO.setTime(30L);

        // Подготавливаем список вопросов
        QuestionDTO question1 = new QuestionDTO();
        question1.setId(1L);
        question1.setQuestionText("What is 2+2?");
        question1.setCorrectOption("4");

        List<QuestionDTO> questions = Collections.singletonList(question1);

        // Создаем объект, который должен возвращаться сервисом
        TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
        testDetailsDTO.setTestDTO(testDTO);
        testDetailsDTO.setQuestions(questions);

        when(testService.getAllQuestionsByTest(1L)).thenReturn(testDetailsDTO);

        ResponseEntity<?> response = testController.getAllQuestions(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TestDetailsDTO returnedDetails = (TestDetailsDTO) response.getBody();
        assertNotNull(returnedDetails);
        assertNotNull(returnedDetails.getTestDTO());
        assertEquals(1L, returnedDetails.getTestDTO().getId());
        assertEquals("Test 1", returnedDetails.getTestDTO().getTitle());
        assertEquals(1, returnedDetails.getQuestions().size());
        QuestionDTO returnedQuestion = returnedDetails.getQuestions().get(0);
        assertEquals("What is 2+2?", returnedQuestion.getQuestionText());
        assertEquals("4", returnedQuestion.getCorrectOption());
    }

    @Test
    void testGetAllQuestions_failure() {
        when(testService.getAllQuestionsByTest(1L)).thenThrow(new RuntimeException("Get questions error"));

        ResponseEntity<?> response = testController.getAllQuestions(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Get questions error", response.getBody());
    }

    @Test
    void testSubmitTest_success() {
        SubmitTestDTO submitDTO = new SubmitTestDTO();
        submitDTO.setTestId(1L);
        submitDTO.setUserId(100L);

        QuestionResponse qr = new QuestionResponse();
        qr.setQuestionId(10L);
        qr.setSelectedOption("A");
        submitDTO.setResponses(Collections.singletonList(qr));

        TestResultDTO testResultDTO = new TestResultDTO();
        testResultDTO.setId(1L);
        testResultDTO.setTotalQuestions(1);
        testResultDTO.setCorrectAnswers(1);
        testResultDTO.setPercentage(100.0);
        testResultDTO.setTestName("Sample Test");
        testResultDTO.setUsername("testUser");

        when(testService.submitTest(submitDTO)).thenReturn(testResultDTO);

        ResponseEntity<?> response = testController.submitTest(submitDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TestResultDTO returnedResult = (TestResultDTO) response.getBody();
        assertNotNull(returnedResult);
        assertEquals(1L, returnedResult.getId());
        assertEquals(1, returnedResult.getTotalQuestions());
        assertEquals(1, returnedResult.getCorrectAnswers());
        assertEquals(100.0, returnedResult.getPercentage());
        assertEquals("Sample Test", returnedResult.getTestName());
        assertEquals("testUser", returnedResult.getUsername());
    }

    @Test
    void testSubmitTest_failure() {
        SubmitTestDTO submitDTO = new SubmitTestDTO();
        submitDTO.setTestId(1L);
        submitDTO.setUserId(100L);

        when(testService.submitTest(submitDTO)).thenThrow(new RuntimeException("Submit error"));

        ResponseEntity<?> response = testController.submitTest(submitDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Submit error", response.getBody());
    }

    @Test
    void testGetAllTestResults_success() {
        TestResultDTO result1 = new TestResultDTO();
        result1.setId(1L);
        result1.setTotalQuestions(10);
        result1.setCorrectAnswers(8);
        result1.setPercentage(80.0);
        result1.setTestName("Test 1");
        result1.setUsername("user1");

        List<TestResultDTO> results = Collections.singletonList(result1);

        when(testService.getAllTestResults()).thenReturn(results);

        ResponseEntity<?> response = testController.getAllTestResults();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> returnedResults = (List<?>) response.getBody();
        assertEquals(1, returnedResults.size());
    }

    @Test
    void testGetAllTestResults_failure() {
        when(testService.getAllTestResults()).thenThrow(new RuntimeException("Results error"));

        ResponseEntity<?> response = testController.getAllTestResults();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Results error", response.getBody());
    }

    @Test
    void testGetAllTestResultsOfUser_success() {
        TestResultDTO result1 = new TestResultDTO();
        result1.setId(1L);
        result1.setTotalQuestions(10);
        result1.setCorrectAnswers(9);
        result1.setPercentage(90.0);
        result1.setTestName("Test 1");
        result1.setUsername("user1");

        List<TestResultDTO> results = Collections.singletonList(result1);

        when(testService.getAllTestResultsOfUser(100L)).thenReturn(results);

        ResponseEntity<?> response = testController.getAllTestResultsOfUser(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> returnedResults = (List<?>) response.getBody();
        assertEquals(1, returnedResults.size());
    }

    @Test
    void testGetAllTestResultsOfUser_failure() {
        when(testService.getAllTestResultsOfUser(100L)).thenThrow(new UserNotFoundException("User Not Found"));

        ResponseEntity<?> response = testController.getAllTestResultsOfUser(100L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User Not Found", response.getBody());
    }
}
