package manager;

import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    static int countId = 0;
    private HashMap<Integer, Task> listTask = new HashMap<>();
    private HashMap<Integer, Epic> listEpic = new HashMap<>();
    private HashMap<Integer, Subtask> listSubtask = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    //Получение списка
    @Override
    public ArrayList<Task> getTasks() {

        return new ArrayList<>(listTask.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(listEpic.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
                return new ArrayList<>(listSubtask.values());
    }

    @Override
    public ArrayList<Integer> getEpicSubtasks(int epicId) {
        Epic epic = listEpic.get(epicId);
        if (epic != null) {
            return epic.getSubtaskId();
        } else
            return new ArrayList<>();
    }

    //Получение по индексу
    @Override
    public Task getTasks(int id) {
        historyManager.addTask(listTask.get(id));
        return listTask.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.addTask(listEpic.get(id));
        return listEpic.get(id);
    }

    @Override
    public Subtask getSubtasks(int id) {
        historyManager.addTask(listSubtask.get(id));
        return listSubtask.get(id);
    }

    //Добавление
    @Override
    public int addNewTask(Task task) {
        int id = generateId();
        task.setId(id);
        listTask.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        listEpic.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = listEpic.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        listSubtask.put(id, subtask);
        epic.addSubtaskId(id);
        updateEpicStatus(epicId);
        return id;
    }

    //Замещение
    @Override
    public void updateTask(Task task) {
        listTask.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && listEpic.containsKey(epic.getId())) {
            StatusTask oldStatus = listEpic.get(epic.getId()).getStatus();
            epic.setStatus(oldStatus);
            listEpic.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && listSubtask.containsKey(subtask.getId())) {
            listSubtask.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    //удаление по индексу
    @Override
    public void deleteTask(int id) {
        listTask.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = listEpic.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                listSubtask.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = listSubtask.remove(id);
        if (subtask != null) {
            Epic epic = listEpic.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskId().remove(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        listTask.clear();
    }

    @Override
    public void deleteAllEpics() {
        listEpic.clear();
        listSubtask.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : listSubtask.values()) {
            Epic epic = listEpic.get(subtask.getEpicId());
            if (epic != null) {
                int num = subtask.getId();
                epic.getSubtaskId().remove(num);
                updateEpicStatus(epic.getId());
            }
        }
        listSubtask.clear();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = listEpic.get(epicId);
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(StatusTask.NEW);
            return;
        }
        int countDONE = 0;
        int countNew = 0;
        for (Integer subtaskId : epic.getSubtaskId()) {
            StatusTask subtaskStatus = listSubtask.get(subtaskId).getStatus();
            switch (subtaskStatus) {
                case StatusTask.NEW:
                    countNew++;
                    break;
                case StatusTask.DONE:
                    countDONE++;
                    break;
            }
        }
        if (countNew == epic.getSubtaskId().size()) {
            epic.setStatus(StatusTask.NEW);
        } else if (countDONE == epic.getSubtaskId().size()) {
            epic.setStatus(StatusTask.DONE);
        } else epic.setStatus(StatusTask.IN_PROGRESS);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private static int generateId() {
        return ++countId;
    }
}
