package handlerTest;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicsHandlerTest extends BaseHttpTest {

    @Test
    void testCreateEpic() throws Exception {
        // Используем GSON для сериализации
        Epic epic = new Epic("Test Epic", "Test Epic Description", StatusTask.NEW);
        String epicJson = toJson(epic);

        HttpResponse<String> response = sendPost("/epics", epicJson);
        assertStatusCode(response, 201);

        // Десериализуем ответ для проверки
        Epic createdEpic = fromJson(response.body(), Epic.class);
        assertEquals("Test Epic", createdEpic.getName());
        assertEquals(StatusTask.NEW, createdEpic.getStatus());
    }

    @Test
    void testEpicStatusWhenSubtasksDone() throws Exception {
        // Создаем эпик через GSON
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        String epicJson = toJson(epic);
        HttpResponse<String> epicResponse = sendPost("/epics", epicJson);
        assertStatusCode(epicResponse, 201);
        int epicId = extractIdFromJson(epicResponse.body());

        // Создаем подзадачу через GSON
        Subtask subtask = new Subtask("Subtask", "Description", StatusTask.NEW, epicId);
        String subtaskJson = toJson(subtask);
        HttpResponse<String> subtaskResponse = sendPost("/subtasks", subtaskJson);
        assertStatusCode(subtaskResponse, 201);
        int subtaskId = extractIdFromJson(subtaskResponse.body());

        // Обновляем подзадачу на DONE через GSON
        Subtask updatedSubtask = new Subtask(subtaskId, "Subtask", "Description", StatusTask.DONE, epicId);
        String updatedSubtaskJson = toJson(updatedSubtask);
        HttpResponse<String> updateResponse = sendPost("/subtasks", updatedSubtaskJson);
        assertStatusCode(updateResponse, 200);

        // Проверяем статус эпика через десериализацию
        HttpResponse<String> response = sendGet("/epics/" + epicId);
        assertStatusCode(response, 200);

        Epic updatedEpic = fromJson(response.body(), Epic.class);
        assertEquals(StatusTask.DONE, updatedEpic.getStatus());
    }
}