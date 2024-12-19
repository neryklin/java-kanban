import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void isEmpty() {
    }

    @Test
    void size() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = taskManager.historyManager;
        assertEquals(historyManager.size(),0, "Очередь инстории инициализируется не нулем");
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        taskManager.addTask(task);
        assertEquals(historyManager.size(),0, "Очередь инстории изменяется при добавлении задачи, не корректно");
        taskManager.getTaskFromId(task.getId());
        assertEquals(historyManager.size(),1, "Очередь инстории не изменяется при просмотре задачи, не корректно");
    }

    @Test
    void remove() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = taskManager.historyManager;
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.getTaskFromId(task.getId());
        assertEquals(historyManager.size(),1, "Очередь инстории не изменяется при просмотре задачи, не корректно");
        taskManager.removeTaskFromId(task.getId());
        assertEquals(historyManager.size(),0, "Очередь истории не удаляет из себя удаленную задачу, не корректно");
    }

    @Test
    void removeAll() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = taskManager.historyManager;
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("fist task","paint green button",TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.getTaskFromId(task.getId());
        taskManager.getTaskFromId(task2.getId());
        assertEquals(historyManager.size(),2, "Очередь инстории не изменяется при просмотре задачи, не корректно");
        taskManager.removeAllTask();
        assertEquals(historyManager.size(),0, "Очередь истории не удаляет из себя удаленную задачу, не корректно");
    }

    @Test
    void add() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = taskManager.historyManager;
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.getTaskFromId(task.getId());
        assertEquals(historyManager.size(),1, "Очередь инстории не изменяется при просмотре задачи, не корректно");

    }

    @Test
    void getHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = taskManager.historyManager;

        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("second task","paint green button",TaskStatus.NEW);
        Task task3 = new Task("third task","paint green button",TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.getTaskFromId(task.getId());
        taskManager.getTaskFromId(task2.getId());
        taskManager.getTaskFromId(task3.getId());
        List<Task> listHistory =  taskManager.historyManager.getHistory();
        assertEquals(listHistory.size(),3, "Первая проверка получения истории 3 положили 3 не получили, не корректно");
        taskManager.getTaskFromId(task2.getId());
        assertEquals(listHistory.size(),3, "Вторая проверка получения истории 3 положили 3 не получили, не корректно");
        assertEquals(historyManager.getHistory().get(2).getId(),task2.getId(),"Третья проверка получения истории дваждый полченный элемент в начала и к онце попадает в список в конце один раз, не корректно");
    }
}