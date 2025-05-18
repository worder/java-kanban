package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds;
    private final LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ZERO, null);
        subtaskIds = new ArrayList<>();
        endTime = null;
    }

    public Epic(
            int id,
            String name,
            String description,
            TaskStatus status,
            List<Integer> subtaskIds,
            Duration duration,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        super(id, name, description, status, duration, startTime);
        this.subtaskIds = new ArrayList<>(subtaskIds);
        this.endTime = endTime;
    }

    public static Epic copyOf(Epic epic) {
        return new Epic(epic.getId(),
                epic.getName(),
                epic.getDescription(),
                epic.getStatus(),
                new ArrayList<>(epic.getSubtaskIds()),
                epic.getDuration(),
                epic.getStartTime(),
                epic.getEndTime());
    }

    public Epic withId(int id) {
        return new Epic(id,
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.getSubtaskIds(),
                this.getDuration(),
                this.getStartTime(),
                this.getEndTime());
    }

    public Epic withStatus(TaskStatus status) {
        return new Epic(this.getId(),
                this.getName(),
                this.getDescription(),
                status,
                this.getSubtaskIds(),
                this.getDuration(),
                this.getStartTime(),
                this.getEndTime());
    }

    public Epic withTemporal(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        return new Epic(this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.getSubtaskIds(),
                duration,
                startTime,
                endTime);
    }

    // ----

    public void addSubtaskId(int id) {
        if (!subtaskIds.contains(id)) {
            subtaskIds.add(id);
        }
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void removeAllSubtaskIds() {
        subtaskIds.clear();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() + ", " +
                "name='" + getName() + "', " +
                "description='" + getDescription() + "', " +
                "status=" + getStatus() + ", " +
                "subtaskIds=" + getSubtaskIds() + "', " +
                "duration='" + getDuration() + "', " +
                "startTime=" + getStartTime() + "', " +
                "endTime=" + getEndTime() +
                '}';
    }
}
