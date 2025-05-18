package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    private final String name;
    private final String description;
    private final TaskStatus status;
    private final Duration duration;
    private final LocalDateTime startTime;
    private final Integer id;

    // constructors and creation methods

    public Task(int id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this(-1, name, description, status, duration, startTime);
    }

    public static Task copyOf(Task task) {
        return new Task(task.id, task.name, task.description, task.status, task.duration, task.startTime);
    }

    public Task withId(int id) {
        return new Task(id, this.name, this.description, this.status, this.duration, this.startTime);
    }

    public Task withStatus(TaskStatus status) {
        return new Task(this.id, this.name, this.description, status, this.duration, this.startTime);
    }

    // ----

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public boolean hasTimeConflictWith(Task otherTask) {
        if (this.equals(otherTask)) {
            return false; // allow update existing task
        }

        LocalDateTime t1s = this.getStartTime();
        LocalDateTime t1e = this.getEndTime();
        LocalDateTime t2s = otherTask.getStartTime();
        LocalDateTime t2e = otherTask.getEndTime();

        return (t1s.isBefore(t2e) && t1e.isAfter(t2s)) || (t1e.isAfter(t2e) && t1s.isBefore(t2e));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() + ", " +
                "name='" + getName() + "', " +
                "description='" + getDescription() + "', " +
                "status=" + getStatus() + "', " +
                "duration='" + getDuration() + "', " +
                "startTime=" + getStartTime() + "', " +
                "endTime=" + getEndTime() +
                '}';
    }
}
