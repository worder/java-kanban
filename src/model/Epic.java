package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        this(id, name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIds) {
        super(id, name, description, status);
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    public Epic(Epic epic) {
        this(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(), epic.getSubtaskIds());
    }

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
    public String toString() {
        return "Epic{" +
                "id=" + getId() + ", " +
                "name='" + getName() + "', " +
                "description='" + getDescription() + "', " +
                "status=" + getStatus() + ", " +
                "subtaskIds=" + getSubtaskIds() +
                '}';
    }
}
