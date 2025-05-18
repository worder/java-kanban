package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.exception.InMemoryTaskManagerCreateException;
import service.exception.InMemoryTaskManagerPutException;
import util.IdGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final IdGenerator idGen = new IdGenerator();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // ---- tasks ----

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        historyManager.remove(tasks.keySet());
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(Task.copyOf(task));
        return task;
    }

    @Override
    public Integer createTask(Task task) {
        if (task == null) {
            throw new InMemoryTaskManagerCreateException("Task is null");
        } else if (isTaskTimeOverlapping(task)) {
            throw new InMemoryTaskManagerCreateException("Task is overlapping");
        }

        int id = idGen.getNewId();
        Task taskWithId = task.withId(id);

        tasks.put(id, taskWithId);
        addToPrioritizedTasks(taskWithId);

        return id; // return id for testing convenience
    }

    protected void putTask(Task task) {
        if (task == null) {
            throw new InMemoryTaskManagerPutException("Task is null");
        } else if (task.getId() == 0) {
            throw new InMemoryTaskManagerPutException("No id assigned to task");
        } else if (tasks.containsKey(task.getId())) {
            throw new InMemoryTaskManagerPutException("Task already exists");
        }

        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);

        idGen.actualizeNextId(task.getId());
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new InMemoryTaskManagerCreateException("Task is null");
        } else if (isTaskTimeOverlapping(task)) {
            throw new InMemoryTaskManagerCreateException("Task is overlapping");
        }

        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void deleteTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
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
            updateEpicTemporal(epic.getId());
        }

        historyManager.remove(subtasks.keySet());
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(Subtask.copyOf(subtask));
            return subtask;
        }

        return null;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new InMemoryTaskManagerCreateException("Subtask is null");
        } else if (isTaskTimeOverlapping(subtask)) {
            throw new InMemoryTaskManagerCreateException("Subtask is overlapping");
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            int id = idGen.getNewId();
            Subtask subtaskWithId = subtask.withId(id);

            subtasks.put(id, subtaskWithId);
            addToPrioritizedTasks(subtaskWithId);

            epic.addSubtaskId(id);
            updateEpicStatus(epicId);
            updateEpicTemporal(epicId);

            return id;
        }

        return null;
    }

    protected void putSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new InMemoryTaskManagerPutException("Subtask is null");
        } else if (subtask.getId() == 0) {
            throw new InMemoryTaskManagerPutException("No id assigned to subtask");
        } else if (!epics.containsKey(subtask.getEpicId())) {
            throw new InMemoryTaskManagerPutException("No epic for subtask found");
        }

        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);

        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        idGen.actualizeNextId(subtask.getId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new InMemoryTaskManagerCreateException("Subtask is null");
        } else if (isTaskTimeOverlapping(subtask)) {
            throw new InMemoryTaskManagerCreateException("Subtask is overlapping");
        }

        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            addToPrioritizedTasks(subtask);

            updateEpicStatus(subtask.getEpicId());
            updateEpicTemporal(subtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicStatus(epic.getId());
            updateEpicTemporal(epic.getId());
        }
    }

    // ---- epics ----

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        prioritizedTasks.removeAll(subtasks.values());
        historyManager.remove(subtasks.keySet());
        historyManager.remove(epics.keySet());
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(Epic.copyOf(epic));
        return epic;
    }

    @Override
    public Integer createEpic(Epic epic) {
        int id = idGen.getNewId();
        epics.put(id, Epic.copyOf(epic).withId(id));
        return id;
    }

    public void putEpic(Epic epic) {
        if (epic == null) {
            throw new InMemoryTaskManagerPutException("Epic is null");
        } else if (epic.getId() == 0) {
            throw new InMemoryTaskManagerPutException("No id assigned to epic");
        } else if (epics.containsKey(epic.getId())) {
            throw new InMemoryTaskManagerPutException("Epic already exists");
        }

        epics.put(epic.getId(), epic);
        idGen.actualizeNextId(epic.getId());
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.put(id, Epic.copyOf(epic));
            updateEpicStatus(id);
            updateEpicTemporal(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            for (int subtaskId : subtaskIds) {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
            }
            historyManager.remove(subtaskIds);
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());
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

        epics.put(epicId, epic.withStatus(currentStatus));
    }

    private void updateEpicTemporal(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        // find min start time
        Optional<LocalDateTime> startTime = epicSubtasks.stream()
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo);

        // find max end time
        Optional<LocalDateTime> endTime = epicSubtasks.stream()
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo);

        // calculate duration sum
        Duration duration = epicSubtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epics.put(epicId, epic.withTemporal(
                startTime.orElse(null),
                endTime.orElse(null),
                duration));
    }

    // ----

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private boolean isTaskTimeOverlapping(Task task) {
        return prioritizedTasks.stream().anyMatch(task::hasTimeConflictWith);
    }
}
