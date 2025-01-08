package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    public void add(Task task) {
        if (task == null) return;
        if (history.size() == 10) {
            history.removeFirst();
        }
        history.add(task);
    }
}
