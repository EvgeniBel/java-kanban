package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task(1, "Task 1", "Description 1", StatusTask.NEW);
        task2 = new Task(2, "Task 2", "Description 2", StatusTask.IN_PROGRESS);
        task3 = new Task(3, "Task 3", "Description 3", StatusTask.DONE);
    }

    @Test
    void testAddTaskToHistory() {
        historyManager.addTask(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testRemoveTaskFromHistory() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void testDuplicateTasksMoveToEnd() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task1); // Дубликат

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void testHistoryPreservesOrder() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void testRemoveFromEmptyHistory() {
        historyManager.remove(1); // Не должно падать
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void testAddNullTask() {
        historyManager.addTask(null);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "Null задачи не должны добавляться в историю");
    }

    @Test
    void testMultipleDuplicatePatterns() {

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task1);
        historyManager.addTask(task3);
        historyManager.addTask(task2);
        historyManager.addTask(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task1, history.get(2));
    }

    @Test
    void testRemoveFromBeginningOfHistory() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void testRemoveFromMiddleOfHistory() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void testRemoveFromEndOfHistory() {
        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }
}