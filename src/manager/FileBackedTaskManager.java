package manager;

import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static manager.CSVTaskFormat.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try {
            if (!file.exists()) {
                return taskManager;
            }
            String textFromFile = Files.readString(file.toPath());
            if (textFromFile.isEmpty() || textFromFile.equals(header)) {
                return taskManager;
            }
            String[] lines = textFromFile.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = taskFromString(line);
                if (task != null) {
                    if (task instanceof Epic) {
                        taskManager.listEpic.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        taskManager.listSubtask.put(task.getId(), (Subtask) task);
                        Subtask subtask = (Subtask) task;
                        Epic epic = taskManager.listEpic.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    } else {
                        taskManager.listTask.put(task.getId(), task);
                    }
                }
            }
            for (Epic epic : taskManager.listEpic.values()) {
                taskManager.updateEpicStatus(epic.getId());
                taskManager.updateEpicTime(epic.getId());
            }
            taskManager.updateIdFromLoadId();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }

        return taskManager;
    }

    @Override
    public int addNewTask(Task task) {
        int taskId = super.addNewTask(task);
        save();
        return taskId;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int epicId = super.addNewEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Integer subtaskId = super.addNewSubtask(subtask);
        if (subtaskId != null) {
            save();
        }
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();
            StringBuilder fileContents = new StringBuilder(header);
            for (Task task : getTasks()) {
                fileContents.append(taskToString(task));
            }
            for (Epic epic : getEpics()) {
                fileContents.append(taskToString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                fileContents.append(taskToString(subtask));
            }

            Files.writeString(file.toPath(), fileContents.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    @Override
    public String toString() {
        return "FileBackedTaskManager{" +
                "file=" + file.getAbsolutePath() +
                ", task=" + getTasks().size() +
                ", epic=" + getEpics().size() +
                ", subtask=" + getSubtasks().size() + "}";
    }

    public static void main(String[] args) throws IOException {
        File file = new File("resources/task.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        int taskId1 = manager.addNewTask(task1);
        Task task2 = new Task("Task_2", "Task_2 description", StatusTask.NEW);
        int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic_1", "Epic_1 description", StatusTask.NEW);
        int epicId1 = manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic_2", "Epic_2 description", StatusTask.NEW);
        int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1", "Subtask_1 for epic 1", StatusTask.NEW, epicId1);
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_2 for epic 1", StatusTask.IN_PROGRESS, epicId1);
        Subtask subtask3 = new Subtask("Subtask_3", "Subtask_3 for epic 2", StatusTask.NEW, epicId2);
        int subtaskId1 = manager.addNewSubtask(subtask1);
        int subtaskId2 = manager.addNewSubtask(subtask2);
        int subtaskId3 = manager.addNewSubtask(subtask3);

        FileBackedTaskManager loaderManager = loadFromFile(file);
        System.out.println("Загружаем из файла " + loaderManager);
    }
}
