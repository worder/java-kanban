package model;

public class Task {
    private final String name;
    private final String description;
    private TaskStatus status;
    private int id;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this(name, description, status);
        setId(id);
    }

    public Task(Task task, String name, String description, TaskStatus status) {
        this(task.getId(), name, description, status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
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
                "status=" + getStatus() +
                '}';
    }
}
