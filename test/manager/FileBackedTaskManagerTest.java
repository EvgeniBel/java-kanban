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
    @Test
    void testSaveAndLoadWithTimeFields() {
        // Тест для проверки сохранения и загрузки временных полей
        Task task = new Task("Test", "Description", StatusTask.NEW);
        manager.addNewTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loadedManager.getTasks(task.getId());

        assertNotNull(loadedTask);
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getEndTime(), loadedTask.getEndTime());
    }
    @Test
    void loadFromNonExistentFile_ShouldCreateEmptyManager() {
        File nonExistentFile = new File("non_existent_file_12345.csv");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(nonExistentFile);
            assertNotNull(manager, "Менеджер должен быть создан");
            assertTrue(manager.getTasks().isEmpty(), "Список задач должен быть пустым");
            assertTrue(manager.getEpics().isEmpty(), "Список эпиков должен быть пустым");
            assertTrue(manager.getSubtasks().isEmpty(), "Список подзадач должен быть пустым");
        }, "Загрузка из несуществующего файла не должна вызывать исключение");
    }

    @Test
    void loadFromCorruptedFile_ShouldHandleGracefully() throws IOException {
        File tempFile = File.createTempFile("corrupted", ".csv");

        // Записываем некорректные данные
        Files.writeString(tempFile.toPath(), "invalid,csv,data\nmore,invalid,data");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);
            assertNotNull(manager, "Менеджер должен быть создан даже с некорректными данными");
        }, "Загрузка из поврежденного файла не должна вызывать исключение");

        tempFile.delete();
    }

    @Test
    void saveToReadOnlyFile_ShouldThrowManagerSaveException() throws IOException {
        File tempFile = File.createTempFile("readonly", ".csv");
        tempFile.setReadOnly();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Test", "Description", StatusTask.NEW);
        manager.addNewTask(task);

        assertThrows(ManagerSaveException.class, manager::save,
                "Сохранение в файл только для чтения должно вызывать ManagerSaveException");

        // Восстанавливаем права на запись для очистки
        tempFile.setWritable(true);
        tempFile.delete();
    }

    @Test
    void loadFromFileWithInvalidTaskData_ShouldSkipInvalidLines() throws IOException {
        File tempFile = File.createTempFile("invalid_tasks", ".csv");

        // Смесь валидных и невалидных данных
        String content = "id,type,name,description,status,epic,duration,startTime\n" +
                "1,TASK,Valid Task,Valid Description,NEW,,60,2024-01-01T10:00\n" +
                "invalid,data,here\n" + // Невалидная строка
                "2,TASK,Another Valid Task,Another Description,NEW,,30,2024-01-01T12:00\n" +
                "3,UNKNOWN_TYPE,Unknown Task,Description,NEW,,45,2024-01-01T14:00\n"; // Неизвестный тип

        Files.writeString(tempFile.toPath(), content);

        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);
            // Должны загрузиться только валидные задачи
            assertEquals(2, manager.getTasks().size(), "Должны загрузиться только валидные задачи");
        }, "Загрузка файла с невалидными строками не должна вызывать исключение");

        tempFile.delete();
    }

    @Test
    void saveWithInvalidFilePath_ShouldThrowManagerSaveException() {
        // Попытка сохранить в корневую директорию (может быть недоступна для записи)
        File invalidFile = new File("/invalid_path/test_tasks.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
        Task task = new Task("Test", "Description", StatusTask.NEW);
        manager.addNewTask(task);

        assertThrows(ManagerSaveException.class, manager::save,
                "Сохранение по невалидному пути должно вызывать ManagerSaveException");
    }

    @Test
    void loadFromEmptyFile_ShouldCreateEmptyManager() throws IOException {
        File tempFile = File.createTempFile("empty", ".csv");

        // Оставляем файл пустым
        Files.writeString(tempFile.toPath(), "");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);
            assertNotNull(manager, "Менеджер должен быть создан");
            assertTrue(manager.getTasks().isEmpty());
            assertTrue(manager.getEpics().isEmpty());
            assertTrue(manager.getSubtasks().isEmpty());
        }, "Загрузка из пустого файла не должна вызывать исключение");

        tempFile.delete();
    }

    @Test
    void loadFromFileWithOnlyHeader_ShouldCreateEmptyManager() throws IOException {
        File tempFile = File.createTempFile("header_only", ".csv");

        Files.writeString(tempFile.toPath(), "id,type,name,description,status,epic,duration,startTime\n");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);
            assertNotNull(manager, "Менеджер должен быть создан");
            assertTrue(manager.getTasks().isEmpty());
            assertTrue(manager.getEpics().isEmpty());
            assertTrue(manager.getSubtasks().isEmpty());
        }, "Загрузка из файла только с заголовком не должна вызывать исключение");

        tempFile.delete();
    }

    @Test
    void saveAndLoadWithSpecialCharacters_ShouldPreserveData() throws IOException {
        File tempFile = File.createTempFile("special_chars", ".csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task with, comma", "Description with \"quotes\" & special chars", StatusTask.NEW);
        manager.addNewTask(task);
        manager.save();

        assertDoesNotThrow(() -> {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            Task loadedTask = loadedManager.getTasks(task.getId());
            assertNotNull(loadedTask);
            assertEquals("Task with, comma", loadedTask.getName());
            assertEquals("Description with \"quotes\" & special chars", loadedTask.getDescription());
        }, "Специальные символы должны корректно сохраняться и загружаться");

        tempFile.delete();
    }
}