package manager;

import tasks.Epic;
import tasks.StatusTask;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int countId = 0;
    protected final HashMap<Integer, Task> listTask = new HashMap<>();
    protected final HashMap<Integer, Epic> listEpic = new HashMap<>();
    protected final HashMap<Integer, Subtask> listSubtask = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> priorityTaskList = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(LocalDateTime::compareTo))
            .thenComparing(Task::getId));

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(priorityTaskList);
    }

    //проверка пересечения задач по времени
    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task1.getEndTime().isAfter(task2.getStartTime());
    }

    //Проверка пересечения задач по времени с сущ.задачами
    private boolean hasTimeOverlapWithExistTask(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return priorityTaskList.stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(existTask -> hasTimeOverlap(newTask, existTask));
    }

    protected void updateEpicTime(int epicId) {
        Epic epic = listEpic.get(epicId);
        if (epic == null || epic.getSubtaskId().isEmpty()) {
            if (epic != null) {
                epic.setStartTime(null);
                epic.setDuration(null);
            }
            return;
        }
        List<Subtask> subtask = getEpicSubtasks(epicId);
        //Самое раннее время начала
        LocalDateTime earlyTimeStart = subtask.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        //Суммарная продолжительнось
        Duration totalDuration = subtask.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(earlyTimeStart);
        epic.setDuration(totalDuration);
    }

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
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = listEpic.get(epicId);
        if (epic != null) {
            return epic.getSubtaskId().stream()
                    .map(listSubtask::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            return new ArrayList<>();
        }
    }

    //Получение по индексу
    @Override
    public Task getTasks(int id) {
        Task task = listTask.get(id);
        if (task != null) {
            historyManager.addTask(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = listEpic.get(id);
        if (epic != null) {
            historyManager.addTask(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtasks(int id) {
        Subtask subtask = listSubtask.get(id);
        if (subtask != null) {
            historyManager.addTask(subtask);
        }
        return subtask;
    }

    //Добавление
    @Override
    public int addNewTask(Task task) {
        if (hasTimeOverlapWithExistTask(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей задачей");
        }
        int id = generateId();
        task.setId(id);
        listTask.put(id, task);
        if (task.getStartTime() != null) {
            priorityTaskList.add(task);
        }
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
        if (hasTimeOverlapWithExistTask(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей");
        }

        int epicId = subtask.getEpicId();
        Epic epic = listEpic.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        listSubtask.put(id, subtask);
        epic.addSubtaskId(id);

        if (subtask.getStartTime() != null) {
            priorityTaskList.add(subtask);
        }
        updateEpicStatus(epicId);
        updateEpicTime(epicId);
        return id;
    }

    //Замещение
    @Override
    public void updateTask(Task task) {
        if (hasTimeOverlapWithExistTask(task)) {
            throw new ManagerSaveException("Задача по времени пересекается с существующей");
        }
        Task oldTask = listTask.get(task.getId());
        if (oldTask != null && oldTask.getStartTime() != null) {
            priorityTaskList.removeIf(t -> t.getId() == task.getId());
        }
        listTask.put(task.getId(), task);

        if (task.getStartTime() != null) {
            priorityTaskList.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && listEpic.containsKey(epic.getId())) {
            Epic existingEpic = listEpic.get(epic.getId());
            epic.setStatus(existingEpic.getStatus());
            epic.setSubtaskIds(existingEpic.getSubtaskId());
            listEpic.put(epic.getId(), epic);
            updateEpicTime(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (hasTimeOverlapWithExistTask(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей");
        }

        if (listSubtask.containsKey(subtask.getId())) {
            Subtask oldSubtask = listSubtask.get(subtask.getId());
            if (oldSubtask.getStartTime() != null) {
                priorityTaskList.remove(oldSubtask);
            }

            listSubtask.put(subtask.getId(), subtask);

            if (subtask.getStartTime() != null) {
                priorityTaskList.add(subtask);
            }
            updateEpicStatus(subtask.getEpicId());
            updateEpicTime(subtask.getEpicId());
        }
    }

    //удаление по индексу
    @Override
    public void deleteTask(int id) {
        Task task = listTask.remove(id);
        if (task != null && task.getStartTime() != null) {
            priorityTaskList.remove(task);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = listEpic.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            if (epic.getStartTime() != null) {
                priorityTaskList.remove(epic);
            }
            for (Integer subtaskId : epic.getSubtaskId()) {
                Subtask subtask = listSubtask.remove(subtaskId);
                if (subtask != null) {
                    if (subtask.getStartTime() != null) {
                        priorityTaskList.remove(subtask);
                    }
                    historyManager.remove(subtaskId);
                }
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = listSubtask.remove(id);
        historyManager.remove(id);
        if (subtask != null) {
            if (subtask.getStartTime() != null) {
                priorityTaskList.remove(subtask);
            }
            Epic epic = listEpic.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : listTask.values()) {
            if (task.getStartTime() != null) {
                priorityTaskList.remove(task);
            }
            historyManager.remove(task.getId());
        }
        listTask.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : listEpic.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : listSubtask.values()) {
            if (subtask.getStartTime() != null) {
                priorityTaskList.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }

        listEpic.clear();
        listSubtask.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : listSubtask.values()) {
            if (subtask.getStartTime() != null) {
                priorityTaskList.remove(subtask);
            }
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : listEpic.values()) {
            epic.getSubtaskId().clear();
        }
        listSubtask.clear();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = listEpic.get(epicId);
        if (epic == null) {
            return;
        }
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(StatusTask.NEW);
            return;
        }
        List<Subtask> subtasks = getEpicSubtasks(epicId);
        boolean allNew = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == StatusTask.NEW);
        boolean allDone = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == StatusTask.DONE);

        if (allNew) {
            epic.setStatus(StatusTask.NEW);
        } else if (allDone) {
            epic.setStatus(StatusTask.DONE);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        updateIdFromLoadId();
        return ++countId;
    }

    protected void updateIdFromLoadId() {
        int maxId = 0;
        for (Integer value : listTask.keySet()) {
            if (maxId < value)
                maxId = value;
        }
        for (Integer value : listEpic.keySet()) {
            if (maxId < value)
                maxId = value;
        }
        for (Integer value : listSubtask.keySet()) {
            if (maxId < value)
                maxId = value;
        }
        countId = maxId;
    }
}
