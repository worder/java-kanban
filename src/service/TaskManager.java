package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final IdGenerator idGen = new IdGenerator();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    // ---- tasks ----

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(idGen.getNewId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // ---- subtasks ----

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        List<Epic> epics = getAllEpics();
        for (Epic epic : epics) {
            epic.removeAllSubtaskIds();
            updateEpicStatus(epic.getId());
        }

        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            int id = idGen.getNewId();
            subtask.setId(id);
            subtasks.put(id, subtask);

            epic.addSubtaskId(id);
            updateEpicStatus(epicId);
        }
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getEpicId());
            epic.removeSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(epic.getId());
        }
    }

    // ---- epics ----

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        int id = idGen.getNewId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.put(id, epic);
            updateEpicStatus(epic.getId());
        }
    }

    public void deleteEpic(int id) {
        Epic epic = this.getEpicById(id);
        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
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
