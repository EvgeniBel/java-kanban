package ru.javacource.schedule.manager;

import ru.javacource.schedule.tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    public static final int MAX_SIZE = 10;
    private List<Task> history = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void addTask(Task task) {
        if (task==null){
            return;
        }
        Task taskCopy = task.copy();
        history.add(taskCopy);
        if (history.size()>MAX_SIZE){
            history.remove(0);
        }
    }
}
