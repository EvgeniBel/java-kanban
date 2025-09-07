package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File testFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void init() throws IOException {
        testFile = File.createTempFile("testFile", ".csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void deleteTestFile() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        assertTrue(testFile.exists(), "Файл должен создаться после сохранения");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loadedManager.getTasks().isEmpty(), "список Tasks должен быть пуст");
        assertTrue(loadedManager.getEpics().isEmpty(), "список Epics должен быть пуст");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "список Subtasks должен быть пуст");

        String header = Files.readString(testFile.toPath());
        assertTrue(header.contains("id,type,name,description,status,epic"), "Фаил должен содержать заголовок");

    }

    @Test
    void testSaveAndLoadFileWithTasks() {
        Task task = new Task("Task", "description", StatusTask.NEW);
        int taskId = manager.addNewTask(task);
        Epic epic = new Epic("Epic", "description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "description", StatusTask.NEW, epic.getId());
        int subtaskId = manager.addNewSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertFalse(loadedManager.getTasks().isEmpty(), "список Tasks не должен быть пуст");
        assertFalse(loadedManager.getEpics().isEmpty(), "список Epics не должен быть пуст");
        assertFalse(loadedManager.getSubtasks().isEmpty(), "список Subtasks не должен быть пуст");
    }

    @Test
    void testIdGeneratedAfterLoadingTasks() {
        Task task1 = new Task("Task1", "description", StatusTask.NEW);
        int taskId1 = manager.addNewTask(task1);
        Task task2 = new Task("Task2", "description", StatusTask.NEW);
        int taskId2 = manager.addNewTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task task3 = new Task("Task2", "description", StatusTask.NEW);
        int taskId3 = loadedManager.addNewTask(task3);

        assertEquals(1, taskId1, "Первая задача должна иметь Id=1");
        assertEquals(2, taskId2, "Вторая задача должна иметь Id=2");
        assertEquals(3, taskId3, "Третья задача должна иметь Id=3");
    }
}