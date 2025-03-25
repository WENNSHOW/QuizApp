package com.quizserver.yarosh.service.test;

import com.quizserver.yarosh.dto.QuestionDTO;
import com.quizserver.yarosh.dto.TestDTO;
import com.quizserver.yarosh.dto.TestDetailsDTO;
import com.quizserver.yarosh.entities.Question;
import com.quizserver.yarosh.entities.Test;
import com.quizserver.yarosh.repository.QuestionRepository;
import com.quizserver.yarosh.repository.TestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository, QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
    }

    public TestDTO createTest(TestDTO dto) {
        Test test = new Test();

        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setTime(dto.getTime());

        return testRepository.save(test).getDto();
    }

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

    public List<TestDTO> getAllTests(){
        return testRepository.findAll().stream().peek(
                test->test.setTime(test.getQuestions().size() * test.getTime())).toList()
                .stream().map(Test::getDto).collect(Collectors.toList());
    }

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
}
