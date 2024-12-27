import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private static int countId = 1;
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
        calcEndTime();
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.id = Task.countId;
        Task.countId++;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        calcEndTime();
    }

    public Duration getDuration() {
        return (duration == null ? Duration.ZERO : duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        calcEndTime();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        calcEndTime();
    }


    public void calcEndTime() {
        this.endTime = startTime.plus(duration);
    }

    public LocalDateTime getEndTime() {
        return endTime;
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
    }

    public Task(int id, String name, TaskStatus status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public static void setCountId(int countId) {
        Task.countId = countId;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
