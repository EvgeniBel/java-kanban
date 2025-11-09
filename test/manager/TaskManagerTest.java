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
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    // Тесты для основных операций с задачами
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
    void testUpdateTask() {
        Task task = new Task("Test", "Description", StatusTask.NEW);
        int taskId = taskManager.addNewTask(task);

        Task updatedTask = new Task(taskId, "Updated Task", "Updated Description", StatusTask.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        assertEquals(updatedTask, taskManager.getTasks(taskId));
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Epic updatedEpic = new Epic(epicId, "Updated Epic", "Updated Description", StatusTask.NEW);
        taskManager.updateEpic(updatedEpic);

        assertEquals(updatedEpic, taskManager.getEpic(epicId));
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        Subtask updatedSubtask = new Subtask(subtaskId, "Updated Subtask", "Updated Description",
                StatusTask.IN_PROGRESS, epicId);
        taskManager.updateSubtask(updatedSubtask);

        assertEquals(updatedSubtask, taskManager.getSubtasks(subtaskId));
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Test", "Description", StatusTask.NEW);
        int taskId = taskManager.addNewTask(task);

        taskManager.deleteTask(taskId);
        assertNull(taskManager.getTasks(taskId));
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        taskManager.deleteEpic(epicId);
        assertNull(taskManager.getEpic(epicId));
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        taskManager.deleteSubtask(subtaskId);
        assertNull(taskManager.getSubtasks(subtaskId));
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        List<Task> tasks = taskManager.getTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1", StatusTask.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", StatusTask.NEW);

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        List<Epic> epics = taskManager.getEpics();
        assertEquals(2, epics.size());
    }

    @Test
    void testGetAllSubtasks() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(2, subtasks.size());
    }

    @Test
    void testGetEpicSubtasks() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, epicId);
        taskManager.addNewSubtask(subtask);

        List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epicId);
        assertEquals(1, epicSubtasks.size());
        assertEquals(subtask, epicSubtasks.get(0));
    }

    @Test
    void testDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", StatusTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusTask.NEW);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void testDeleteAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1", StatusTask.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", StatusTask.NEW);

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    // Тесты для статуса Epic
    @Test
    void testEpicStatusAllSubtasksNew() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusAllSubtasksDone() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.DONE, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.DONE, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusSubtasksNewAndDone() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.DONE, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusAllSubtasksInProgress() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.IN_PROGRESS, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.IN_PROGRESS, epicId);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(StatusTask.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    void testEpicStatusNoSubtasks() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        assertEquals(StatusTask.NEW, taskManager.getEpic(epicId).getStatus());
    }

    // Тесты для приоритетных задач
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

        Task firstTask = prioritized.iterator().next();
        assertEquals(task2, firstTask);
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

    // Тесты для пересечения времени
    @Test
    void testTimeOverlapDetection() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(60);

        Task task1 = new Task("Task 1", "Description", StatusTask.NEW, duration, startTime);
        Task task2 = new Task("Task 2", "Description", StatusTask.NEW, duration, startTime.plusMinutes(30));

        taskManager.addNewTask(task1);
        assertThrows(ManagerSaveException.class, () -> taskManager.addNewTask(task2));
    }

    // Тесты для граничных случаев
    @Test
    void testGetTasksWithInvalidId() {
        assertNull(taskManager.getTasks(999));
    }

    @Test
    void testGetEpicWithInvalidId() {
        assertNull(taskManager.getEpic(999));
    }

    @Test
    void testGetSubtaskWithInvalidId() {
        assertNull(taskManager.getSubtasks(999));
    }

    @Test
    void testDeleteNonExistentTask() {
        assertDoesNotThrow(() -> taskManager.deleteTask(999));
    }

    @Test
    void testDeleteNonExistentEpic() {
        assertDoesNotThrow(() -> taskManager.deleteEpic(999));
    }

    @Test
    void testDeleteNonExistentSubtask() {
        assertDoesNotThrow(() -> taskManager.deleteSubtask(999));
    }

    @Test
    void testUpdateNonExistentTask() {
        Task nonExistentTask = new Task(999, "Non-existent", "Description", StatusTask.NEW);
        assertDoesNotThrow(() -> taskManager.updateTask(nonExistentTask));
    }

    @Test
    void testUpdateNonExistentEpic() {
        Epic nonExistentEpic = new Epic(999, "Non-existent", "Description", StatusTask.NEW);
        assertDoesNotThrow(() -> taskManager.updateEpic(nonExistentEpic));
    }

    @Test
    void testUpdateNonExistentSubtask() {
        Subtask nonExistentSubtask = new Subtask(999, "Non-existent", "Description", StatusTask.NEW, 1);
        assertDoesNotThrow(() -> taskManager.updateSubtask(nonExistentSubtask));
    }

    // Тесты для связи подзадач и эпиков
    @Test
    void testSubtaskHasValidEpicId() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, epicId);
        int subtaskId = taskManager.addNewSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtasks(subtaskId);
        assertEquals(epicId, savedSubtask.getEpicId());
    }

    @Test
    void testEpicContainsSubtasksIds() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId);

        int subtaskId1 = taskManager.addNewSubtask(subtask1);
        int subtaskId2 = taskManager.addNewSubtask(subtask2);

        Epic savedEpic = taskManager.getEpic(epicId);
        assertEquals(2, savedEpic.getSubtaskId().size());
        assertTrue(savedEpic.getSubtaskId().contains(subtaskId1));
        assertTrue(savedEpic.getSubtaskId().contains(subtaskId2));
    }

    @Test
    void testAddingSubtaskToNonExistentEpic() {
        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, 999);
        Integer subtaskId = taskManager.addNewSubtask(subtask);
        assertNull(subtaskId);
    }

    @Test
    void testGetEpicSubtasksReturnsOnlySpecificEpicSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Description 1", StatusTask.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", StatusTask.NEW);

        int epicId1 = taskManager.addNewEpic(epic1);
        int epicId2 = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId2);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(1, taskManager.getEpicSubtasks(epicId1).size());
        assertEquals(1, taskManager.getEpicSubtasks(epicId2).size());
    }
}