package handlerTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import http.handler.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseHttpTest {
    protected static final String BASE_URL = "http://localhost:8080";
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected HttpTaskServer taskServer;
    protected TaskManager taskManager;
    protected HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        client = HttpClient.newHttpClient();

        // Очищаем данные перед каждым тестом
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (taskServer != null) {
            Thread.sleep(500);
            taskServer.stop();
            Thread.sleep(500);
        }
    }

    protected HttpRequest.Builder createRequestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json");
    }

    protected void assertStatusCode(HttpResponse<String> response, int expectedStatus, String message) {
        assertEquals(expectedStatus, response.statusCode(), message);
    }

    protected void assertStatusCode(HttpResponse<String> response, int expectedStatus) {
        assertStatusCode(response, expectedStatus,
                "Неверный статус код. Ожидался: " + expectedStatus +
                        ", получен: " + response.statusCode() +
                        ". Тело ответа: " + response.body());
    }

    protected HttpResponse<String> sendGet(String path) throws Exception {
        HttpRequest request = createRequestBuilder(path)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> sendPost(String path, String body) throws Exception {
        HttpRequest request = createRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> sendDelete(String path) throws Exception {
        HttpRequest request = createRequestBuilder(path)
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected int extractIdFromJson(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            return jsonObject.get("id").getAsInt();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось извлечь ID из JSON: " + json, e);
        }
    }

    // Новый метод для сериализации объектов в JSON
    protected String toJson(Object object) {
        return GSON.toJson(object);
    }

    // Новый метод для десериализации JSON в объект
    protected <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }
}