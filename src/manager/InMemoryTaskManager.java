package manager;

import domain.Epic;
import domain.Subtask;
import domain.Task;
import domain.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    HistoryManager historyManager = Managers.getDefaultHistory();
    //год интервалов по 15 минут в массиве
    int[] intervalMapBusy = new int[366 * 24 * 60 / 15];

    Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    };

    Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public ArrayList<Integer> getBusyIntreval(Task task) {
        ArrayList<Integer> busyInterval = new ArrayList<>();
        if (task.getStartTime() == null && task.getEndTime() == null) {
            return busyInterval;
        }
        int startCheck = calculateIndexOfArray(task.getStartTime());
        int endtCheck = calculateIndexOfArray(task.getEndTime());
        for (int i = startCheck; i < endtCheck; i++) {
            if (intervalMapBusy[i] != 0 && intervalMapBusy[i] != task.getId()) {
                busyInterval.add(intervalMapBusy[i]);
            }
        }
        return busyInterval;
    }

    public List<Integer> addTaskToBusyPlan(Task task) {
        List<Integer> crossingTask = getBusyIntreval(task);
        if (crossingTask.size() > 0) {
            crossingTask.stream()
                    .map(o -> {
                        return getTaskFromId(o);
                    })
                    .forEach(System.out::println);
            return crossingTask;
        } else {
            if (task.getStartTime() != null && task.getEndTime() != null) {
                int startCheck = calculateIndexOfArray(task.getStartTime());
                int endtCheck = calculateIndexOfArray(task.getEndTime());
                for (int i = startCheck; i < endtCheck; i++) {
                    intervalMapBusy[i] = task.getId();
                }
            }
        }
        return crossingTask;
    }

    public void removeTaskFromBusyPlan(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            int startCheck = calculateIndexOfArray(task.getStartTime());
            int endtCheck = calculateIndexOfArray(task.getEndTime());
            for (int i = startCheck; i < endtCheck; i++) {
                if (intervalMapBusy[i] == task.getId()) {
                    intervalMapBusy[i] = 0;
                }
            }
        }
    }

    public int calculateIndexOfArray(LocalDateTime localDateTime) {
        return (localDateTime.getDayOfYear() - 1) * 96 + localDateTime.getHour() * 4 + localDateTime.getMinute() / 15;
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void prioritizedTasksRemove(Task task) {
        if (task.getStartTime() != null && prioritizedTasks.contains(task)) {
            prioritizedTasks.remove(task);
        }
    }

    public void prioritizedTasksAdd(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        task.calculateEndTime();
        prioritizedTasksAdd(task);
        addTaskToBusyPlan(task);

    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        task.calculateEndTime();
        prioritizedTasksAdd(task);
        addTaskToBusyPlan(task);

    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateEpicTimeVariable(epic);
        prioritizedTasksAdd(epic);
        addTaskToBusyPlan(epic);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        boolean newStatus = true;
        boolean doneStatus = true;
        if (!epic.getSubTaskList().isEmpty()) {
            for (Subtask subtask : epic.getSubTaskList().values()) {
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
    public void updateEpicTimeVariable(Epic epic) {
        Duration durationSum = Duration.ZERO;
        LocalDateTime minStartTime = LocalDateTime.MAX;
        LocalDateTime maxEndTime = LocalDateTime.MIN;
        if (!epic.getSubTaskList().isEmpty()) {
            for (Subtask subtask : epic.getSubTaskList().values()) {
                prioritizedTasksAdd(subtask);
                addTaskToBusyPlan(subtask);
                durationSum.plus(subtask.getDuration());
                if (subtask.getStartTime() != null && subtask.getStartTime().isBefore(minStartTime)) {
                    minStartTime = subtask.getStartTime();
                }
                if (subtask.getEndTime() != null && subtask.getEndTime().isAfter(maxEndTime)) {
                    maxEndTime = subtask.getEndTime();
                }
            }
        }
    }

    @Override
    public void addEpicSubTask(Epic epic, Subtask subtask) {
        epic.getSubTaskList().put(subtask.getId(), subtask);
        subtask.setEpic(epic);
        updateEpicStatus(epic);
        updateEpicTimeVariable(epic);
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
            for (Subtask subtask : epic.getSubTaskList().values()) {
                subtasklist.put(subtask.getId(), subtask);
            }
        }
        return subtasklist;
    }

    @Override
    public HashMap<Integer, Subtask> getSubTasksListFromEpic(Epic epic) {
        return epic.getSubTaskList();
    }

    @Override
    public void removeAllTask() {
        tasks.clear();
        epics.clear();
        historyManager.removeAll();
        prioritizedTasks.clear();
        Arrays.fill(intervalMapBusy, 0);
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

    public Epic getEpicFromId(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    public Subtask getSubtaskFromId(int id) {
        HashMap<Integer, Subtask> subtasklist = getAllSubTasksList();
        if (subtasklist.containsKey(id)) {
            Subtask subtask = subtasklist.get(id);
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public void removeTaskFromId(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasksRemove(tasks.get(id));
            removeTaskFromBusyPlan(tasks.get(id));
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            HashMap<Integer, Subtask> subtasklist = getSubTasksListFromEpic(epics.get(id));
            for (Subtask value : subtasklist.values()) {
                historyManager.remove(value.getId());
            }
            prioritizedTasksRemove(epics.get(id));
            removeTaskFromBusyPlan(epics.get(id));
            epics.remove(id);
        } else {
            HashMap<Integer, Subtask> subtasklist = getAllSubTasksList();
            if (subtasklist.containsKey(id)) {
                prioritizedTasksRemove(subtasklist.get(id));
                removeTaskFromBusyPlan(subtasklist.get(id));
                Epic teampEpic = subtasklist.get(id).getEpic();
                subtasklist.remove(id);
                updateEpicStatus(teampEpic);
                updateEpicTimeVariable(teampEpic);
                teampEpic.removeSubtask(teampEpic, id);
            }
        }
        historyManager.remove(id);

    }


    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public int[] getIntervalMapBusy() {
        return intervalMapBusy;
    }


}
