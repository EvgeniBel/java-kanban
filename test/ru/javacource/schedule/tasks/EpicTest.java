package ru.javacource.schedule.tasks;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTest {

    @Test
    public void testCreatingEpic() {
        Epic epic = new Epic("Epic_1", "Epic_1 description", StatusTask.NEW);
        assertEquals("Epic_1", epic.getName());
        assertEquals("Epic_1 description", epic.getDescription());
        assertEquals(StatusTask.NEW, epic.getStatus());
    }

    @Test
    public void testSetterTask(){
        Task epic = new Task(25,"Epic_1", "Epic_1 description", StatusTask.NEW);
        epic.setId(15);
        epic.setName("epic_ONE");
        epic.setDescription("epic_ONE description");
        epic.setStatus(StatusTask.IN_PROGRESS);
        assertEquals(15,epic.getId());
        assertEquals("epic_ONE",epic.getName());
        assertEquals("epic_ONE description",epic.getDescription());
        assertEquals(StatusTask.IN_PROGRESS,epic.getStatus());
    }

    @Test
    void testAddSubtaskId() {
        Epic epic = new Epic(1, "Epic", "Epic_1 description", StatusTask.NEW);
        epic.addSubtaskId(1);
        epic.addSubtaskId(5);
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(5);
        assertEquals(expected, epic.getSubtaskId());
    }
    @Test
    public void testEpicsWithTheSameIdAreEqual(){
        Epic epic1 = new Epic(1,"Task_1", "Epic_1 description", StatusTask.NEW);
        Epic epic2 = new Epic(1,"Task_5", "another description", StatusTask.IN_PROGRESS);
        assertEquals(epic1, epic2, "Экземпляры с одинаковым ID должны быть равны");
    }

    @Test
    public void testEpicCantAddInEpicAsSubtask(){
        Epic epic1 = new Epic(1,"Task_1", "Epic_1 description", StatusTask.NEW);
        Subtask subtask = new Subtask(1,"Subtask","Subtask description",StatusTask.NEW,1);
        assertNotEquals(epic1,subtask);
    }
}