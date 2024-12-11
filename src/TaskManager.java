import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> idToTask;
    private final Map<Integer, Subtask> idToSubtask;
    private final Map<Integer, Epic> idToEpic;

    TaskManager() {
        idToTask = new HashMap<>();
        idToSubtask = new HashMap<>();
        idToEpic = new HashMap<>();
    }

    // ---- tasks ----

    public List<Task> getAllTasks() {
        return new ArrayList<>(idToTask.values());
    }

    public void deleteAllTasks() {
        idToTask.clear();
    }

    public Task getTaskById(int id) {
        return idToTask.get(id);
    }

    public Task createTask(Task task) {
        task.setId(IdGenerator.getNewId());
        idToTask.put(task.getId(), task);

        return task;
    }

    public Task updateTask(Task task) {
        int id = task.getId();
        if (idToTask.containsKey(id)) {
            idToTask.put(id, task);
        }

        return task;
    }

    public Task deleteTask(int id) {
        Task task = getTaskById(id);
        idToTask.remove(id);

        return task;
    }

    // ---- subtasks ----

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(idToSubtask.values());
    }

    public void deleteAllSubtasks() {
        List<Epic> epics = getAllEpics();
        for (Epic epic : epics) {
            epic.removeAllSubtaskIds();
            updateEpicStatus(epic.getId());
        }

        idToSubtask.clear();
    }

    public Subtask getSubtaskById(int id) {
        return idToSubtask.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            int id = IdGenerator.getNewId();
            subtask.setId(id);
            idToSubtask.put(id, subtask);

            epic.addSubtaskId(id);
            updateEpicStatus(epicId);
        }

        return subtask;
    }

    public Subtask updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (idToSubtask.containsKey(id)) {
            idToSubtask.put(id, subtask);
            updateEpicStatus(subtask.getEpicId());
        }

        return subtask;
    }

    public Subtask deleteSubtask(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getEpicId());
            epic.removeSubtaskId(id);
            idToSubtask.remove(id);
            updateEpicStatus(epic.getId());
        }

        return subtask;
    }

    // ---- epics ----

    public List<Epic> getAllEpics() {
        return new ArrayList<>(idToEpic.values());
    }

    public void deleteAllEpics() {
        idToSubtask.clear();
        idToEpic.clear();
    }

    public Epic getEpicById(int id) {
        return idToEpic.get(id);
    }

    public Epic createEpic(Epic epic) {
        int id = IdGenerator.getNewId();
        epic.setId(id);
        idToEpic.put(id, epic);

        return epic;
    }

    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (idToEpic.containsKey(id)) {
            idToEpic.put(id, epic);
            updateEpicStatus(epic.getId());
            return epic;
        }

        return null;
    }

    public Epic deleteEpic(int id) {
        Epic epic = this.getEpicById(id);
        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                idToSubtask.remove(subtaskId);
            }
            idToEpic.remove(id);
            return epic;
        }

        return null;
    }

    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            List<Subtask> subtasks = new ArrayList<>();
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                subtasks.add(getSubtaskById(subtaskId));
            }
            return subtasks;
        }

        return null;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        TaskStatus currentStatus = TaskStatus.NEW;
        if (!epicSubtasks.isEmpty()) {
            boolean allNew = true;
            boolean allDone = true;

            for (Subtask subtask : epicSubtasks) {
                TaskStatus subtaskStatus = subtask.getStatus();
                if (allNew && subtaskStatus != TaskStatus.NEW) allNew = false;
                if (allDone && subtaskStatus != TaskStatus.DONE) allDone = false;
                if (!allNew && !allDone) break;
            }

            if (allDone) {
                currentStatus = TaskStatus.DONE;
            } else if (!allNew) {
                currentStatus = TaskStatus.IN_PROGRESS;
            }
        }

        epic.setStatus(currentStatus);
    }
}
