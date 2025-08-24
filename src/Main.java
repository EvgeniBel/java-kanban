import manager.InMemoryTaskManager;
import manager.TaskManager;
import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        int taskId1 = manager.addNewTask(task1);
        Task task2 = new Task("Task_2", "Task_2 description", StatusTask.NEW);
        int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic_1", "Epic_2 description", StatusTask.IN_PROGRESS);
        int epicId1 = manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic_2", "Epic_2 description", StatusTask.NEW);
        int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1 for epic 1", StatusTask.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_2 for epic 1", StatusTask.NEW, epicId1);
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_3 for epic 1", StatusTask.NEW, epicId1);
        int subtask1Id = manager.addNewSubtask(subtask1);
        int subtask2Id = manager.addNewSubtask(subtask2);
        int subtask3Id = manager.addNewSubtask(subtask3);

        printAllTasks(manager);

        System.out.println("\nЗапросы");
        manager.getEpic(epicId2);
        manager.getTasks(taskId1);
        manager.getTasks(taskId2);
        manager.getSubtasks(subtask3Id);
        manager.getSubtasks(subtask2Id);
        manager.getEpic(epicId1);
        printHistory(manager);

        System.out.println("\nПовторно смотрим задачу Эпик_2");
        manager.getEpic(epicId2);
        printHistory(manager);

        System.out.println("\nУдаляем задачу Task_2");
        manager.deleteTask(taskId2);
        printHistory(manager);

        System.out.println("\nУдаляем задачу Эпик_1");
        manager.deleteEpic(epicId1);

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
        System.out.println("История запросов");
        for (Task task : manager.getHistory()) {
            System.out.println("Название: " + task.getName() + ", ID: " + task.getId());
        }
    }
}
