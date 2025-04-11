package com.quizserver.yarosh.controller;

import com.quizserver.yarosh.dto.QuestionDTO;
import com.quizserver.yarosh.dto.SubmitTestDTO;
import com.quizserver.yarosh.dto.TestDTO;
import com.quizserver.yarosh.service.test.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Создание теста",
            description = "Создает новый тест по данным, переданным в теле запроса."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тест успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка создания теста")
    })
    @PostMapping()
    public ResponseEntity<?>git  createTest(
            @RequestBody(
                    description = "Данные теста",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "TestDTOExample",
                                            value = "{\"id\": 1, \"title\": \"Sample Test\", " +
                                                    "\"description\": \"This is a sample test\", \"time\": 60}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody TestDTO dto) {
        try {
            return new ResponseEntity<>(testService.createTest(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Добавление вопроса в тест",
            description = "Добавляет новый вопрос в существующий тест."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Вопрос успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при добавлении вопроса")
    })
    @PostMapping("/question")
    public ResponseEntity<?> addQuestionInTest(
            @RequestBody(
                    description = "Данные вопроса",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QuestionDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "QuestionDTOExample",
                                            value = "{\"id\": 1, \"questionText\": \"What is Java?\", " +
                                                    "\"optionA\": \"Programming language\", \"optionB\": \"Coffee\", " +
                                                    "\"optionC\": \"Island\", \"optionD\": \"None\", " +
                                                    "\"correctOption\": \"optionA\"}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody QuestionDTO dto) {
        try {
            return new ResponseEntity<>(testService.addQuestionInTest(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получение всех тестов",
            description = "Возвращает список всех доступных тестов."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список тестов успешно получен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при получении списка тестов")
    })
    @GetMapping()
    public ResponseEntity<?> getAllTests() {
        try {
            return new ResponseEntity<>(testService.getAllTests(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получение вопросов теста",
            description = "Возвращает все вопросы для теста по указанному ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список вопросов успешно получен"),
            @ApiResponse(responseCode = "400", description = "Ошибка при получении вопросов")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getAllQuestions(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(testService.getAllQuestionsByTest(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Отправка теста",
            description = "Отправляет результаты теста для проверки."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты теста успешно отправлены"),
            @ApiResponse(responseCode = "400", description = "Ошибка при отправке результатов теста")
    })
    @PostMapping("/submit-test")
    public ResponseEntity<?> submitTest(
            @RequestBody(
                    description = "Данные для отправки теста",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubmitTestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "SubmitTestDTOExample",
                                            value = "{\"testId\": 1, \"userId\": 1, " +
                                                    "\"responses\": [{\"questionId\": 1, \"selectedOption\": \"optionA\"}]}"
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody SubmitTestDTO dto) {
        try {
            return new ResponseEntity<>(testService.submitTest(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получение всех результатов тестов",
            description = "Возвращает результаты всех тестов."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты успешно получены"),
            @ApiResponse(responseCode = "400", description = "Ошибка при получении результатов")
    })
    @GetMapping("/test-result")
    public ResponseEntity<?> getAllTestResults() {
        try {
            return new ResponseEntity<>(testService.getAllTestResults(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Получение результатов тестов пользователя",
            description = "Возвращает результаты тестов для конкретного пользователя по его ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты успешно получены"),
            @ApiResponse(responseCode = "400", description = "Ошибка при получении результатов")
    })
    @GetMapping("/test-result/{id}")
    public ResponseEntity<?> getAllTestResultsOfUser(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(testService.getAllTestResultsOfUser(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
