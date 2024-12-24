public class Subtask extends Task {
    private Epic epicTask;

    public Subtask(int id, String name, TaskStatus status, String description, Epic epicTask) {
        super(id, name, status, description);
        this.epicTask = epicTask;
        epicTask.subTaskList.put(this.getId(), this);
    }

    public Subtask(String name, String description, TaskStatus status, Epic epicTask) {
        super(name, description, status);
        this.epicTask = epicTask;
    }

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public void setEpic(Epic epic) {
        this.epicTask = epic;
    }

    public Epic getEpic() {
        return epicTask;
    }

    public void setEpicTask(Epic epicTask) {
        this.epicTask = epicTask;
    }

    public String prepareToSave() {
        String sep = ",";
        return this.getId() + sep + this.getClass() + sep + this.getName() + sep + this.getStatus() + sep + this.getDescription() + sep + this.epicTask.getId();
    }

}
