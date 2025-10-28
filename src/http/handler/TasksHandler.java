package http.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.ManagerSaveException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager) {
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
        if (path.equals("/tasks")) {
            // Получить все задачи
            sendSuccess(exchange, taskManager.getTasks());
        } else if (path.matches("/tasks/\\d+")) {
            // Получить задачу по ID
            String idStr = getPathParam(exchange, 2);
            try {
                int id = Integer.parseInt(idStr);
                Task task = taskManager.getTasks(id);
                if (task != null) {
                    sendSuccess(exchange, task);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID задачи");
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = readText(exchange);
            Task task = parseJson(body, Task.class);

            if (task.getId() == 0) {
                // Создание новой задачи
                int newId = taskManager.addNewTask(task);
                if (newId != 0) {
                    Task createdTask = taskManager.getTasks(newId);
                    sendCreated(exchange, createdTask);
                } else {
                    sendHasOverlaps(exchange);
                }
            } else {
                // Обновление существующей задачи
                taskManager.updateTask(task);
                sendSuccess(exchange, task);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный JSON");
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/tasks/\\d+")) {
            String idStr = getPathParam(exchange, 2);
            try {
                int id = Integer.parseInt(idStr);
                taskManager.deleteTask(id);
                sendText(exchange, "Задача удалена", 200);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный ID задачи");
            }
        } else if (path.equals("/tasks")) {
            // Удалить все задачи
            taskManager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}