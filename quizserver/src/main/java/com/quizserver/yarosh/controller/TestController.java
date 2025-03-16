package com.quizserver.yarosh.controller;

import com.quizserver.yarosh.dto.TestDTO;
import com.quizserver.yarosh.service.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/test")
@CrossOrigin("*")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping()
    public ResponseEntity<?> createTest(@RequestBody TestDTO dto) {
        try{
            return new ResponseEntity<>(testService.createTest(dto), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
