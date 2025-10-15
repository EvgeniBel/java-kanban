package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {

    @Test
    public void testCreatingSubtask() {
        Subtask subtask = new Subtask("Subtask_1", "Subtask_1 description", StatusTask.NEW, 3);
        assertEquals("Subtask_1", subtask.getName());
        assertEquals("Subtask_1 description", subtask.getDescription());
        assertEquals(StatusTask.NEW, subtask.getStatus());
        assertEquals(3, subtask.epicId);
    }

    @Test
    public void testSetterSubtask() {
        Subtask subtask = new Subtask(4, "Subtask_1", "Subtask_1 description", StatusTask.NEW, 3);
        subtask.setId(15);
        subtask.setName("Subtask_ONE");
        subtask.setDescription("Task_ONE description");
        subtask.setStatus(StatusTask.IN_PROGRESS);
        assertEquals(15, subtask.getId());
        assertEquals("Subtask_ONE", subtask.getName());
        assertEquals("Task_ONE description", subtask.getDescription());
        assertEquals(StatusTask.IN_PROGRESS, subtask.getStatus());
    }

    @Test
    void testSubtasksWithDifferentIdAreNotEqual() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description 1", StatusTask.NEW, 1);
        Subtask subtask2 = new Subtask(2, "Subtask 1", "Description 1", StatusTask.NEW, 1);
        assertNotEquals(subtask1, subtask2, "Экземпляры с разными ID не должны быть равны. Неправильная логика метода equals(). Необходимо сравнение по ID");
    }

    @Test
    public void testSubtaskWithTheSameIdAreEqual() {
        Subtask subtask1 = new Subtask(1, "Task_1", "Epic_1 description", StatusTask.NEW, 1);
        Subtask subtask2 = new Subtask(1, "Task_5", "another description", StatusTask.IN_PROGRESS, 1);
        assertEquals(subtask1, subtask2, "Экземпляры с одинаковым ID должны быть равны. Неправильная логика метода equals(). Необходимо сравнение по ID");
    }
}