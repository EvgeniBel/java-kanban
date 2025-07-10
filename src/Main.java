public class Main {

    public static void main(String[] args) {
        ManagerTask manager = new ManagerTask();
        Task task1 = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        Task task2 = new Task("Task_2", "Task_1 description", StatusTask.IN_PROGRESS);
        int taskId1 = manager.addNewTask(task1);
        int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic_1", "Epic_1 description", StatusTask.NEW);
        Epic epic2 = new Epic("Epic_2", "Epic_2 description", StatusTask.IN_PROGRESS);
        int epicId1 = manager.addNewEpic(epic1);
        int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1 for epic 1", StatusTask.NEW,epicId1);
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_2 for epic 1", StatusTask.NEW,epicId1);
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_1 for epic 2", StatusTask.NEW,epicId2);
        int subtask1Id = manager.addNewSubtask(subtask1);
        int subtask2Id = manager.addNewSubtask(subtask2);
        int subtask3Id = manager.addNewSubtask(subtask3);

        printAllTask(manager);

        System.out.println("\nИзменяем статусы...\n");
        // Меняем статусы задач
        task1.setStatus(StatusTask.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.printf("%s изменен на  - %s.\n", task1.getName(),task1.getStatus().name());
        subtask2.setStatus(StatusTask.DONE);
        manager.updateSubtask(subtask2);
        System.out.printf("%s изменен на  - %s.\n", subtask2.getName(),subtask2.getStatus().name());

        // Меняем статусы подзадач
        subtask1.setStatus(StatusTask.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        System.out.printf("%s изменен на  - %s.\n", subtask1.getName(),subtask1.getStatus().name());
        subtask2.setStatus(StatusTask.DONE);
        manager.updateSubtask(subtask2);
        System.out.printf("%s изменен на  - %s.\n", subtask2.getName(),subtask2.getStatus().name());
        subtask3.setStatus(StatusTask.DONE);
        manager.updateSubtask(subtask3);
        System.out.printf("%s изменен на  - %s.\n", subtask3.getName(),subtask3.getStatus().name());

        System.out.println("\nПолный вывод\n");
        printAllTask(manager);

        // Удаляем
        System.out.println("\nУдаляем задачу 1 и эпик 1\n");
        manager.deleteTask(taskId1);
        manager.deleteEpic(epicId1);

        printAllTask(manager);
    }

    private static void printAllTask(ManagerTask manager) {
        System.out.println("Задачи");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("Сабтаски");
        for (Subtask subTask : manager.getSubtasks()) {
            System.out.println(subTask);
        }
    }
}

