import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

     @Test
    void getDefaultHistoryCheck() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertTrue(inMemoryHistoryManager.historyVeiwList.isEmpty(), "Инициализировалось не пустое хранилище");

    }
}