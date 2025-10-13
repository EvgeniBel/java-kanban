package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @Test
    void testAddAndGetTask() {
        Task task = new Task("Test", "Description", StatusTask.NEW);
        int taskId = taskManager.addNewTask(task);

        Task savedTask = taskManager.getTasks(taskId);
        assertNotNull(savedTask);
        assertEquals(task, savedTask);
    }

    @Test
    void testAddAndGetEpic() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic);
        assertEquals(epic, savedEpic);
    }

    @Test
    void testAddAndGetSubtask() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, epicId);
        Integer subtaskId = taskManager.addNewSubtask(subtask);
        assertNotNull(subtaskId);

        Subtask savedSubtask = taskManager.getSubtasks(subtaskId);
        assertNotNull(savedSubtask);
        assertEquals(subtask, savedSubtask);
    }

    @Test
    void testEpicStatusCalculation() {
        // a. Все подзадачи со статусом NEW
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", StatusTask.NEW, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Description", StatusTask.NEW,
                Duration.ofMinutes(30), now.plusHours(2));
        Task task2 = new Task("Task 2", "Description", StatusTask.NEW,
                Duration.ofMinutes(45), now.plusHours(1));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Set<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());

        // Проверяем порядок: task2 должен быть первым (раньше по времени)
        Task firstTask = prioritized.iterator().next();
        assertEquals(task2, firstTask);
    }

    @Test
    void testTimeOverlapDetection() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(60);

        Task task1 = new Task("Task 1", "Description", StatusTask.NEW, duration, startTime);
        Task task2 = new Task("Task 2", "Description", StatusTask.NEW, duration, startTime.plusMinutes(30));

        taskManager.addNewTask(task1);

        // Должно выбросить исключение из-за пересечения времени
        assertThrows(ManagerSaveException.class, () -> taskManager.addNewTask(task2));
    }

    @Test
    void testTasksWithoutStartTimeNotInPrioritized() {
        Task taskWithTime = new Task("Task with time", "Description", StatusTask.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task taskWithoutTime = new Task("Task without time", "Description", StatusTask.NEW);

        taskManager.addNewTask(taskWithTime);
        taskManager.addNewTask(taskWithoutTime);

        Set<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertTrue(prioritized.contains(taskWithTime));
    }

    @Test
    void testEpicStatusCalculation_AllDone() { //Все подзадачи со статусом DONE
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusTask.DONE, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", StatusTask.DONE, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusCalculation_NewAndDone() {
        // c. Подзадачи со статусами NEW и DONE
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", StatusTask.DONE, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusCalculation_InProgress() {
        // d. Подзадачи со статусом IN_PROGRESS
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", StatusTask.IN_PROGRESS, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", StatusTask.IN_PROGRESS, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }
}