import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{

    HashMap<Integer, Subtask>  subTaskList = new HashMap<>();
    public Epic(String name, String description, TaskStatus status, HashMap<Integer, Subtask> subTaskList) {
        super(name, description, status);
        if (subTaskList!=null) {
            this.subTaskList=subTaskList;
        }
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

}
