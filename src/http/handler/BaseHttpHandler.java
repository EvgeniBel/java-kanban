package http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendSuccess(HttpExchange exchange, Object data) throws IOException {
        String response = GSON.toJson(data);
        sendText(exchange, response, 200);
    }

    protected void sendCreated(HttpExchange exchange, Object data) throws IOException {
        String response = GSON.toJson(data);
        sendText(exchange, response, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача не найдена", 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача пересекается с существующей", 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendText(exchange, "Внутренняя ошибка сервера", 500);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 400);
    }

    protected String readText(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    protected <T> T parseJson(String json, Class<T> clazz) throws JsonSyntaxException {
        return GSON.fromJson(json, clazz);
    }

    protected String getPathParam(HttpExchange exchange, int index) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length > index) {
            return pathParts[index];
        }
        return null;
    }

    //доп метод для извлечения параметров из query string URL
    protected Integer getQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                    try {
                        return Integer.parseInt(keyValue[1]);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
