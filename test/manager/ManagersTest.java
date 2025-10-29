package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {
    //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    public void testGetDefaultManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер задач не проинициализирован");
        assertTrue(manager instanceof InMemoryTaskManager, "Менеджер истории не проинициализирован");
    }

    @Test
    public void testManagersGetDefaultHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager, "Менеджер истории не проинициализирован");
        assertTrue(manager instanceof InMemoryHistoryManager);
    }
}