package handlerTest;

import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest extends BaseHttpTest {

    @Test
    void testGetHistoryAfterViewingTasks() throws Exception {
        // Создаем задачи через GSON
        Task task1 = new Task("Task 1", "Description", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description", StatusTask.NEW);

        String task1Json = toJson(task1);
        String task2Json = toJson(task2);

        HttpResponse<String> task1Response = sendPost("/tasks", task1Json);
        HttpResponse<String> task2Response = sendPost("/tasks", task2Json);
        int taskId1 = extractIdFromJson(task1Response.body());
        int taskId2 = extractIdFromJson(task2Response.body());

        // Просматриваем задачи
        sendGet("/tasks/" + taskId1);
        sendGet("/tasks/" + taskId2);

        // Получаем историю и десериализуем
        HttpResponse<String> response = sendGet("/history");
        assertStatusCode(response, 200);

        // Десериализуем список задач из истории
        Task[] history = fromJson(response.body(), Task[].class);
        assertEquals(2, history.length);
        assertTrue(history[0].getName().equals("Task 1") || history[0].getName().equals("Task 2"));
        assertTrue(history[1].getName().equals("Task 1") || history[1].getName().equals("Task 2"));
    }
}