public class Subtask extends Task {

    private final int epicId;

    public Subtask(int epicId, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, int epicId, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask, String name, String description, TaskStatus status) {
        this(subtask.getId(), subtask.getEpicId(), name, description, status);
    }

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
                "status=" + getStatus() +
                '}';
    }
}
