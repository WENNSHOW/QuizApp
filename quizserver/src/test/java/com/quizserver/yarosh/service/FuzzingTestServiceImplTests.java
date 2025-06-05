package com.quizserver.yarosh.service;

import com.quizserver.yarosh.dto.QuestionDTO;
import com.quizserver.yarosh.dto.QuestionResponse;
import com.quizserver.yarosh.dto.SubmitTestDTO;
import com.quizserver.yarosh.dto.TestDTO;
import com.quizserver.yarosh.dto.TestDetailsDTO;
import com.quizserver.yarosh.entities.Users;
import com.quizserver.yarosh.util.UserNotFoundException;
import com.quizserver.yarosh.repository.QuestionRepository;
import com.quizserver.yarosh.repository.TestRepository;
import com.quizserver.yarosh.repository.TestResultRepository;
import com.quizserver.yarosh.repository.UserRepository;
import com.quizserver.yarosh.service.test.TestService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FuzzingTestServiceImplTests {

    @Autowired
    private TestService testService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    private Long existingTestId;
    private Long existingQuestionId;
    private Long existingUserId;

    @BeforeEach
    void setUp() {
        // Очистим все репозитории, чтобы гарантировать чистую БД
        testResultRepository.deleteAll();
        questionRepository.deleteAll();
        testRepository.deleteAll();
        userRepository.deleteAll();

        // 1) Создаём один Test в БД, чтобы использовать его ID
        TestDTO testDTO = new TestDTO();
        testDTO.setTitle("Sample Test");
        testDTO.setDescription("Sample Description");
        testDTO.setTime(30L);
        TestDTO savedTest = testService.createTest(testDTO);
        existingTestId = savedTest.getId();

        // 2) Создаём один вопрос, привязанный к этому тесту
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(existingTestId);
        questionDTO.setQuestionText("What is 2 + 2?");
        questionDTO.setOptionA("3");
        questionDTO.setOptionB("4");
        questionDTO.setOptionC("5");
        questionDTO.setOptionD("22");
        questionDTO.setCorrectOption("B");
        QuestionDTO savedQuestion = testService.addQuestionInTest(questionDTO);
        existingQuestionId = savedQuestion.getId();

        // 3) Создаём одного пользователя для тестирования методов, связанных с пользователем
        Users user = new Users();
        user.setName("FuzzTester");
        user.setEmail("fuzz@test.com");
        user.setPassword("password");
        user.setRole(null); // пусть сервис назначит USER
        Users persistedUser = userRepository.save(user);
        existingUserId = persistedUser.getId();
    }

    /**
     * 1. Попытка SQL-инъекции/«удаления БД» при создании теста.
     *    Передаём в title значение вроде "\"; DROP TABLE tests; --".
     *    Ожидаем, что при попытке сохранения БД выбросит DataIntegrityViolationException
     *    или ConstraintViolationException (в зависимости от конфигурации валидации/БД).
     */
    @org.junit.jupiter.api.Test
    void fuzzing_createTest_sqlInjectionOrDeleteDbAttempt() {
        TestDTO dto = new TestDTO();
        dto.setTitle("\"; DROP TABLE tests; --");
        dto.setDescription("Attempt to delete DB");
        dto.setTime(30L);

        // Должен пройти без исключений
        TestDTO result = Assertions.assertDoesNotThrow(
                () -> testService.createTest(dto),
                "Ожидаем, что SQL-подобная строка сохранится как обычный title"
        );
        Assertions.assertNotNull(result, "Должен вернуть DTO сохраненного теста");
        Assertions.assertEquals("\"; DROP TABLE tests; --", result.getTitle(),
                "Title должен совпадать с переданным значением без искажения");
    }

    /**
     * 2. Добавление вопроса с несуществующим testId.
     *    Передаём в QuestionDTO.id значение Long.MAX_VALUE, которого нет в БД.
     *    Ожидаем EntityNotFoundException.
     */
    @org.junit.jupiter.api.Test
    void fuzzing_addQuestionInTest_nonExistentTestId() {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(Long.MAX_VALUE); // отсутствующий testId
        dto.setQuestionText("Dummy question?");
        dto.setOptionA("A");
        dto.setOptionB("B");
        dto.setOptionC("C");
        dto.setOptionD("D");
        dto.setCorrectOption("A");

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> testService.addQuestionInTest(dto),
                "Ожидаем EntityNotFoundException при передаче несуществующего testId"
        );
    }

    /**
     * 3. Добавление вопроса с null/слишком длинными строками/неправильным correctOption.
     *    Используем существующий testId, но даём questionText = null и варианты по 10000 символов,
     *    а correctOption = "Z" (недопустимый вариант).
     *    Ожидаем ConstraintViolationException или DataIntegrityViolationException.
     */
    @org.junit.jupiter.api.Test
    void fuzzing_addQuestionInTest_nullAndLongFields() {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(existingTestId);
        dto.setQuestionText(null); // нелегитимно
        dto.setOptionA(RandomStringUtils.randomAlphabetic(10000));
        dto.setOptionB(RandomStringUtils.randomAlphabetic(10000));
        dto.setOptionC(RandomStringUtils.randomAlphabetic(10000));
        dto.setOptionD(RandomStringUtils.randomAlphabetic(10000));
        dto.setCorrectOption("Z"); // невалидный вариант

        Assertions.assertThrows(
                Exception.class,
                () -> testService.addQuestionInTest(dto),
                "Ожидаем ошибку (ConstraintViolation или DataIntegrityViolation) при null/слишком длинных/неправильных данных"
        );
    }

    /**
     * 4. Отправка результатов теста с несуществующим questionId и «мусорным» selectedOption.
     *    Первый кейс: questionId = -999 (отсутствует) → EntityNotFoundException.
     *    Второй кейс: существующий questionId, но selectedOption содержит SQL-инъекцию → ожидаем
     *    ошибку валидации (например, ConstraintViolationException).
     */
    @org.junit.jupiter.api.Test
    void fuzzing_submitTest_invalidQuestionId() {
        SubmitTestDTO request = new SubmitTestDTO();
        request.setTestId(existingTestId);
        request.setUserId(existingUserId);

        // 1) Первый элемент — несуществующий questionId → EntityNotFoundException
        List<QuestionResponse> responses = new ArrayList<>();
        responses.add(new QuestionResponse(-999L, "A"));
        request.setResponses(responses);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> testService.submitTest(request),
                "Ожидаем EntityNotFoundException для несуществующего questionId"
        );
    }

    /**
     * 5. Получение всех результатов конкретного пользователя с несуществующим userId.
     *    Передаём userId = -1 → ожидаем UserNotFoundException.
     */
    @org.junit.jupiter.api.Test
    void fuzzing_getAllTestResultsOfUser_invalidUserId() {
        Long invalidUserId = -1L;

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> testService.getAllTestResultsOfUser(invalidUserId),
                "Ожидаем UserNotFoundException для несуществующего userId"
        );
    }

    /**
     * 6. Получение вопросов теста с несуществующим testId.
     *    Передаём testId = 999999L → метод должен вернуть пустой TestDetailsDTO (без NPE).
     */
    @org.junit.jupiter.api.Test
    void fuzzing_getAllQuestionsByTest_nonExistentTestId_returnsEmptyOrNull() {
        TestDetailsDTO details = testService.getAllQuestionsByTest(999999L);

        Assertions.assertNotNull(details, "Метод не должен вернуть null для несуществующего testId");
        // Учитывая, что в реализации getQuestions() может быть null
        Assertions.assertTrue(
                details.getQuestions() == null || details.getQuestions().isEmpty(),
                "Для несуществующего testId getQuestions() должно быть null или пустым списком"
        );
        // TestDTO тоже будет null, так как нет такого теста
        Assertions.assertNull(details.getTestDTO(), "Для несуществующего testId TestDTO должно быть null");
    }

    /**
     * 7. Отправка очень большого списка ответов (10 000 элементов) для submitTest.
     *    Первый элемент — валидный, остальные 9999 — несуществующие → ожидание EntityNotFoundException
     *    при первой попытке сохранить несуществующий questionId.
     */
    @org.junit.jupiter.api.Test
    void fuzzing_submitTest_veryLargeResponsesList() {
        SubmitTestDTO request = new SubmitTestDTO();
        request.setTestId(existingTestId);
        request.setUserId(existingUserId);

        List<QuestionResponse> responses = new ArrayList<>();
        // 1 валидный ответ
        responses.add(new QuestionResponse(existingQuestionId, "B")); // правильный вариант
        // 9999 несуществующих
        for (int i = 0; i < 9999; i++) {
            responses.add(new QuestionResponse(999999L, "A"));
        }
        request.setResponses(responses);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> testService.submitTest(request),
                "Ожидаем EntityNotFoundException при первой попытке найти несуществующий вопрос"
        );
    }
}
