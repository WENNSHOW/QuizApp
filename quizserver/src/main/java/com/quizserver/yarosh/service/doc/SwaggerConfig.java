package com.quizserver.yarosh.service.doc;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI getApi() {
        return new OpenAPI()
                .servers(
                        List.of(
                                new Server().url("http://localhost:8080")
                        )
                )
                .info(new Info()
                        .title("Quiz Server API")
                        .version("1.0.0")
                        .description("API для работы с тестами и пользователями.\n\n" +
                                "Контроллер TestController:\n" +
                                " - **createTest**: Создает новый тест по данным, переданным в теле запроса.\n" +
                                " - **addQuestionInTest**: Добавляет вопрос в существующий тест.\n" +
                                " - **getAllTests**: Получает список всех тестов.\n" +
                                " - **getAllQuestions**: Получает все вопросы для указанного теста по его ID.\n" +
                                " - **submitTest**: Отправляет результаты теста для проверки.\n" +
                                " - **getAllTestResults**: Возвращает результаты всех тестов.\n" +
                                " - **getAllTestResultsOfUser**: Возвращает результаты тестов для конкретного пользователя по ID.\n\n" +
                                "Контроллер UserController:\n" +
                                " - **signupUser**: Регистрирует нового пользователя (если email ещё не используется).\n" +
                                " - **login**: Аутентифицирует пользователя по логину и паролю."
                        )
                );
    }
}
