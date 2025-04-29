package service;

import model.*;
import service.exception.ManagerSaveException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Path path;
    private static final Charset FILE_CHARSET = StandardCharsets.UTF_8;

    public FileBackedTaskManager(HistoryManager history, Path filepath) {
        super(history);
        path = filepath;
    }

    private void save() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage(), e);
            }
        }

        try (BufferedWriter out = Files.newBufferedWriter(path, FILE_CHARSET)) {
            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(getAllTasks());
            allTasks.addAll(getAllEpics());
            allTasks.addAll(getAllSubtasks());

            for (Task task : allTasks) {
                out.write(taskToString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage(), e);
        }
    }

    private void load() {
        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader in = Files.newBufferedReader(path, FILE_CHARSET)) {
            while (in.ready()) {
                String line = in.readLine();
                Task task = stringToTask(line);
                switch (task) {
                    case Epic epic -> putEpic(epic);
                    case Subtask subtask -> putSubtask(subtask);
                    default -> putTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage(), e);
        }
    }

    private Task stringToTask(String string) {
        String[] parts = string.split(",");

        int id = Integer.parseInt(parts[0]);
        String name = parts[2];
        String desc = parts[4];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        TaskType type = TaskType.valueOf(parts[1]);

        return switch (type) {
            case TaskType.EPIC -> new Epic(id, name, desc, status);
            case TaskType.SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new Subtask(id, epicId, name, desc, status);
            }
            default -> new Task(id, name, desc, status);
        };
    }

    private String taskToString(Task t) {
        // id,type,name,status,description,epic
        String template = "%s,%s,%s,%s,%s,%s%n";
        return switch (t) {
            case Epic e -> String.format(template,
                    e.getId(), TaskType.EPIC, e.getName(), e.getStatus(), e.getDescription(), "");
            case Subtask s -> String.format(template,
                    s.getId(), TaskType.SUBTASK, s.getName(), s.getStatus(), s.getDescription(), s.getEpicId());
            default -> String.format(template,
                    t.getId(), TaskType.TASK, t.getName(), t.getStatus(), t.getDescription(), "");
        };
    }

    @Override
    public Integer createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Integer createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public static FileBackedTaskManager loadFromFile(HistoryManager history, Path path) {
        FileBackedTaskManager tm = new FileBackedTaskManager(history, path);
        tm.load();
        return tm;
    }
}
