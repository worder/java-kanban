package service;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        Path tasksFile = Paths.get("data", "tasks.csv");
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), tasksFile);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
