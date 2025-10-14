package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        super.setUp();
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(tempFile);
    }

    // Специфичные тесты для FileBackedTaskManager
    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        taskManager.save();
        assertTrue(tempFile.exists(), "Файл должен создаться после сохранения");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());

        String header = Files.readString(tempFile.toPath());
        assertTrue(header.contains("id,type,name,description,status,epic"));
    }

    @Test
    void testIdGeneratedAfterLoadingTasks() {
        Task task1 = new Task("Task1", "description", StatusTask.NEW);
        int taskId1 = taskManager.addNewTask(task1);
        Task task2 = new Task("Task2", "description", StatusTask.NEW);
        int taskId2 = taskManager.addNewTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task task3 = new Task("Task3", "description", StatusTask.NEW);
        int taskId3 = loadedManager.addNewTask(task3);

        assertEquals(1, taskId1);
        assertEquals(2, taskId2);
        assertEquals(3, taskId3);
    }
}