import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void addTask() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("second  task","paint red button",TaskStatus.NEW);
        InMemoryTaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        assertEquals(taskManager.getTasksList().size(),2,"Не все задачи добавлены в TaskList");
    }

    @Test
    void updateTask() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        InMemoryTaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);

        HashMap<Integer, Task> tasksList= taskManager.getTasksList();
        Task taskferst = tasksList.get(task.getId());

        int id = task.getId();
        Task task2 = new Task("fist task2","paint green button2",TaskStatus.IN_PROGRESS);
        task2.setId(id);
        taskManager.addTask(task2);
        HashMap<Integer, Task> tasksList2= taskManager.getTasksList();
        Task taskSecond = tasksList2.get(id);


        assertEquals(taskferst.getId(),taskSecond.getId(), "task при апдейте ломается");
        assertNotEquals(taskferst.getName(),taskSecond.getName(), "task при апдейте ломается");
        assertNotEquals(taskferst.getDescription(),taskSecond.getDescription(), "task при апдейте ломается");
        assertNotEquals(taskferst.getStatus(),taskSecond.getStatus(), "task при апдейте ломается");
    }

    @Test
    void newTaskAddAndCheck() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        InMemoryTaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        Task taskFind = taskManager.getTaskFromId(task.getId());
        assertEquals(task.getId(),taskFind.getId(), "task при сохранении ломается");
        assertEquals(task.getName(),taskFind.getName(), "task при сохранении ломается");
        assertEquals(task.getDescription(),taskFind.getDescription(), "task при сохранении ломается");
        assertEquals(task.getStatus(),taskFind.getStatus(), "task при сохранении ломается");

    }


    @Test
    void removeAllTask() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("second  task","paint red button",TaskStatus.NEW);
        InMemoryTaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.removeAllTask();
        assertEquals(taskManager.getTasksList().size(),0,"список задач TaskList не обнуляется");
    }

    @Test
    void getTaskFromId() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        InMemoryTaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        Task taskFind = taskManager.getTaskFromId(task.getId());
        assertEquals(taskFind,task,"Не коррекртно найден таск по id");
    }

    @Test
    void removeTaskFromId() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("second  task","paint red button",TaskStatus.NEW);
        InMemoryTaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        int idTask = task.getId();
        taskManager.removeTaskFromId(idTask);
        assertNull(taskManager.getTaskFromId(idTask), "Не удаляется таск по Id");
    }
}