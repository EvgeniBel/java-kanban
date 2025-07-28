package ru.javacource.schedule.manager;

import ru.javacource.schedule.tasks.Task;
import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addTask(Task task);
}
