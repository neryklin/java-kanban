public class Main {

    public static void main(String[] args) {

    //test
        TaskManager taskManager = new TaskManager();

        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        Task task2 = new Task("second  task","paint red button",TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        task2.setName("new fist task");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);


        Epic epic = new Epic("fist epic","epic green button",TaskStatus.NEW);
        Subtask subtask = new Subtask("subtask fist epic","open the color",TaskStatus.NEW,epic);
        Subtask subtask2 = new Subtask("subtask second epic","get brush",TaskStatus.NEW,epic);
        Subtask subtask3 = new Subtask("subtask fird epic","paint the button",TaskStatus.NEW,epic);
        Epic epic2 = new Epic("second  epic","epic red button",TaskStatus.NEW);
        Epic epic3 = new Epic("third  epic","third",TaskStatus.NEW);
        Subtask subtask4 = new Subtask("subtask SECOND epic","TEST 2",TaskStatus.NEW);
        taskManager.addEpicSubTask(epic3,subtask4);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        System.out.println(taskManager.getTasksList().toString());
        System.out.println("-----------------------------------");
        System.out.println(taskManager.getEpicsList().toString());
        System.out.println("-----------------------------------");
        System.out.println(taskManager.getAllSubTasksList().toString());

    }
}
