import java.util.ArrayList;
import java.util.HashMap;

public class ManagerTask {
    static int countId = 0;

    HashMap<Integer, Task> listTask = new HashMap<>();
    HashMap<Integer, Epic> listEpic = new HashMap<>();
    HashMap<Integer, Subtask> listSubtask = new HashMap<>();


    //Получение списка
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(listTask.values());
    }
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(listEpic.values());
    }
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(listSubtask.values());
    }
    public ArrayList<Integer> getEpicSubtasks(int epicId) {
        Epic epic = listEpic.get(epicId);
        if (epic != null) {
            return epic.getSubtaskId();
        }else
            return new ArrayList<>();
    }
    //Получение по индексу
    public Task getTasks(int id) {
        return listTask.get(id);
    }
    public Epic getEpic(int id) {
        return listEpic.get(id);
    }
    public Subtask getSubtasks(int id) {
        return listSubtask.get(id);
    }
    //Добавление
    public int addNewTask(Task task) {
        int id = ++countId;
        task.setId(id);
        listTask.put(id, task);
        return id;
    }
    public int addNewEpic(Epic epic) {
        int id = ++countId;
        epic.setId(id);
        listEpic.put(id, epic);
        return id;
    }
    public Integer addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = listEpic.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = ++countId;
        subtask.setId(id);
        listSubtask.put(id, subtask);
        epic.addSubtaskId(id);
        updateEpicStatus(epicId);
        return id;
    }
    //Замещение
    public void updateTask(Task task) {
        listTask.put(task.getId(), task);
    }
    public void updateEpic(Epic epic) {
        if (epic != null && listEpic.containsKey(epic.getId())){
            StatusTask oldStatus = listEpic.get(epic.getId()).getStatus();
            epic.setStatus(oldStatus);
            listEpic.put(epic.getId(), epic);
        }
    }
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && listSubtask.containsKey(subtask.getId())) {
            listSubtask.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }
    //удаление по индексу
    public void deleteTask(int id) {
        listTask.remove(id);
    }
    public void deleteEpic(int id) {
        Epic epic = listEpic.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                listSubtask.remove(subtaskId);
            }
        }

    }
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

    public void deleteAllTasks() {
        listTask.clear();
    }
    public void deleteAllEpics() {
        listEpic.clear();
        listSubtask.clear();
    }
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
                case NEW:
                    countNew++;
                    break;
                case DONE:
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
}
