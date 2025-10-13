package manager;

import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeOverlapTest {

    @Test
    void testTimeOverlapScenarios() {
        LocalDateTime baseTime = LocalDateTime.of(2025, 10, 1, 21, 0);
        Duration duration = Duration.ofHours(1);

        InMemoryTaskManager manager = new InMemoryTaskManager();

        // Первая задача: 21:00 - 22:00
        Task task1 = new Task("Task 1", "Description", StatusTask.NEW, duration, baseTime);
        manager.addNewTask(task1);

        // Пересекающаяся задача: 21:30 - 22:30 (пересекается с первой)
        Task overlappingTask = new Task("Overlapping", "Description", StatusTask.NEW,
                duration, baseTime.plusMinutes(30));

        // Должно выбросить исключение из-за пересечения времени
        assertThrows(ManagerSaveException.class, () -> manager.addNewTask(overlappingTask),
                "Должно быть выброшено исключение при пересечении временных интервалов");

        // Непересекающаяся задача (после): 22:00 - 23:00
        Task nonOverlappingAfter = new Task("After", "Description", StatusTask.NEW,
                duration, baseTime.plusHours(2));
        assertDoesNotThrow(() -> manager.addNewTask(nonOverlappingAfter),
                "Не должно быть исключения для непересекающихся задач (после)");

        // Непересекающаяся задача (до): 20:00 - 21:00
        Task nonOverlappingBefore = new Task("Before", "Description", StatusTask.NEW,
                duration, baseTime.minusHours(2));
        assertDoesNotThrow(() -> manager.addNewTask(nonOverlappingBefore),
                "Не должно быть исключения для непересекающихся задач (до)");
    }

    @Test
    void testDifferentOverlapScenarios() {
        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(1);

        // Задача начинается раньше, но пересекается
        {
            InMemoryTaskManager manager = new InMemoryTaskManager();
            Task baseTask = new Task("Task", "Description", StatusTask.NEW, duration, baseTime);
            manager.addNewTask(baseTask);

            Task overlapStart = new Task("Overlap Start", "Description", StatusTask.NEW,
                    Duration.ofMinutes(90), baseTime.minusMinutes(30));
            assertThrows(ManagerSaveException.class, () -> manager.addNewTask(overlapStart),
                    "Задача, начинающаяся раньше и заканчивающаяся во время базовой, должна пересекаться");
        }

        // Задача пересекается c существующей и заканчивается позже
        {
            InMemoryTaskManager manager = new InMemoryTaskManager();
            Task baseTask = new Task("Task", "Description", StatusTask.NEW, duration, baseTime);
            manager.addNewTask(baseTask);

            Task overlapEnd = new Task("Overlap End", "Description", StatusTask.NEW,
                    Duration.ofMinutes(90), baseTime.plusMinutes(30));
            assertThrows(ManagerSaveException.class, () -> manager.addNewTask(overlapEnd),
                    "Задача, начинающаяся во время базовой и заканчивающаяся позже, должна пересекаться");
        }

        // Задача полностью внутри существующей задачи
        {
            InMemoryTaskManager manager = new InMemoryTaskManager();
            Task baseTask = new Task("Task", "Description", StatusTask.NEW, duration, baseTime);
            manager.addNewTask(baseTask);

            Task inside = new Task("Task2", "Description", StatusTask.NEW,
                    Duration.ofMinutes(30), baseTime.plusMinutes(15));
            assertThrows(ManagerSaveException.class, () -> manager.addNewTask(inside),
                    "Задача полностью внутри базовой должна пересекаться");
        }

        // Задача полностью пересекается с существующей
        {
            InMemoryTaskManager manager = new InMemoryTaskManager();
            Task baseTask = new Task("Base", "Description", StatusTask.NEW, duration, baseTime);
            manager.addNewTask(baseTask);

            Task contains = new Task("Contains", "Description", StatusTask.NEW,
                    Duration.ofHours(2), baseTime.minusMinutes(30));
            assertThrows(ManagerSaveException.class, () -> manager.addNewTask(contains),
                    "Задача, полностью содержащая базовую, должна пересекаться");
        }
    }

    @Test
    void testTasksWithoutTime_ShouldNotCauseOverlap() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW); // Без времени
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0)); // С временем

        manager.addNewTask(task1);

        assertDoesNotThrow(() -> manager.addNewTask(task2),
                "Не должно быть исключения когда одна задача без времени");
    }

    @Test
    void testBothTasksWithoutTime_ShouldNotCauseOverlap() { // Задачи без времени
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW);

        manager.addNewTask(task1);

        assertDoesNotThrow(() -> manager.addNewTask(task2),
                "Не должно быть исключения, когда обе задачи без времени");
    }

    @Test
    void testTasksWithNonOverlappingTime_ShouldNotThrowException() { // Задачи не пересекаются
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 10, 0));
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 14, 0));

        manager.addNewTask(task1);

        assertDoesNotThrow(() -> manager.addNewTask(task2),
                "Не должно быть исключения при непересекающихся временных интервалах");
    }

    @Test
    void testUpdateTaskWithOverlappingTime_ShouldThrowException() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 12, 0));

        int task1Id = manager.addNewTask(task1);
        manager.addNewTask(task2);

        Task updatedTask1 = new Task(task1Id, "Updated Task 1", "Updated Description", StatusTask.NEW,
                Duration.ofHours(3), LocalDateTime.of(2024, 1, 1, 11, 0)); // Теперь пересекается

        assertThrows(ManagerSaveException.class, () -> manager.updateTask(updatedTask1),
                "Должно быть исключение при обновлении задачи с пересекающимся временем");
    }
}