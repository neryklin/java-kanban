import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public void addTask(Task task){
      tasks.put(task.getId(),task);
    }

    public void updateTask(Task task){
        tasks.put(task.getId(),task);
    }

    public void addEpic(Epic epic){
        epics.put(epic.getId(),epic);
    }

    public void addEpicSubTask(Epic epic,Subtask subtask){
        epic.subTaskList.put(subtask.getId(),subtask);
        subtask.setEpic(epic);
    }

    public HashMap<Integer, Task> getTasksList(){
       return tasks;
    }

    public HashMap<Integer, Epic> getEpicsList(){
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubTasksList(){
        HashMap<Integer, Subtask> subtasklist = new HashMap<>();
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.subTaskList.values()) {
                subtasklist.put(subtask.getId(), subtask);
            }
        }
        return subtasklist;
    }

    public HashMap<Integer, Subtask> getSubTasksListFromEpic(Epic epic){
        return epic.subTaskList;
    }

    public void removeAllTask(){
        tasks.clear();
        epics.clear();
    }

    public Task getTaskFromId(int id){
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            HashMap<Integer, Subtask> subtasklist  = getAllSubTasksList();
            if (subtasklist.containsKey(id)) {
                return subtasklist.get(id);
            }
        }
        return null;
    }

}
