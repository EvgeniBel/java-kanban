package handlerTest;

import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TasksHandlerTest extends BaseHttpTest {
    @Test
    void testGetAllTasks_WhenNoTasks_ShouldReturnEmptyList() throws Exception {
        HttpResponse<String> response = sendGet("/tasks");
        assertStatusCode(response, 200);

        // Десериализуем ответ для проверки
        Task[] tasks = fromJson(response.body(), Task[].class);
        assertEquals(0, tasks.length);
    }

    @Test
    void testCreateTask_ShouldReturnCreatedTask() throws Exception {
        // Создаем объект задачи и сериализуем в JSON
        Task task = new Task("Test Task", "Test Description", StatusTask.NEW);
        String taskJson = toJson(task);

        HttpResponse<String> response = sendPost("/tasks", taskJson);
        assertStatusCode(response, 201);

        // Десериализуем созданную задачу для проверки
        Task createdTask = fromJson(response.body(), Task.class);
        assertEquals("Test Task", createdTask.getName());
        assertEquals("Test Description", createdTask.getDescription());
        assertEquals(StatusTask.NEW, createdTask.getStatus());
        assertTrue(createdTask.getId() > 0);
    }

    @Test
    void testCreateTask_WithTime_ShouldReturnCreatedTask() throws Exception {
        // Создаем задачу с временными параметрами
        Task task = new Task("Task With Time", "Description", StatusTask.NEW,
                Duration.ofMinutes(90), // PT1H30M
                LocalDateTime.of(2024, 1, 1, 10, 0));
        String taskJson = toJson(task);

        HttpResponse<String> response = sendPost("/tasks", taskJson);
        assertStatusCode(response, 201);

        Task createdTask = fromJson(response.body(), Task.class);
        assertEquals("Task With Time", createdTask.getName());
        assertEquals(Duration.ofMinutes(90), createdTask.getDuration());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), createdTask.getStartTime());
    }

    @Test
    void testCreateTask_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        HttpResponse<String> response = sendPost("/tasks", invalidJson);
        assertStatusCode(response, 400);
    }

    @Test
    void testGetTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        // Создаем задачу через API
        Task task = new Task("Test Task", "Description", StatusTask.NEW);
        String taskJson = toJson(task);
        HttpResponse<String> createResponse = sendPost("/tasks", taskJson);
        assertStatusCode(createResponse, 201);
        int taskId = extractIdFromJson(createResponse.body());

        // Получаем задачу по ID
        HttpResponse<String> response = sendGet("/tasks/" + taskId);
        assertStatusCode(response, 200);

        Task retrievedTask = fromJson(response.body(), Task.class);
        assertEquals("Test Task", retrievedTask.getName());
        assertEquals(taskId, retrievedTask.getId());
    }

    @Test
    void testGetTaskById_WhenTaskNotExists_ShouldReturnNotFound() throws Exception {
        HttpResponse<String> response = sendGet("/tasks/999");
        assertStatusCode(response, 404);
    }

    @Test
    void testUpdateTask_ShouldUpdateSuccessfully() throws Exception {
        // Создаем задачу
        Task task = new Task("Original Task", "Description", StatusTask.NEW);
        String taskJson = toJson(task);
        HttpResponse<String> createResponse = sendPost("/tasks", taskJson);
        assertStatusCode(createResponse, 201);
        int taskId = extractIdFromJson(createResponse.body());

        // Обновляем задачу
        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", StatusTask.IN_PROGRESS);
        String updatedJson = toJson(updatedTask);

        HttpResponse<String> response = sendPost("/tasks", updatedJson);
        assertStatusCode(response, 200);

        Task responseTask = fromJson(response.body(), Task.class);
        assertEquals("Updated Task", responseTask.getName());
        assertEquals("Updated Description", responseTask.getDescription());
        assertEquals(StatusTask.IN_PROGRESS, responseTask.getStatus());
        assertEquals(taskId, responseTask.getId());
    }

    @Test
    void testUpdateTask_WithTime_ShouldUpdateSuccessfully() throws Exception {
        // Создаем задачу с временем
        Task task = new Task("Original Task", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 9, 0));
        String taskJson = toJson(task);
        HttpResponse<String> createResponse = sendPost("/tasks", taskJson);
        assertStatusCode(createResponse, 201);
        int taskId = extractIdFromJson(createResponse.body());

        // Обновляем задачу с новым временем
        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", StatusTask.IN_PROGRESS,
                Duration.ofHours(2), LocalDateTime.of(2024, 1, 1, 14, 0));
        String updatedJson = toJson(updatedTask);

        HttpResponse<String> response = sendPost("/tasks", updatedJson);
        assertStatusCode(response, 200);

        Task responseTask = fromJson(response.body(), Task.class);
        assertEquals("Updated Task", responseTask.getName());
        assertEquals(Duration.ofHours(2), responseTask.getDuration());
        assertEquals(LocalDateTime.of(2024, 1, 1, 14, 0), responseTask.getStartTime());
    }

    @Test
    void testDeleteTask_ShouldDeleteSuccessfully() throws Exception {
        // Создаем задачу
        Task task = new Task("Task to delete", "Description", StatusTask.NEW);
        String taskJson = toJson(task);
        HttpResponse<String> createResponse = sendPost("/tasks", taskJson);
        assertStatusCode(createResponse, 201);
        int taskId = extractIdFromJson(createResponse.body());

        // Удаляем задачу
        HttpResponse<String> deleteResponse = sendDelete("/tasks/" + taskId);
        assertStatusCode(deleteResponse, 200);

        // Проверяем, что задача удалена
        HttpResponse<String> getResponse = sendGet("/tasks/" + taskId);
        assertStatusCode(getResponse, 404);
    }

    @Test
    void testDeleteAllTasks_ShouldDeleteAllTasks() throws Exception {
        // Создаем несколько задач
        Task task1 = new Task("Task 1", "Desc 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Desc 2", StatusTask.NEW);

        String task1Json = toJson(task1);
        String task2Json = toJson(task2);

        sendPost("/tasks", task1Json);
        sendPost("/tasks", task2Json);

        // Удаляем все задачи
        HttpResponse<String> response = sendDelete("/tasks");
        assertStatusCode(response, 200);

        // Проверяем, что задач нет
        HttpResponse<String> getResponse = sendGet("/tasks");
        assertStatusCode(getResponse, 200);

        Task[] tasks = fromJson(getResponse.body(), Task[].class);
        assertEquals(0, tasks.length);
    }

    @Test
    void testCreateTask_WithTimeOverlap_ShouldReturnNotAcceptable() throws Exception {
        // Создаем первую задачу
        Task task1 = new Task("Task 1", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        String firstTaskJson = toJson(task1);

        HttpResponse<String> firstResponse = sendPost("/tasks", firstTaskJson);
        assertStatusCode(firstResponse, 201);

        // Пытаемся создать пересекающуюся задачу
        Task overlappingTask = new Task("Overlapping Task", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 30));
        String overlappingTaskJson = toJson(overlappingTask);

        HttpResponse<String> response = sendPost("/tasks", overlappingTaskJson);
        assertStatusCode(response, 406);
        assertTrue(response.body().contains("пересекается"));
    }

    @Test
    void testCreateTask_WithNoOverlap_ShouldSuccess() throws Exception {
        // Создаем первую задачу
        Task task1 = new Task("Task 1", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        String firstTaskJson = toJson(task1);

        HttpResponse<String> firstResponse = sendPost("/tasks", firstTaskJson);
        assertStatusCode(firstResponse, 201);

        // Создаем непересекающуюся задачу
        Task nonOverlappingTask = new Task("Non Overlapping Task", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 11, 30));
        String nonOverlappingTaskJson = toJson(nonOverlappingTask);

        HttpResponse<String> response = sendPost("/tasks", nonOverlappingTaskJson);
        assertStatusCode(response, 201);

        Task createdTask = fromJson(response.body(), Task.class);
        assertEquals("Non Overlapping Task", createdTask.getName());
    }

    @Test
    void testCreateMultipleTasks_ShouldAllBeReturned() throws Exception {
        // Создаем несколько задач
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.IN_PROGRESS);
        Task task3 = new Task("Task 3", "Description 3", StatusTask.DONE);

        String task1Json = toJson(task1);
        String task2Json = toJson(task2);
        String task3Json = toJson(task3);

        sendPost("/tasks", task1Json);
        sendPost("/tasks", task2Json);
        sendPost("/tasks", task3Json);

        // Получаем все задачи
        HttpResponse<String> response = sendGet("/tasks");
        assertStatusCode(response, 200);

        Task[] tasks = fromJson(response.body(), Task[].class);
        assertEquals(3, tasks.length);

        // Проверяем, что все задачи присутствуют
        boolean hasTask1 = false, hasTask2 = false, hasTask3 = false;
        for (Task task : tasks) {
            if (task.getName().equals("Task 1")) hasTask1 = true;
            if (task.getName().equals("Task 2")) hasTask2 = true;
            if (task.getName().equals("Task 3")) hasTask3 = true;
        }

        assertTrue(hasTask1, "Должна содержать Task 1");
        assertTrue(hasTask2, "Должна содержать Task 2");
        assertTrue(hasTask3, "Должна содержать Task 3");
    }

    @Test
    void testCreateTask_WithNullTime_ShouldSuccess() throws Exception {
        // Создаем задачу без времени (null значения)
        Task task = new Task("Task Without Time", "Description", StatusTask.NEW);
        // Явно устанавливаем null, если конструктор этого не делает
        task.setStartTime(null);
        task.setDuration(null);

        String taskJson = toJson(task);

        HttpResponse<String> response = sendPost("/tasks", taskJson);
        assertStatusCode(response, 201);

        Task createdTask = fromJson(response.body(), Task.class);
        assertEquals("Task Without Time", createdTask.getName());
        assertNull(createdTask.getStartTime());
        assertNull(createdTask.getDuration());
    }
}