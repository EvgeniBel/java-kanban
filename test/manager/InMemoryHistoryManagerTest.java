package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void initHistorymanager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void testAddHistory() {
        Task task = new Task("Test_1", "Testion task", StatusTask.NEW);
        historyManager.addTask(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    public void testHistoricVersionsByPointer() {
        Task task = new Task("Test_1", "Testion task", StatusTask.NEW);
        historyManager.addTask(task);
        task.setStatus(StatusTask.IN_PROGRESS);
        historyManager.addTask(task);
        assertEquals(StatusTask.NEW, historyManager.getHistory().get(0).getStatus(),"Не сохранена предыдущая версия задачи");
    }
    @Test
    public void testDeletedTaskIfSiseMoreTen(){

        for (int i = 0; i < InMemoryHistoryManager.MAX_SIZE + 1; i++) {
            Task task = new Task("Task " + i, "description", StatusTask.NEW);
            task.setId(i);
            historyManager.addTask(task);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(InMemoryHistoryManager.MAX_SIZE, history.size(),"Размер тасков в истории не соответсвует MAX-возможному значению");
        assertEquals(1, history.get(0).getId(),"При переполненнии не удалился первый таск");

    }
}