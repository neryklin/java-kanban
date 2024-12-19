import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
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

    @Override
    public void addEpicSubTask(Epic epic, Subtask subtask) {
        epic.subTaskList.put(subtask.getId(), subtask);
        subtask.setEpic(epic);
        updateEpicStatus(epic);
    }

    @Override
    public HashMap<Integer, Task> getTasksList() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpicsList() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubTasksList() {
        HashMap<Integer, Subtask> subtasklist = new HashMap<>();
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.subTaskList.values()) {
                subtasklist.put(subtask.getId(), subtask);
            }
        }
        return subtasklist;
    }

    @Override
    public HashMap<Integer, Subtask> getSubTasksListFromEpic(Epic epic) {
        return epic.subTaskList;
    }

    @Override
    public void removeAllTask() {
        tasks.clear();
        epics.clear();
        historyManager.removeAll();
    }

    @Override
    public Task getTaskFromId(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        } else {
            HashMap<Integer, Subtask> subtasklist = getAllSubTasksList();
            if (subtasklist.containsKey(id)) {
                Subtask subtask = subtasklist.get(id);
                historyManager.add(subtask);
                return subtask;
            }
        }
        return null;
    }

    @Override
    public Task removeTaskFromId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            HashMap<Integer, Subtask> subtasklist = getSubTasksListFromEpic(epics.get(id));
            for (Subtask value : subtasklist.values()) {
                historyManager.remove(value.getId());
            }
            epics.remove(id);
        } else {
            HashMap<Integer, Subtask> subtasklist = getAllSubTasksList();
            if (subtasklist.containsKey(id)) {
                Epic teampEpic = subtasklist.get(id).getEpic();
                subtasklist.remove(id);
                updateEpicStatus(teampEpic);
            }
        }
        historyManager.remove(id);
        return null;
    }


}
