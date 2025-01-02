import manager.InMemoryHistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @Test
    void getDefaultHistoryCheck() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertTrue(inMemoryHistoryManager.getHistory().isEmpty(), "Инициализировалось не пустое хранилище");

    }
}