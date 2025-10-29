package handlerTest;

import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest extends BaseHttpTest {

    @Test
    void testGetPrioritizedTasksReturnsSorted() throws Exception {
        // Создаем задачи через GSON
        Task task2 = new Task("Task 2", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task1 = new Task("Task 1", "Description", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 11, 0));

        String task2Json = toJson(task2);
        String task1Json = toJson(task1);

        sendPost("/tasks", task2Json); // Раньше
        sendPost("/tasks", task1Json); // Позже

        HttpResponse<String> response = sendGet("/prioritized");
        assertStatusCode(response, 200);

        // Десериализуем приоритетный список
        Task[] prioritizedTasks = fromJson(response.body(), Task[].class);
        assertEquals(2, prioritizedTasks.length);
        assertEquals("Task 2", prioritizedTasks[0].getName()); // Должна быть первой
        assertEquals("Task 1", prioritizedTasks[1].getName()); // Должна быть второй
    }
}