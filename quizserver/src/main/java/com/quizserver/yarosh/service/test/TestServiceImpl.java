package com.quizserver.yarosh.service.test;

import com.quizserver.yarosh.dto.*;
import com.quizserver.yarosh.entities.Question;
import com.quizserver.yarosh.entities.Test;
import com.quizserver.yarosh.entities.TestResult;
import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.repository.QuestionRepository;
import com.quizserver.yarosh.repository.TestRepository;
import com.quizserver.yarosh.repository.TestResultRepository;
import com.quizserver.yarosh.repository.UserRepository;
import com.quizserver.yarosh.util.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository, QuestionRepository questionRepository, TestResultRepository testResultRepository, UserRepository userRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.testResultRepository = testResultRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TestDTO createTest(TestDTO dto) {
        Test test = new Test();

        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setTime(dto.getTime());

        return testRepository.save(test).getDto();
    }

    @Transactional
    public QuestionDTO addQuestionInTest(QuestionDTO dto) {
        Optional<Test> optionalTest = testRepository.findById(dto.getId());

        if (optionalTest.isPresent()) {
            Question question = new Question();

            question.setTest(optionalTest.get());
            question.setQuestionText(dto.getQuestionText());
            question.setOptionA(dto.getOptionA());
            question.setOptionB(dto.getOptionB());
            question.setOptionC(dto.getOptionC());
            question.setOptionD(dto.getOptionD());
            question.setCorrectOption(dto.getCorrectOption());

            return questionRepository.save(question).getDto();
        }
        throw new EntityNotFoundException("Test Not Found");
    }

    @Transactional(readOnly = true)
    public List<TestDTO> getAllTests(){
        return testRepository.findAll().stream().peek(
                test->test.setTime(test.getQuestions().size() * test.getTime())).toList()
                .stream().map(Test::getDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestDetailsDTO getAllQuestionsByTest(Long id){
        Optional<Test> optionalTest = testRepository.findById(id);
        TestDetailsDTO testDetailsDTO = new TestDetailsDTO();

        if (optionalTest.isPresent()) {
            TestDTO testDTO = optionalTest.get().getDto();
            testDTO.setTime(optionalTest.get().getQuestions().size() * optionalTest.get().getTime());

            testDetailsDTO.setTestDTO(testDTO);
            testDetailsDTO.setQuestions(optionalTest.get().getQuestions().stream().map(Question::getDto).toList());
            return testDetailsDTO;

        }
        return testDetailsDTO;
    }

    @Transactional
    public TestResultDTO submitTest(SubmitTestDTO request) {
        Test test = testRepository.findById(request.getTestId()).orElseThrow(() -> new EntityNotFoundException("Test Not Found"));

        Users user = userRepository.findById(request.getUserId()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        int correctAnswers = 0;
        for (QuestionResponse response : request.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId()).orElseThrow(() -> new EntityNotFoundException("Question Not Found"));

            if (question.getCorrectOption().equals(response.getSelectedOption())){
                correctAnswers++;
            }
        }

        int totalQuestions = test.getQuestions().size();

        double percentage = ((double) correctAnswers / totalQuestions) * 100;

        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setUser(user);
        testResult.setTotalQuestions(totalQuestions);
        testResult.setCorrectAnswers(correctAnswers);
        testResult.setPercentage(percentage);

        return testResultRepository.save(testResult).getDTO();
    }

    @Transactional(readOnly = true)
    public List<TestResultDTO> getAllTestResults(){
        List<TestResultDTO> dtos = testResultRepository.findAll()
                .stream()
                .map(TestResult::getDTO)
                .toList();

        return dtos.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                this::generateKey,
                                Function.identity(),
                                (dto1, dto2) -> dto1
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    @Transactional(readOnly = true)
    public List<TestResultDTO> getAllTestResultsOfUser(Long userId)  {
        try {
            userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
        List<TestResultDTO> dtos = testResultRepository.findAllByUserId(userId)
                .stream()
                .map(TestResult::getDTO)
                .toList();

        return dtos.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                this::generateKey,
                                Function.identity(),
                                (dto1, dto2) -> dto1
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    private String generateKey(Object dto) {
        StringBuilder keyBuilder = new StringBuilder();
        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("id".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                keyBuilder.append(value).append("_");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при доступе к полям объекта", e);
            } finally {
                field.setAccessible(false);
            }
        }
        return keyBuilder.toString();
    }
}
