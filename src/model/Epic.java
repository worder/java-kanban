package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, List<Integer> subtaskIds, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.subtaskIds = subtaskIds;
    }

    public Epic(Epic epic, String name, String description) {
        this(epic.getId(), epic.getSubtaskIds(), name, description);
    }

    public Epic(Epic epic) {
        this(epic, epic.getName(), epic.getDescription());
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
