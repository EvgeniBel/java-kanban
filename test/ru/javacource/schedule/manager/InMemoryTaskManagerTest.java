package ru.javacource.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javacource.schedule.tasks.Epic;
import ru.javacource.schedule.tasks.StatusTask;
import ru.javacource.schedule.tasks.Subtask;
import ru.javacource.schedule.tasks.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager manager;

    @BeforeEach
    public void initManager() {
        manager = Managers.getDefault();
    }

    @Test
    public void testAddNewTask() {
        Task task = new Task("task_1", "description", StatusTask.NEW);
        final int taskId = manager.addNewTask(task);
        Task savedTask = manager.getTasks(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("task_1", "description", StatusTask.NEW);
        int taskId = manager.addNewTask(task);
        Task task2 = new Task(taskId, "task_2", "description", StatusTask.IN_PROGRESS);
        manager.updateTask(task2);
        assertEquals(task2, manager.getTasks(taskId), "Task не обновился");
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("task_1", "description", StatusTask.NEW);
        int taskId = manager.addNewTask(task);
        manager.deleteTask(taskId);
        assertNull(manager.getTasks(taskId), "Task not delete");
    }


    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void testImmutabilityOfTheTaskWhenAddedToTheManager() {
        Task taskOriginal = new Task("Test_1", "Dascription", StatusTask.NEW);
        String name = taskOriginal.getName();
        String despetion = taskOriginal.getDescription();
        int id = taskOriginal.getId();
        StatusTask status = taskOriginal.getStatus();

        int taskId = manager.addNewTask(taskOriginal);
        Task taskAfterAdd = manager.getTasks(taskId);
        assertEquals(name, taskAfterAdd.getName(),"Поле name изменилось");
        assertEquals(despetion, taskAfterAdd.getDescription(),"Поле despetion изменилось");
        assertEquals(status, taskAfterAdd.getStatus(),"Поле  status изменилось");
    }

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    public void testTasksInitIdAandGeneratedIDontConflict() {
        Task taskGenId = new Task("Test_gen", "Dascription", StatusTask.NEW);
        int genId = manager.addNewTask(taskGenId);
        Task taskInitId = new Task(manager.getTasks(genId));
        taskInitId.setStatus(StatusTask.IN_PROGRESS);
        manager.updateTask(taskInitId);
        int initId =  manager.addNewTask(taskInitId);

        assertNotNull(manager.getTasks(),"Менеджер пуст");
        assertNotNull(manager.getTasks(genId),"Задача с генерированным ID не существует");
        assertNotNull(manager.getTasks(initId),"Задача с инициализированным ID не существует");
        assertNotEquals(genId, initId, "ID конфликтуют");
        assertEquals(2,manager.getTasks().size(),"Некорректно добавлены задачи");
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
}