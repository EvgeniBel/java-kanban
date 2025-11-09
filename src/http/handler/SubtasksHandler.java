package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
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
        if (path.equals("/subtasks")) {
            // Получить все подзадачи
            sendSuccess(exchange, taskManager.getSubtasks());
        } else if (path.matches("/subtasks/\\d+")) {
            // Получить подзадачу по ID
            String idStr = getPathParam(exchange, 2);
            try {
                int id = Integer.parseInt(idStr);
                Subtask subtask = taskManager.getSubtasks(id);
                if (subtask != null) {
                    sendSuccess(exchange, subtask);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID подзадачи");
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = readText(exchange);
            Subtask subtask = parseJson(body, Subtask.class);

            if (subtask.getId() == 0) {
                // Создание новой подзадачи
                Integer newId = taskManager.addNewSubtask(subtask);
                if (newId != null) {
                    Subtask createdSubtask = taskManager.getSubtasks(newId);
                    sendCreated(exchange, createdSubtask);
                } else {
                    sendHasOverlaps(exchange);
                }
            } else {
                // Обновление существующей подзадачи
                taskManager.updateSubtask(subtask);
                sendSuccess(exchange, subtask);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            String idStr = getPathParam(exchange, 2);
            try {
                int id = Integer.parseInt(idStr);
                taskManager.deleteSubtask(id);
                sendText(exchange, "Подзадача удалена", 200);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID подзадачи");
            }
        } else if (path.equals("/subtasks")) {
            // Удалить все подзадачи
            taskManager.deleteAllSubtasks();
            sendText(exchange, "Все подзадачи удалены", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}
