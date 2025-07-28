package ru.javacource.schedule.manager;

import org.junit.jupiter.api.Test;
import ru.javacource.schedule.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
  //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
   @Test
    public void testClassReturnsInitializedReadyToUseTaskMasnager(){
       TaskManager manager = Managers.getDefault();
       assertNotNull(manager,"Менеджер задач не проинициализирован");
       assertTrue(manager instanceof InMemoryTaskManager,"Менеджер истории не проинициализирован");
   }
    @Test
    public void testClassReturnsInitializedReadyToUseHistoryManager(){
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager,"Менеджер истории не проинициализирован");
        assertTrue(manager instanceof InMemoryHistoryManager);
    }

}