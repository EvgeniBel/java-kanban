package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void testCreationTask() {
        Task task = new Task("Task_1", "Task_1 description", StatusTask.NEW);
        assertEquals("Task_1", task.getName());
        assertEquals("Task_1 description", task.getDescription());
        assertEquals(StatusTask.NEW, task.getStatus());
    }

    @Test
    public void testSetterTask() {
        Task task = new Task(25, "Task_1", "Task_1 description", StatusTask.NEW);
        task.setId(15);
        task.setName("Task_ONE");
        task.setDescription("Task_ONE description");
        task.setStatus(StatusTask.IN_PROGRESS);
        assertEquals(15, task.getId(), "ID не изменился");
        assertEquals("Task_ONE", task.getName(), "имя таска не изменилось");
        assertEquals("Task_ONE description", task.getDescription());
        assertEquals(StatusTask.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void testTasksWithTheSameIdAreEqual() {
        Task task1 = new Task(1, "Task_1", "Task_1 description", StatusTask.NEW);
        Task task2 = new Task(1, "Task_5", "another description", StatusTask.IN_PROGRESS);
        assertEquals(task1, task2, "Экземпляры с одинаковым ID должны быть равны");
    }

}