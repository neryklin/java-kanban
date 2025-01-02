import domain.Epic;
import domain.Subtask;
import domain.Task;
import domain.TaskStatus;
import manager.FileBackedTaskManager;
import manager.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        //test
        FileBackedTaskManager taskManager = Managers.getDefault();
        LocalDateTime o = LocalDateTime.of(2024, 01, 01, 00, 00);
        int iii = taskManager.calculateIndexOfArray(o);
        int iii2 = taskManager.calculateIndexOfArray(o.plusDays(1));
        int iii3 = taskManager.calculateIndexOfArray(o.plusHours(1));
        int iii4 = taskManager.calculateIndexOfArray(o.plusMonths(1));

        System.out.println("---------- loaded---------------");
        System.out.println("---------- test task start---------------");
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        Task task1 = new Task("second task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(3));
        Task task2 = new Task("second  task", "paint red button", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println(taskManager.getTasksList().toString());
        task2.setName("new fist task");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);
        System.out.println(taskManager.getTasksList().toString());
        System.out.println("---------- remove test task --------------");
        taskManager.prioritizedTasksRemove(task2);
        taskManager.removeTaskFromId(task2.getId());
        System.out.println(taskManager.getTasksList().toString());
        System.out.println("---------- test task end ---------------");

        System.out.println("---------- test epic start---------------");
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(10));
        Subtask subtask = new Subtask("subtask 1", "open the color", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("subtask 2", "get brush", TaskStatus.NEW, epic);
        Subtask subtask3 = new Subtask("subtask 3", "paint the button", TaskStatus.NEW, epic);
        Epic epic2 = new Epic("second  epic", "epic red button", TaskStatus.NEW);
        Epic epic3 = new Epic("third  epic", "third", TaskStatus.NEW);
        Subtask subtask4 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        System.out.println("---------- list epic--------------");

        taskManager.addEpicSubTask(epic3, subtask4);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        System.out.println(taskManager.getEpicsList().toString());
        System.out.println("---------- remove epic ---------------");
        taskManager.removeTaskFromId(epic3.getId());
        System.out.println(taskManager.getEpicsList().toString());
        System.out.println("---------- test epic end---------------");


        //Дополнительное задание. Реализуем пользовательский сценарий
        System.out.println("---------- test history---------------");
        List<Task> historyList = taskManager.getHistoryManager().getHistory();
        System.out.println(historyList);


        Epic epic5 = new Epic(" epic 5", "epic green button", TaskStatus.NEW);
        Subtask subtask5 = new Subtask("subtask 5", "open the color", TaskStatus.NEW, epic5);
        Subtask subtask6 = new Subtask("subtask 6", "get brush", TaskStatus.NEW, epic5);
        Subtask subtask7 = new Subtask("subtask 7", "paint the button", TaskStatus.NEW, epic5);
        Epic epic6 = new Epic("epic 6", "epic red button", TaskStatus.NEW, Duration.of(13, ChronoUnit.HOURS), LocalDateTime.now().minusDays(5));
        taskManager.addEpic(epic5);
        taskManager.addEpic(epic6);
        taskManager.getTaskFromId(epic5.getId());
        taskManager.getTaskFromId(epic6.getId());
        taskManager.getTaskFromId(subtask5.getId());
        taskManager.getTaskFromId(subtask7.getId());
        taskManager.getTaskFromId(subtask6.getId());
        historyList = taskManager.getHistoryManager().getHistory();
        System.out.println(historyList);
        taskManager.getTaskFromId(epic5.getId());
        taskManager.getTaskFromId(subtask5.getId());
        historyList = taskManager.getHistoryManager().getHistory();
        System.out.println(historyList);
        taskManager.removeTaskFromId(subtask6.getId());
        historyList = taskManager.getHistoryManager().getHistory();
        System.out.println(historyList);
        taskManager.removeTaskFromId(epic5.getId());
        historyList = taskManager.getHistoryManager().getHistory();
        System.out.println(historyList);

        //Дополнительное задание. Реализуем пользовательский сценарий
        // Дополнительное задание. Реализуем пользовательский сценарий
        //Дополнительное задание. Реализуем пользовательский сценарий
        System.out.println("---------- test load---------------");
        FileBackedTaskManager taskManager2 = Managers.getDefault();
        taskManager2.load();
        System.out.println("---------- final---------------");

    }
}
