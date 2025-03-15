package com.quizserver.yarosh.service.test;

import com.quizserver.yarosh.dto.TestDTO;
import com.quizserver.yarosh.entities.Test;
import com.quizserver.yarosh.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public TestDTO createTest(TestDTO dto) {
        Test test = new Test();

        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setTime(dto.getTime());

        return testRepository.save(test).getDto();
    }
}
