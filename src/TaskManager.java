import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(Task task);

    void addEpic(Epic epic);

    void updateEpicStatus(Epic epic);

    void addEpicSubTask(Epic epic, Subtask subtask);

    HashMap<Integer, Task> getTasksList();

    HashMap<Integer, Epic> getEpicsList();

    HashMap<Integer, Subtask> getAllSubTasksList();

    HashMap<Integer, Subtask> getSubTasksListFromEpic(Epic epic);

    void removeAllTask();

    Task getTaskFromId(int id);


    Task removeTaskFromId(int id);


}
