package service;

import model.*;
import service.exception.InMemoryTaskManagerLoadException;
import service.exception.ManagerLoadException;
import service.exception.ManagerSaveException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Path path;
    private static final Charset FILE_CHARSET = StandardCharsets.UTF_8;
    public static final String LINE_SEPARATOR = "\n";

    public FileBackedTaskManager(HistoryManager history, Path filepath) {
        super(history);
        path = filepath;
    }

    private void save() {
        if (path == null) {
            throw new ManagerSaveException("Path is null");
        } else if (!Files.exists(path)) {
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
        if (path == null) {
            throw new ManagerLoadException("Path is null");
        } else if (!Files.exists(path)) {
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
            throw new ManagerLoadException(e.getMessage(), e);
        }
    }

    private Task stringToTask(String string) {
        String[] parts = string.split(",");

        if (parts.length != 9) {
            throw new InMemoryTaskManagerLoadException(
                    "Line parsing error, expected 9 columns, provided: " + parts.length);
        }

        Function<String, LocalDateTime> parseDate = dateString -> {
            try {
                return LocalDateTime.parse(dateString);
            } catch (DateTimeParseException e) {
                return null;
            }
        };

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String desc = parts[4];
        int epicId = Integer.parseInt(parts[5]);
        LocalDateTime startTime = parseDate.apply(parts[6]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(parts[7]));
        LocalDateTime endTime = parseDate.apply(parts[8]);

        return switch (type) {
            case TaskType.EPIC -> new Epic(id, name, desc, status, new ArrayList<>(), duration, startTime, endTime);
            case TaskType.SUBTASK -> new Subtask(id, epicId, name, desc, status, duration, startTime);
            default -> new Task(id, name, desc, status, duration, startTime);
        };
    }

    private String taskToString(Task t) {
        // id,type,name,status,description,epic,startTime,duration,endTime
        String commonTemplate = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s" + LINE_SEPARATOR,
                t.getId(),
                "%s", // task type
                t.getName(),
                t.getStatus(),
                t.getDescription(),
                "%s", // epicId
                t.getStartTime(),
                t.getDuration().toMinutes(),
                "%s" // endTime for epics only
        );
        return switch (t) {
            case Epic e -> String.format(commonTemplate, TaskType.EPIC, "0", e.getEndTime());
            case Subtask s -> String.format(commonTemplate, TaskType.SUBTASK, s.getEpicId(), "0");
            default -> String.format(commonTemplate, TaskType.TASK, "0", "0");
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
        Integer id = super.createSubtask(subtask);
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
