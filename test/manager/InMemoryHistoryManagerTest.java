package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.StatusTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager manager;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    public void initHistorymanager() {
        manager = new InMemoryTaskManager();
        task1 = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        task2 = new Task("Task_2", "Task_2 description", StatusTask.NEW);
        task3 = new Task("Task_3", "Task_3 description", StatusTask.NEW);
    }

    @Test
    void testAddHistory() {
        int task1Id = manager.addNewTask(task1);
        assertEquals(0, manager.getHistory().size(), "После добавления задачи, история должна быть пустой.");
        manager.getTasks(task1Id);
        assertNotNull(manager.getHistory(), "После добавления задачи и после просмотра - история не должна быть пустой.");
        assertEquals(1, manager.getHistory().size(), "После добавления задачи, после просмотра - история не должна быть пустой.");
    }

    @Test
    void getHistoryEmptyHistoryReturnEmptyList() { //Пустая история задач
        List<Task> history = manager.getHistory();

        assertNotNull(history, "История не должна быть null");
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }


    @Test
    public void testHistoricVersionsByPointer() {
        int task1Id = manager.addNewTask(task1);
        manager.getTasks(task1Id);
        assertEquals(StatusTask.NEW, manager.getHistory().get(0).getStatus(), "В истории после просмотра версия должна соответствовать");
        task1.setStatus(StatusTask.IN_PROGRESS);
        assertEquals(StatusTask.IN_PROGRESS, manager.getHistory().get(0).getStatus(), "После изменения не обновился статус задачи");
    }

    @Test
    public void testDoubleTask() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task1Id);

        List<Task> tasks = manager.getHistory();
        assertEquals(2, tasks.size(), "После повторного просмотра дубликат должен удалиться");
        assertEquals(task2, tasks.get(0), "Должен сохраниться порядок просмотра");
        assertEquals(task1, tasks.get(1), "Должен сохраниться порядок просмотра");
    }

    @Test
    public void testDeleteTaskInBegin() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);
        assertEquals(3, manager.getHistory().size(), "Размер истории не соответствует кол-ву просмотров");
        manager.deleteTask(task1Id);
        assertEquals(2, manager.getHistory().size(), "После удаления задачи -  в  истории тоже исчезает");
        assertEquals(task2, manager.getHistory().get(0), "Должен сохраниться порядок просмотра после удаления");
        assertEquals(task3, manager.getHistory().get(1), "Должен сохраниться порядок просмотра после удаления");
    }

    @Test
    public void testRemoveTaskInMiddle() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);
        assertEquals(3, manager.getHistory().size(), "Размер истории не соответствует кол-ву просмотров");
        manager.deleteTask(task2Id);
        assertEquals(2, manager.getHistory().size(), "После удаления задачи -  в  истории тоже исчезает");
        assertEquals(task1, manager.getHistory().get(0), "Должен сохраниться порядок просмотра после удаления");
        assertEquals(task3, manager.getHistory().get(1), "Должен сохраниться порядок просмотра после удаления");
    }

    @Test
    public void testRemoveTaskInFinish() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);
        assertEquals(3, manager.getHistory().size(), "Размер истории не соответствует кол-ву просмотров");
        manager.deleteTask(task3Id);
        assertEquals(2, manager.getHistory().size(), "После удаления задачи -  в  истории тоже исчезает");
        assertEquals(task1, manager.getHistory().get(0), "Должен сохраниться порядок просмотра после удаления");
        assertEquals(task2, manager.getHistory().get(1), "Должен сохраниться порядок просмотра после удаления");
    }

    @Test
    void addTask_DuplicateTasks_ShouldMoveToEnd() {  //Дублирование

        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task1Id);

        List<Task> history = manager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 уникальные задачи");
        assertEquals(task2, history.get(0), "Первая задача должна остаться на месте");
        assertEquals(task1, history.get(1), "Дубликат должен переместиться в конец");
    }

    @Test
    void remove_FromBeginning_ShouldUpdateHistoryCorrectly() {//Удаление из истории: начало
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);

        manager.deleteTask(task1Id);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(task2, history.get(0), "Первой должна стать вторая задача");
        assertEquals(task3, history.get(1), "Второй должна остаться третья задача");
    }

    @Test
    void remove_FromMiddle_ShouldUpdateHistoryCorrectly() {  //Удаление из истории: середина

        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);

        manager.deleteTask(task2Id);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(task1, history.get(0), "Первая задача должна остаться на месте");
        assertEquals(task3, history.get(1), "Третья задача должна стать второй");
    }

    @Test
    void remove_FromEnd_ShouldUpdateHistoryCorrectly() {//Удаление из истории: конец

        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);

        manager.deleteTask(task3Id);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(task1, history.get(0), "Первая задача должна остаться на месте");
        assertEquals(task2, history.get(1), "Вторая задача должна остаться на месте");
    }

    @Test
    void remove_NonExistentTask_ShouldNotChangeHistory() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);

        manager.deleteTask(999);
        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История не должна измениться при удалении несуществующей задачи");
    }

    @Test
    void historyOrder_ShouldPreserveInsertionOrder() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);

        List<Task> history = manager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2");
        assertEquals(task3, history.get(2), "Третья задача должна быть task3");
    }

    @Test
    void multipleDuplicates_ShouldKeepOnlyLastOccurrence() {
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);

        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task1Id); // Первый дубликат
        manager.getTasks(task3Id);
        manager.getTasks(task1Id); // Второй дубликат

        List<Task> history = manager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 уникальные задачи");
        assertEquals(task2, history.get(0), "Первая задача должна быть task2");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
        assertEquals(task1, history.get(2), "Последняя задача должна быть task1 (последний дубликат)");
    }
}