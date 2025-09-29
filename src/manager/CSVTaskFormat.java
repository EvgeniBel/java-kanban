package manager;

import tasks.*;

import static tasks.TaskType.*;

public class CSVTaskFormat {
    public static final String header = "id,type,name,description,status,epic\n";

    public static String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d\n",
                    subtask.getId(),
                    SUBTASK,
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicId());

        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s\n",
                    task.getId(),
                    EPIC,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus());
        } else {
            return String.format("%d,%s,%s,%s,%s\n",
                    task.getId(),
                    TASK,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus());
        }
    }

    public static Task taskFromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 5) {
            return null;
        }
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        String description = parts[3];
        StatusTask status = StatusTask.valueOf(parts[4]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                if (parts.length < 6) {
                    return null;
                }
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                return null;
        }
    }
}
