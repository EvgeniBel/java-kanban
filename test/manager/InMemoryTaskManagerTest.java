package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager manager;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    public void initManager() {
        manager = Managers.getDefault();
        task1 = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        task2 = new Task("Task_2", "Task_2 description", StatusTask.NEW);
        task3 = new Task("Task_3", "Task_3 description", StatusTask.NEW);
    }

    @Test
    public void testAddNewTask() {
        int taskId = manager.addNewTask(task1);
        Task savedTask = manager.getTasks(taskId);

        assertTrue(taskId > 0, "Генератор ID не работает");
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testAddNewEpiEndNewSubtask() {
        Epic epic = new Epic("epic_1", "description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Sutask_1", "description", StatusTask.NEW, epicId);
        int subtaskId = manager.addNewSubtask(subtask);
        List<Epic> epics = manager.getEpics();
        List<Subtask> subtasks = manager.getSubtasks();

        assertTrue(epicId > 0, "Генератор ID не работает");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        assertTrue(subtaskId > 0, "Генератор ID не работает");
        assertNotNull(subtasks, "Эпики не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество эпиков.");
        assertEquals(subtask, subtasks.get(0), "Эпики не совпадают.");
    }

    @Test
    public void testUpdateTask() {
        int task1Id = manager.addNewTask(task1);
        task2 = new Task(task1Id, "task_2", "description", StatusTask.IN_PROGRESS);
        manager.updateTask(task2);
        assertEquals(task2, manager.getTasks(task1Id), "Task не обновился");
    }

    @Test
    public void testDeleteTask() {
        int task1Id = manager.addNewTask(task1);
        manager.deleteTask(task1Id);
        assertNull(manager.getTasks(task1Id), "Task not delete");
    }


    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void testImmutabilityOfTheTaskWhenAddedToTheManager() {
        Task taskOriginal = new Task("Test_1", "Description", StatusTask.NEW);
        String name = taskOriginal.getName();
        String despetion = taskOriginal.getDescription();
        int id = taskOriginal.getId();
        StatusTask status = taskOriginal.getStatus();

        int taskId = manager.addNewTask(taskOriginal);
        Task taskAfterAdd = manager.getTasks(taskId);
        assertEquals(name, taskAfterAdd.getName(), "Поле name изменилось");
        assertEquals(despetion, taskAfterAdd.getDescription(), "Поле despetion изменилось");
        assertEquals(status, taskAfterAdd.getStatus(), "Поле  status изменилось");
    }

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    public void testTasksInitIdAndGeneratedIDontConflict() {
        Task taskGenId = new Task("Test_gen", "Dascription", StatusTask.NEW);
        int genId = manager.addNewTask(taskGenId);
        Task taskInitId = new Task(manager.getTasks(genId));
        taskInitId.setStatus(StatusTask.IN_PROGRESS);
        manager.updateTask(taskInitId);
        int initId = manager.addNewTask(taskInitId);

        assertNotNull(manager.getTasks(), "Менеджер пуст");
        assertNotNull(manager.getTasks(genId), "Задача с генерированным ID не существует");
        assertNotNull(manager.getTasks(initId), "Задача с инициализированным ID не существует");
        assertNotEquals(genId, initId, "ID конфликтуют");
        assertEquals(2, manager.getTasks().size(), "Некорректно добавлены задачи");
    }

    //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    public void testManagerAddDifferentTypeAndSearchId() {
        Task task = new Task("Test_1", "Dascription", StatusTask.NEW);
        int taskId = manager.addNewTask(task);
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Descreption", StatusTask.NEW, epic.getId());
        Integer subtaskId = manager.addNewSubtask(subtask);

        assertEquals(1, manager.getTasks().size(), "Должен быть 1 task ");
        assertEquals(1, manager.getEpics().size(), "Должен быть 1 epic");
        assertEquals(1, manager.getSubtasks().size(), "Должен быть 1 subtask");

        assertEquals(taskId, manager.getTasks(taskId).getId(), "ID должны совпадать");
        assertEquals(epicId, manager.getEpic(epicId).getId(), "ID должны совпадать");
        assertEquals(subtaskId, manager.getSubtasks(subtaskId).getId(), "ID должны совпадать");

        assertEquals(task, manager.getTasks(taskId), "Менеджер должен возвращать добавленную задачу");
        assertEquals(epic, manager.getEpic(epicId), "Менеджер должен возвращать добавленный эпик");
        assertEquals(subtask, manager.getSubtasks(subtaskId), "Менеджер должен возвращать добавленную подзадачу");
    }

    @Test
    public void testDeleteAllTask() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.deleteAllTasks();
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void testDeleteAllSubtasks() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Descreption1", StatusTask.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Descreption2", StatusTask.NEW, epic.getId());
        Integer subtaskId1 = manager.addNewSubtask(subtask1);
        Integer subtaskId2 = manager.addNewSubtask(subtask2);

        manager.getSubtasks(subtaskId1);
        manager.getSubtasks(subtaskId2);
        assertNotEquals(0,manager.getSubtasks().size(), "Сабтаски не добавлены в историю");

        manager.deleteAllSubtasks();
        assertTrue(manager.getSubtasks().isEmpty(), "Сабтаски не удалены");
        assertTrue(manager.getHistory().isEmpty(), "Сабтаски из истории не удалены");
    }

    @Test
    public void testDeleteAllEpics() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        Epic epic2 = new Epic("Epic2", "Description", StatusTask.IN_PROGRESS);
        int epicId = manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Descreption1", StatusTask.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Descreption2", StatusTask.NEW, epic.getId());
        Integer subtaskId1 = manager.addNewSubtask(subtask1);
        Integer subtaskId2 = manager.addNewSubtask(subtask2);

        manager.getSubtasks(subtaskId1);
        manager.getSubtasks(subtaskId2);
        manager.getEpic(epicId);
        assertNotEquals(0,manager.getSubtasks().size(), "История не должна быть пустой");

        manager.deleteAllEpics();

        assertEquals(0, manager.getSubtasks().size(), "Сабтаски не удалены");
        assertTrue(manager.getSubtasks().isEmpty(), "Сабтаски не удалены");
        assertEquals(0, manager.getEpics().size(), "Эпики не удалены");
        assertTrue(manager.getEpics().isEmpty(), "Эпики не удалены");
    }
}