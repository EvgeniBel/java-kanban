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
        String messageErrorGenId = "Генератор ID не работает";

        assertTrue(epicId > 0, messageErrorGenId);
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        assertTrue(subtaskId > 0, messageErrorGenId);
        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Сабтасков.");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают.");
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

        String massageIdMatch = "ID должны совпадать";
        assertEquals(taskId, manager.getTasks(taskId).getId(), massageIdMatch);
        assertEquals(epicId, manager.getEpic(epicId).getId(), massageIdMatch);
        assertEquals(subtaskId, manager.getSubtasks(subtaskId).getId(), massageIdMatch);

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
        assertNotEquals(0, manager.getSubtasks().size(), "Сабтаски не добавлены в историю");

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
        assertNotEquals(0, manager.getSubtasks().size(), "История не должна быть пустой");

        manager.deleteAllEpics();

        assertEquals(0, manager.getSubtasks().size(), "Сабтаски не удалены");
        assertTrue(manager.getSubtasks().isEmpty(), "Сабтаски не удалены");
        assertEquals(0, manager.getEpics().size(), "Эпики не удалены");
        assertTrue(manager.getEpics().isEmpty(), "Эпики не удалены");
    }

    @Test
    void testGetTasksWithInvalidIdReturnNull() {
        assertNull(manager.getTasks(999), "Несуществующий ID должен возвращать null");
    }

    @Test
    void testGetEpicWithInvalidIdReturnNull() {
        assertNull(manager.getEpic(999), "Несуществующий ID эпика должен возвращать null");
    }


    @Test
    void testGetSubtasksWithInvalidIdReturnNull() {
        assertNull(manager.getSubtasks(999), "Несуществующий ID эпика должен возвращать null");
    }

    @Test
    void testDeleteNonExistentTaskNotRaiseException() {
        assertDoesNotThrow(() -> manager.deleteTask(999),
                "Удаление несуществующей задачи не должно вызывать исключение");
    }

    @Test
    void testDeleteNonExistentEpicNotRaiseException() {
        assertDoesNotThrow(() -> manager.deleteEpic(999),
                "Удаление несуществующего эпика не должно вызывать исключение");
    }

    @Test
    void testDeleteNonExistentSubtaskNotRaiseException() {
        assertDoesNotThrow(() -> manager.deleteSubtask(999),
                "Удаление несуществующего Subtask не должно вызывать исключение");
    }

    @Test
    void testUpdateNonExistentTaskNotRaiseException() {
        Task nonExistentTask = new Task(999, "Non-existent", "Description", StatusTask.NEW);
        assertDoesNotThrow(() -> manager.updateTask(nonExistentTask),
                "Обновление несуществующей задачи не должно вызывать исключение");

    }

    @Test
    void testUpdateNonExistentEpicNotRaiseException() {
        Epic nonExistentTask = new Epic(999, "Non-existent", "Description", StatusTask.NEW);
        assertDoesNotThrow(() -> manager.updateEpic(nonExistentTask),
                "Обновление несуществующего Epic не должно вызывать исключение");

    }

    @Test
    void testUpdateNonExistentSubtaskNotRaiseException() {
        Subtask nonExistentTask = new Subtask(999, "Non-existent", "Description", StatusTask.NEW, 1);
        assertDoesNotThrow(() -> manager.updateSubtask(nonExistentTask),
                "Обновление несуществующего Subtask не должно вызывать исключение");

    }

    @Test
    void epicStatus_AllSubtasksNewReturnNewForEpic() { //Все подзадачи со статусом NEW
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", StatusTask.NEW, epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        assertEquals(StatusTask.NEW, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть NEW, когда все подзадачи NEW");
    }

    @Test
    void epicStatus_AllSubtasksDoneReturnDoneForEpic() { //Все подзадачи со статусом DONE
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.DONE, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.DONE, epicId);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", StatusTask.DONE, epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        assertEquals(StatusTask.DONE, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть DONE, когда все подзадачи DONE");
    }

    @Test
    void epicStatus_SubtasksNewAndDoneReturnInProgressForEpic() { //Подзадачи со статусами NEW и DONE
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.DONE, epicId);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", StatusTask.NEW, epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, когда есть подзадачи NEW и DONE");
    }

    @Test
    void epicStatus_AllSubtasksInProgressReturnInProgressForEpic() { //Подзадачи со статусом IN_PROGRESS
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.IN_PROGRESS, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.IN_PROGRESS, epicId);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", StatusTask.IN_PROGRESS, epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, когда все подзадачи IN_PROGRESS");
    }

    @Test
    void epicStatus_MixedStatusSubtaskReturnInProgressForEpic() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.IN_PROGRESS, epicId);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", StatusTask.DONE, epicId);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть IN_PROGRESS при смешанных статусах включая IN_PROGRESS");
    }

    @Test
    void epicStatus_SingleSubtaskInProgressReturnInProgressForEpic() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", StatusTask.IN_PROGRESS, epicId);
        manager.addNewSubtask(subtask);

        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен быть IN_PROGRESS при одной подзадаче IN_PROGRESS");
    }

    @Test
    void epicStatus_NoSubtasks_ReturnNewForEpic() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        assertEquals(StatusTask.NEW, manager.getEpic(epicId).getStatus(),
                "Статус эпика без подзадач должен быть NEW");
    }

    @Test
    void epicStatus_UpdateSubtaskToInProgressUpdateEpicStatus() {
        Epic epic = new Epic("Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", StatusTask.NEW, epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        // Изначально статус NEW
        assertEquals(StatusTask.NEW, manager.getEpic(epicId).getStatus());

        // Обновляем подзадачу на IN_PROGRESS
        Subtask updatedSubtask = new Subtask(subtaskId, "Updated Subtask", "Updated Description",
                StatusTask.IN_PROGRESS, epicId);
        manager.updateSubtask(updatedSubtask);

        assertEquals(StatusTask.IN_PROGRESS, manager.getEpic(epicId).getStatus(),
                "Статус эпика должен пересчитаться на IN_PROGRESS после обновления подзадачи");
    }

    @Test
    void subtaskShouldHaveValidEpicId() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, epicId);
        int subtaskId = manager.addNewSubtask(subtask);

        Subtask savedSubtask = manager.getSubtasks(subtaskId);
        assertNotNull(savedSubtask, "Подзадача должна существовать");
        assertEquals(epicId, savedSubtask.getEpicId(), "Подзадача должна быть связана с правильным эпиком");
    }

    @Test
    void epicShouldContainSubtasksIds() {
        Epic epic = new Epic("Test Epic", "Description", StatusTask.NEW);
        int epicId = manager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId);

        int subtaskId1 = manager.addNewSubtask(subtask1);
        int subtaskId2 = manager.addNewSubtask(subtask2);

        Epic savedEpic = manager.getEpic(epicId);
        assertEquals(2, savedEpic.getSubtaskId().size(), "Эпик должен содержать 2 подзадачи");
        assertTrue(savedEpic.getSubtaskId().contains(subtaskId1), "Эпик должен содержать ID первой подзадачи");
        assertTrue(savedEpic.getSubtaskId().contains(subtaskId2), "Эпик должен содержать ID второй подзадачи");
    }

    @Test
    void addingSubtaskToNonExistentEpic_ShouldReturnNull() {
        Subtask subtask = new Subtask("Test Subtask", "Description", StatusTask.NEW, 999); // Несуществующий эпик

        Integer subtaskId = manager.addNewSubtask(subtask);

        assertNull(subtaskId, "Добавление подзадачи к несуществующему эпику должно возвращать null");
    }

    @Test
    void getEpicSubtasks_ShouldReturnOnlySubtasksForSpecificEpic() {
        Epic epic1 = new Epic("Epic 1", "Description 1", StatusTask.NEW);
        Epic epic2 = new Epic("Epic 2", "Description 2", StatusTask.NEW);

        int epicId1 = manager.addNewEpic(epic1);
        int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", StatusTask.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", StatusTask.NEW, epicId2);

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        // Проверяем, что каждый эпик содержит только свои подзадачи
        assertEquals(1, manager.getEpicSubtasks(epicId1).size(), "Эпик 1 должен содержать только свои подзадачи");
        assertEquals(1, manager.getEpicSubtasks(epicId2).size(), "Эпик 2 должен содержать только свои подзадачи");

        assertEquals(subtask1.getEpicId(), epicId1, "Подзадача 1 должна принадлежать эпику 1");
        assertEquals(subtask2.getEpicId(), epicId2, "Подзадача 2 должна принадлежать эпику 2");
    }
}