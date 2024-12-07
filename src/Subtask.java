public class Subtask extends Task{
    private Epic epicTask;

    public Subtask(String name, String description, TaskStatus status,Epic epicTask) {
        super(name, description, status);
        this.epicTask = epicTask;
        epicTask.subTaskList.put(this.getId(),this);
    }

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public void setEpic(Epic epic){
        this.epicTask = epic;
    }
}
