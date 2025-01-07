package domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {

    HashMap<Integer, Subtask> subTaskList = new HashMap<>();


    public Epic(String name, String description, TaskStatus status, HashMap<Integer, Subtask> subTaskList) {
        super(name, description, status);
        if (subTaskList != null) {
            this.subTaskList = subTaskList;
        }
    }

    public Epic(String name, String description, TaskStatus status, HashMap<Integer, Subtask> subTaskList, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        if (subTaskList != null) {
            this.subTaskList = subTaskList;
        }
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public Epic(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, status, description, duration, startTime, endTime);
    }

    public Epic(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
    }

    public HashMap<Integer, Subtask> getSubTaskList() {
        return subTaskList;
    }

    public void removeSubtask(Epic epic, Integer subtaskId) {
        epic.subTaskList.remove(subtaskId);

    }
}
