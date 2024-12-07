import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public void updateEpicStatus(Epic epic) {
        boolean newStatus = true;
        boolean doneStatus = true;
        if (!epic.subTaskList.isEmpty()) {
            for (Subtask subtask : epic.subTaskList.values()) {
                if (subtask.getStatus() != TaskStatus.NEW) {
                    newStatus = false;
                }
                if (subtask.getStatus() != TaskStatus.DONE) {
                    doneStatus = false;
                }
            }
            if (doneStatus) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newStatus) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }

    }

    public void addEpicSubTask(Epic epic, Subtask subtask) {
        epic.subTaskList.put(subtask.getId(), subtask);
        subtask.setEpic(epic);
        updateEpicStatus(epic);
    }

    public HashMap<Integer, Task> getTasksList() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpicsList() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubTasksList() {
        HashMap<Integer, Subtask> subtasklist = new HashMap<>();
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.subTaskList.values()) {
                subtasklist.put(subtask.getId(), subtask);
            }
        }
        return subtasklist;
    }

    public HashMap<Integer, Subtask> getSubTasksListFromEpic(Epic epic) {
        return epic.subTaskList;
    }

    public void removeAllTask() {
        tasks.clear();
        epics.clear();
    }

    public Task getTaskFromId(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            HashMap<Integer, Subtask> subtasklist = getAllSubTasksList();
            if (subtasklist.containsKey(id)) {
                return subtasklist.get(id);
            }
        }
        return null;
    }

    public Task removeTaskFromId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            epics.remove(id);
        } else {
            HashMap<Integer, Subtask> subtasklist = getAllSubTasksList();
            if (subtasklist.containsKey(id)) {
                Epic teampEpic = subtasklist.get(id).getEpic();
                subtasklist.remove(id);
                updateEpicStatus(teampEpic);
            }
        }
        return null;
    }

}
