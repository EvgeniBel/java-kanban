package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void testCreatingEpic() {
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

}