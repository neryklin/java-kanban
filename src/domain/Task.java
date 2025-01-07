package domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static int countId = 1;
    private String name;
    private int id;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        calculateEndTime();
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.id = Task.countId;
        Task.countId++;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        calculateEndTime();
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.id = Task.countId;
        Task.countId++;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        calculateEndTime();
    }

    public Task(int id, String name, TaskStatus status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task() {
        this.id = Task.countId;
        Task.countId++;
    }

    public static void setCountId(int countId) {
        Task.countId = countId;
    }

    public Duration getDuration() {
        return (duration == null ? Duration.ZERO : duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        calculateEndTime();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        calculateEndTime();
    }

    public void calculateEndTime() {
        if (startTime!=null) {
            this.endTime = startTime.plus(duration);
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public String prepareToSave() {
        String sep = ",";
        return id + sep +
                this.getClass() + sep +
                name + sep +
                status + sep +
                description + sep +
                (duration == null ? null : duration.toMinutes()) + sep +
                (startTime == null ? null : startTime) + sep +
                (endTime == null ? null : endTime);
    }
}
