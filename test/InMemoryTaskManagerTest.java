import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void checkPrioritizedTasksAdd() {
        FileBackedTaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("second task", "paint green button", TaskStatus.NEW, Duration.of(15, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 10, 1, 0, 0));
        taskManager.addTask(task1);
        TreeSet<Task> taskSet = (TreeSet) taskManager.getPrioritizedTasks();
        assertEquals(taskSet.size(), 1, "Ошиюка добавления реестра сортированных задач по приоритетам одна задача, в списке больше одной");
        assertEquals(taskSet.first().getId(), task1.getId(), "Ошиюка добавления реестра сортированных задач по приоритетам одна задача, но не правильная");
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 3, 1, 1, 45));
        taskManager.addTask(task);
        TreeSet<Task> taskSet2 = (TreeSet) taskManager.getPrioritizedTasks();
        assertEquals(taskSet2.size(), 2, "Ошиюка добавления реестра сортированных задач по приоритетам две задачс, в списке не ДВЕ");
        assertEquals(taskSet2.first().getId(), task.getId(), "Ошиюка добавления реестра сортированных задач по приоритетам две задачи, но не правильная");
    }

    @Test
    void checkStatusEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(10));
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask 1", "open the color", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("subtask 2", "get brush", TaskStatus.NEW, epic);
        Subtask subtask3 = new Subtask("subtask 3", "paint the button", TaskStatus.NEW, epic);

        taskManager.addEpicSubTask(epic, subtask);
        taskManager.addEpicSubTask(epic, subtask2);
        taskManager.addEpicSubTask(epic, subtask3);
        assertEquals(epic.getStatus(), TaskStatus.NEW, "Ошибка расчет статуса эпик NEW");
        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateEpicStatus(epic);
        assertEquals(epic.getStatus(), TaskStatus.DONE, "Ошибка расчет статуса эпик DONE");
        subtask.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateEpicStatus(epic);
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Ошибка расчет статуса эпик DONE");
    }

    @Test
    void removeTastIntervalMap() {
        FileBackedTaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("second task", "paint green button", TaskStatus.NEW, Duration.of(15, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 1, 1, 1, 45));
        taskManager.addTask(task1);
        taskManager.addTask(task);
        taskManager.removeTaskFromId(task1.getId());
        assertEquals(taskManager.intervalMapBusy[0], 0, "Не корректно удален таск не зачищена интервал МАП интервал Мап");
        assertEquals(taskManager.intervalMapBusy[7], task.getId(), "Не корректно удален таск не зачищена интервал МАП интервал Мап");
        assertEquals(taskManager.intervalMapBusy[8], task.getId(), "Не корректно удален таск не зачищена интервал МАП интервал Мап");
    }

    @Test
    void checkStatusCrosNoCros() {
        FileBackedTaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("second task", "paint green button", TaskStatus.NEW, Duration.of(15, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 1, 1, 1, 45));
        taskManager.addTask(task1);
        taskManager.addTask(task);
        assertEquals(taskManager.intervalMapBusy[0], task1.getId(), "Не корректно встало первое значение в начало периода интервал Мап");
        assertEquals(taskManager.intervalMapBusy[7], task.getId(), "Не корректно встало второе значение в 1-45 длиной 30 минут значение в интервал Мап");
        assertEquals(taskManager.intervalMapBusy[8], task.getId(), "Не корректно встало второе значение в 1-45 длиной 30 минут значение в интервал Мап");
    }

    @Test
    void checkStatusCrosYesCros() {
        FileBackedTaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("second task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.MINUTES),
                LocalDateTime.of(2024, 1, 1, 0, 15));
        taskManager.addTask(task1);
        taskManager.addTask(task);
        assertEquals(taskManager.intervalMapBusy[0], task1.getId(), "Не корректно встало первое значение в начало периода интервал Мап");
        assertEquals(taskManager.intervalMapBusy[1], task1.getId(), "Не корректно встало первое значение в начало периода интервал Мап");
        assertEquals(taskManager.intervalMapBusy[3], 0, "Не корректно заполнено пересечение, задача не должна была встать в план значение в интервал Мап");
        assertEquals(taskManager.intervalMapBusy[4], 0, "Не корректно заполнено пересечение, задача не должна была встать в план значение в интервал Мап");
    }

    @Test
    void addTask() {
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        Task task2 = new Task("second  task", "paint red button", TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        assertEquals(taskManager.getTasksList().size(), 2, "Не все задачи добавлены в TaskList");
    }

    @Test
    void updateTask() {
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);

        HashMap<Integer, Task> tasksList = taskManager.getTasksList();
        Task taskferst = tasksList.get(task.getId());

        int id = task.getId();
        Task task2 = new Task("fist task2", "paint green button2", TaskStatus.IN_PROGRESS);
        task2.setId(id);
        taskManager.addTask(task2);
        HashMap<Integer, Task> tasksList2 = taskManager.getTasksList();
        Task taskSecond = tasksList2.get(id);


        assertEquals(taskferst.getId(), taskSecond.getId(), "task при апдейте ломается");
        assertNotEquals(taskferst.getName(), taskSecond.getName(), "task при апдейте ломается");
        assertNotEquals(taskferst.getDescription(), taskSecond.getDescription(), "task при апдейте ломается");
        assertNotEquals(taskferst.getStatus(), taskSecond.getStatus(), "task при апдейте ломается");
    }

    @Test
    void newTaskAddAndCheck() {
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        Task taskFind = taskManager.getTaskFromId(task.getId());
        assertEquals(task.getId(), taskFind.getId(), "task при сохранении ломается");
        assertEquals(task.getName(), taskFind.getName(), "task при сохранении ломается");
        assertEquals(task.getDescription(), taskFind.getDescription(), "task при сохранении ломается");
        assertEquals(task.getStatus(), taskFind.getStatus(), "task при сохранении ломается");

    }


    @Test
    void removeAllTask() {
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        Task task2 = new Task("second  task", "paint red button", TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.removeAllTask();
        assertEquals(taskManager.getTasksList().size(), 0, "список задач TaskList не обнуляется");
    }

    @Test
    void getTaskFromId() {
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        Task taskFind = taskManager.getTaskFromId(task.getId());
        assertEquals(taskFind, task, "Не коррекртно найден таск по id");
    }

    @Test
    void removeTaskFromId() {
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        Task task2 = new Task("second  task", "paint red button", TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        int idTask = task.getId();
        taskManager.removeTaskFromId(idTask);
        assertNull(taskManager.getTaskFromId(idTask), "Не удаляется таск по Id");
    }
}