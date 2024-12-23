import java.util.HashMap;

public class Epic extends Task {

    HashMap<Integer, Subtask> subTaskList = new HashMap<>();

    public Epic(String name, String description, TaskStatus status, HashMap<Integer, Subtask> subTaskList) {
        super(name, description, status);
        if (subTaskList != null) {
            this.subTaskList = subTaskList;
        }
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public String prepareToSave() {
        String sep = ",";
        return this.getId() + sep + this.getClass() + sep + this.getName() + sep + this.getStatus() + sep + this.getDescription();
    }
}
