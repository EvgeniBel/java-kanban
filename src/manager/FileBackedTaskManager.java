package manager;

import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static  File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(FileBackedTaskManager.file);
        try {
            String content = Files.readString(FileBackedTaskManager.file.toPath());
            String[] lines = content.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    break;
                }

                Task task = taskFromString(line);
                if (task != null) {
                    if (task instanceof Epic) {
                        getListEpic().put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        getlistSubtask().put(task.getId(), (Subtask) task);
                        Subtask subtask = (Subtask) task;
                        Epic epic = getListEpic().get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }

                    } else {
                       getListTask().put(task.getId(), task);
                    }
                }
            }
            for (Epic epic : getListEpic().values()) {
                taskManager.updateEpicStatus(epic.getId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }
        return taskManager;
    }

    private static Task taskFromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            return null;
        }
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        String description = parts[3];
        StatusTask status = StatusTask.valueOf(parts[4]);

        switch (type) {
            case "Task":
                return new Task(id, name, description, status);
            case "Epic":
                return new Epic(id, name, description, status);
            case "Subtask":
                if (parts.length < 6) {
                    return null;
                }
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                return null;
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d\n",
                    subtask.getId(),
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicId());

        } else if (task instanceof Epic) {
            return String.format("%d,SUBTASK,%s,%s,%s,%d\n",
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getStatus());
        } else{
            return String.format("%d,SUBTASK,%s,%s,%s,%d\n",
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getStatus());
        }
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
        save();
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
        try{
            file.getParentFile().mkdirs();
            String header = "id,type,name,description,status,epic\n";
            StringBuilder content = new StringBuilder(header);
            for(Task task : getTasks()){
                content.append(taskToString(task));
            }
            for (Epic epic : getEpics()){
                content.append(taskToString(epic));
            }
            for (Subtask subtask : getSubtasks()){
                content.append(taskToString(subtask));
            }
            content.append("\n");
            Files.writeString(file.toPath(),content.toString(),
                    StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }

    }

    @Override
    public String toString() {
        return "FileBackedTaskManager{"+
    "file="+ file.getAbsolutePath()+
                ", task="+getTasks().size()+
                ", epic="+getEpics().size()+
                ", subtask="+getSubtasks().size()+"}";
    }


    public static void main (String[] args){
        File file = new File("resources/task.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

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

        FileBackedTaskManager loaderManeger = loadFromFile(file);
    }
}
