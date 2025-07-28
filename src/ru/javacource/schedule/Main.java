package ru.javacource.schedule;

import ru.javacource.schedule.manager.InMemoryTaskManager;
import ru.javacource.schedule.manager.TaskManager;
import ru.javacource.schedule.tasks.Epic;
import ru.javacource.schedule.tasks.StatusTask;
import ru.javacource.schedule.tasks.Subtask;
import ru.javacource.schedule.tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        int taskId1 = manager.addNewTask(task1);


        Epic epic1 = new Epic("Epic_1", "Epic_1 description", StatusTask.NEW);
        Epic epic2 = new Epic("Epic_2", "Epic_2 description", StatusTask.IN_PROGRESS);
        int epicId1 = manager.addNewEpic(epic1);
        int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1 for epic 1", StatusTask.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_2 for epic 1", StatusTask.NEW, epicId1);
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_1 for epic 2", StatusTask.NEW, epicId2);
        int subtask1Id = manager.addNewSubtask(subtask1);
        int subtask2Id = manager.addNewSubtask(subtask2);
        int subtask3Id = manager.addNewSubtask(subtask3);

        printAllTasks(manager);

        System.out.printf("\nСмотрим созданные выборочно таски, эпики, сабтаски\n %s\n%s\n%s\n",
        manager.getTasks(taskId1),manager.getSubtasks(subtask2Id),manager.getEpic(epicId2));

        //меняем статус и смотрим изменения в истории
        task1.setStatus(StatusTask.IN_PROGRESS);
        subtask2.setStatus(StatusTask.DONE);
        manager.updateSubtask(subtask2);
        System.out.println("\nПоменяли статусы в Task_1 и Subtask_2 и делаем просмотр изменений");
        System.out.println(manager.getTasks(taskId1));
        System.out.println(manager.getSubtasks(subtask2Id));

        printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {

        System.out.println("Созданный список\nЗадачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория последних просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
