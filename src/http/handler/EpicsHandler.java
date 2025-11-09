package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            // Получить все эпики
            sendSuccess(exchange, taskManager.getEpics());
        } else if (path.matches("/epics/\\d+")) {
            String idStr = getPathParam(exchange, 2);
            try {
                int id = Integer.parseInt(idStr);

                if (path.endsWith("/subtasks")) {
                    // Получить подзадачи эпика
                    List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
                    sendSuccess(exchange, subtasks);
                } else {
                    // Получить эпик по ID
                    Epic epic = taskManager.getEpic(id);
                    if (epic != null) {
                        sendSuccess(exchange, epic);
                    } else {
                        sendNotFound(exchange);
                    }
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID эпика");
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = readText(exchange);
            Epic epic = parseJson(body, Epic.class);
            if (epic == null) {
                sendBadRequest(exchange, "Некорректные данные эпика");
                return;
            }

            if (epic.getId() == 0 || !taskManager.getEpics().contains(epic)) {
                // Создание нового эпика
                int newId = taskManager.addNewEpic(epic);
                Epic createdEpic = taskManager.getEpic(newId);
                sendCreated(exchange, createdEpic);
            } else {
                // Обновление существующего эпика
                taskManager.updateEpic(epic);
                sendSuccess(exchange, epic);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            String idStr = getPathParam(exchange, 2);
            try {
                int id = Integer.parseInt(idStr);
                taskManager.deleteEpic(id);
                sendText(exchange, "Эпик удален", 200);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID эпика");
            }
        } else if (path.equals("/epics")) {
            // Удалить все эпики
            taskManager.deleteAllEpics();
            sendText(exchange, "Все эпики удалены", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}