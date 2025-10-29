package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static tasks.TaskType.*;

public class CSVTaskFormat {
    public static final String header = "id,type,name,description,status,epic,duration,startTime\n";

    public static String taskToString(Task task) {
        String durationStr = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTimeStr = task.getStartTime() != null ? String.valueOf(task.getStartTime()) : "";
        if (task instanceof Subtask subtask) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s\n",
                    subtask.getId(),
                    SUBTASK,
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicId(),
                    durationStr,
                    startTimeStr);
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,,%s,%s\n",
                    task.getId(),
                    EPIC,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus(),
                    durationStr,
                    startTimeStr);
        } else {
            return String.format("%d,%s,%s,%s,%s,,%s,%s\n",
                    task.getId(),
                    TASK,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus(),
                    durationStr,
                    startTimeStr);
        }
    }

    public static Task taskFromString(String value) {
        String[] parts = value.split(",", -1);
        if (parts.length < 5) {
            return null;
        }
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        String description = parts[3];
        StatusTask status = StatusTask.valueOf(parts[4]);

        Duration duration = null;
        LocalDateTime startTime = null;

        if (parts.length > 6 && !parts[6].isEmpty()) {
            try {
                duration = Duration.ofMinutes(Long.parseLong(parts[6]));
            } catch (NumberFormatException e) {
                throw new ManagerSaveException("Неверный формат duration: " + parts[6], e);
            }
        }
        if (parts.length > 7 && !parts[7].isEmpty()) {
            try {
                startTime = LocalDateTime.parse(parts[7]);
            } catch (Exception e) {
                System.err.println("Ошибка парсинга startTime: " + parts[7]);
            }
        }

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                return new Epic(id, name, description, status, duration, startTime);
            case SUBTASK:
                if (parts.length < 6 || parts[5].isEmpty()) {
                    return null;
                }
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, epicId, duration, startTime);
            default:
                return null;
        }
    }
}
