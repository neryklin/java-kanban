import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @Test
    void getDefaultCheck() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        //TODO
        //assertTrue(inMemoryTaskManager.tasks.isEmpty(),"Инициализировалось не пустое хранилище");
        //assertTrue(inMemoryTaskManager.epics.isEmpty(),"Инициализировалось не пустое хранилище");
    }

    @Test
    void getDefaultHistoryCheck() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertTrue(inMemoryHistoryManager.historyVeiwList.isEmpty(), "Инициализировалось не пустое хранилище");

    }
}