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
        assertEquals(2, tasks.size(),"После повторного просмотра дубликат должен удалиться");
        assertEquals(task2, tasks.get(0),"Должен сохраниться порядок просмотра");
        assertEquals(task1, tasks.get(1),"Должен сохраниться порядок просмотра");
    }

    @Test
    public void testDeleteTaskInBegin(){
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);
        assertEquals(3, manager.getHistory().size(),"Размер истории не соответствует кол-ву просмотров");
        manager.deleteTask(task1Id);
        assertEquals(2, manager.getHistory().size(),"После удаления задачи -  в  истории тоже исчезает");
        assertEquals(task2, manager.getHistory().get(0),"Должен сохраниться порядок просмотра после удаления");
        assertEquals(task3, manager.getHistory().get(1),"Должен сохраниться порядок просмотра после удаления");
    }

    @Test
    public void testRemoveTaskInMiddle(){
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);
        assertEquals(3, manager.getHistory().size(),"Размер истории не соответствует кол-ву просмотров");
        manager.deleteTask(task2Id);
        assertEquals(2, manager.getHistory().size(),"После удаления задачи -  в  истории тоже исчезает");
        assertEquals(task1, manager.getHistory().get(0),"Должен сохраниться порядок просмотра после удаления");
        assertEquals(task3, manager.getHistory().get(1),"Должен сохраниться порядок просмотра после удаления");
    }

    @Test
    public void testRemoveTaskInFinish(){
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);
        manager.getTasks(task1Id);
        manager.getTasks(task2Id);
        manager.getTasks(task3Id);
        assertEquals(3, manager.getHistory().size(),"Размер истории не соответствует кол-ву просмотров");
        manager.deleteTask(task3Id);
        assertEquals(2, manager.getHistory().size(),"После удаления задачи -  в  истории тоже исчезает");
        assertEquals(task1, manager.getHistory().get(0),"Должен сохраниться порядок просмотра после удаления");
        assertEquals(task2, manager.getHistory().get(1),"Должен сохраниться порядок просмотра после удаления");
    }

    @Test
    public void testIsEmptyHistory(){
        int task1Id = manager.addNewTask(task1);
        int task2Id = manager.addNewTask(task2);
        int task3Id = manager.addNewTask(task3);

        assertEquals(0,manager.getHistory().size());
        assertTrue(manager.getHistory().isEmpty());
    }
}