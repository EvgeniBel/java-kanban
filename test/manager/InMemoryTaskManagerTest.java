package manager;

import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    // Специфичные тесты для InMemoryTaskManager
    @Test
    void testTasksWithTheSameIdAreEqual() {
        Task task1 = new Task(1, "Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task(1, "Task 2", "Description 2", StatusTask.IN_PROGRESS);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void testTasksWithDifferentIdAreNotEqual() {
        Task task1 = new Task(1, "Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task(2, "Task 1", "Description 1", StatusTask.NEW);

        assertNotEquals(task1, task2, "Задачи с разным ID не должны быть равны");
    }
}