import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {




    //test
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        System.out.println("---------- test task start---------------");
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("second  task","paint red button",TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        System.out.println(taskManager.getTasksList().toString());
        task2.setName("new fist task");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);
        System.out.println(taskManager.getTasksList().toString());
        System.out.println("---------- remove test task --------------");
        taskManager.removeTaskFromId(task2.getId());
        System.out.println(taskManager.getTasksList().toString());
        System.out.println("---------- test task end ---------------");

        System.out.println("---------- test epic start---------------");
        Epic epic = new Epic("fist epic","epic green button",TaskStatus.NEW);
        Subtask subtask = new Subtask("subtask 1","open the color",TaskStatus.NEW,epic);
        Subtask subtask2 = new Subtask("subtask 2","get brush",TaskStatus.NEW,epic);
        Subtask subtask3 = new Subtask("subtask 3","paint the button",TaskStatus.NEW,epic);
        Epic epic2 = new Epic("second  epic","epic red button",TaskStatus.NEW);
        Epic epic3 = new Epic("third  epic","third",TaskStatus.NEW);
        Subtask subtask4 = new Subtask("subtask 4","TEST 2",TaskStatus.NEW);
        System.out.println("---------- list epic--------------");

        taskManager.addEpicSubTask(epic3,subtask4);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        System.out.println(taskManager.getEpicsList().toString());
        System.out.println("---------- remove epic ---------------");
        taskManager.removeTaskFromId(epic3.getId());
        System.out.println(taskManager.getEpicsList().toString());
        System.out.println("---------- test epic end---------------");


        ArrayList<Task> historyList= taskManager.historyManager.getHistory();
        System.out.println(historyList);
        taskManager.getTaskFromId(0);
        taskManager.getTaskFromId(3);
        historyList= taskManager.historyManager.getHistory();
        System.out.println(historyList);



    }
}
