package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(
            int epicId,
            String name,
            String description,
            TaskStatus status,
            Duration duration,
            LocalDateTime startTime
    ) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(
            int id,
            int epicId,
            String name,
            String description,
            TaskStatus status,
            Duration duration,
            LocalDateTime startTime
    ) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public static Subtask copyOf(Subtask subtask) {
        return new Subtask(
                subtask.getId(),
                subtask.getEpicId(),
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getDuration(),
                subtask.getStartTime());
    }

    public Subtask withId(int id) {
        return new Subtask(
                id,
                this.getEpicId(),
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.getDuration(),
                this.getStartTime()
        );
    }

    public Subtask withStatus(TaskStatus status) {
        return new Subtask(
                this.getId(),
                this.getEpicId(),
                this.getName(),
                this.getDescription(),
                status,
                this.getDuration(),
                this.getStartTime()
        );
    }

    // ----

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() + ", " +
                "epicId=" + getEpicId() + ", " +
                "name='" + getName() + "', " +
                "description='" + getDescription() + "', " +
                "status=" + getStatus() + "', " +
                "duration='" + getDuration() + "', " +
                "startTime=" + getStartTime() + "', " +
                "endTime=" + getEndTime() +
                '}';
    }
}
