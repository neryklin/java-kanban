import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epicTask;

    public Subtask(int id, String name, TaskStatus status, String description, Epic epicTask) {
        super(id, name, status, description);
        this.epicTask = epicTask;
        epicTask.subTaskList.put(this.getId(), this);
    }

    public Subtask(int id, String name, TaskStatus status, String description, Epic epicTask, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, status, description, duration, startTime, endTime);
        this.epicTask = epicTask;
        epicTask.subTaskList.put(this.getId(), this);
    }


    public Subtask(int id, String name, TaskStatus status, String description, Epic epicTask, Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
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
        String stringPrepareToSave = super.prepareToSave();
        return stringPrepareToSave + sep + this.epicTask.getId();
    }

}
