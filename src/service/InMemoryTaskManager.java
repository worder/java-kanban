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

public class InMemoryTaskManager implements TaskManager {

    private final IdGenerator idGen = new IdGenerator();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;

    InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // ---- tasks ----

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task =  tasks.get(id);
        if (task == null) return null;
        historyManager.add(new Task(task));
        return task;
    }

    @Override
    public Integer createTask(Task task) {
        Task newTask = new Task(task); // make a copy of task for isolation

        int id = idGen.getNewId();
        newTask.setId(id);
        tasks.put(id, newTask);

        return id; // return id for testing convenience
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (!tasks.containsKey(id)) return;
        tasks.put(id, new Task(task));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // ---- subtasks ----

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        List<Epic> epics = getAllEpics();
        for (Epic epic : epics) {
            epic.removeAllSubtaskIds();
            updateEpicStatus(epic.getId());
        }

        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) return null;
        historyManager.add(new Subtask(subtask));
        return subtask;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            Subtask newSubtask = new Subtask(subtask);

            int id = idGen.getNewId();
            newSubtask.setId(id);
            subtasks.put(id, newSubtask);

            epic.addSubtaskId(id);
            updateEpicStatus(epicId);
            return id;
        }

        return null;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, new Subtask(subtask));
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(epic.getId());
        }
    }

    // ---- epics ----

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return null;
        historyManager.add(new Epic(epic));
        return epic;
    }

    @Override
    public Integer createEpic(Epic epic) {
        Epic newEpic = new Epic(epic);

        int id = idGen.getNewId();
        newEpic.setId(id);
        epics.put(id, newEpic);

        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.put(id, new Epic(epic));
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Subtask> epicSubtasks = new ArrayList<>();
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
            return epicSubtasks;
        }

        return null;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
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

    // ----

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
