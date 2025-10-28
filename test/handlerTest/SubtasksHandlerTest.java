package handlerTest;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtasksHandlerTest extends BaseHttpTest {

    @Test
    void testCreateSubtask_WithTime_ShouldReturnCreatedSubtask() throws Exception {
        // Создаем эпик
        Epic epic = new Epic("Parent Epic", "Description", StatusTask.NEW);
        String epicJson = toJson(epic);
        HttpResponse<String> epicResponse = sendPost("/epics", epicJson);
        assertStatusCode(epicResponse, 201);
        int epicId = extractIdFromJson(epicResponse.body());

        // Создаем подзадачу с временем через GSON
        Subtask subtask = new Subtask("Subtask With Time", "Description", StatusTask.NEW, epicId,
                Duration.ofMinutes(45), LocalDateTime.of(2025, 10, 28, 14, 0));
        String subtaskJson = toJson(subtask);

        HttpResponse<String> response = sendPost("/subtasks", subtaskJson);
        assertStatusCode(response, 201);

        // Десериализуем ответ для проверки
        Subtask createdSubtask = fromJson(response.body(), Subtask.class);
        assertEquals("Subtask With Time", createdSubtask.getName());
        assertEquals(epicId, createdSubtask.getEpicId());
    }

    @Test
    void testCreateSubtask_WithTimeOverlap_ShouldReturnNotAcceptable() throws Exception {
        // Создаем эпик
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        String epicJson = toJson(epic);
        HttpResponse<String> epicResponse = sendPost("/epics", epicJson);
        assertStatusCode(epicResponse, 201);
        int epicId = extractIdFromJson(epicResponse.body());

        // Первая подзадача
        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusTask.NEW, epicId,
                Duration.ofHours(1), LocalDateTime.of(2025, 10, 28, 14, 0));
        String firstSubtaskJson = toJson(subtask1);
        HttpResponse<String> firstResponse = sendPost("/subtasks", firstSubtaskJson);
        assertStatusCode(firstResponse, 201);

        // Пересекающаяся подзадача
        Subtask overlappingSubtask = new Subtask("Overlapping Subtask", "Description", StatusTask.NEW, epicId,
                Duration.ofHours(1), LocalDateTime.of(2025, 10, 28, 14, 0));
        String overlappingSubtaskJson = toJson(overlappingSubtask);
        HttpResponse<String> response = sendPost("/subtasks", overlappingSubtaskJson);

        assertStatusCode(response, 406);
        assertTrue(response.body().contains("пересекается"));
    }
}