package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager hm;

    @BeforeEach
    public void prepareHistoryManager() {
        hm = new InMemoryHistoryManager();
    }

    @Test
    public void addTask() {
        Task task1 = new Task(1, "Task 1", "desc", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 3", "desc", TaskStatus.NEW);
        Task task3 = new Task(3, "Task 3", "desc", TaskStatus.NEW);
        hm.add(task1);
        hm.add(task2);
        hm.add(task3);

        assertEquals(task1, hm.getHistory().getFirst());
        assertEquals(task2, hm.getHistory().get(1));
        assertEquals(task3, hm.getHistory().getLast());
    }

    @Test
    public void removeTask() {
        Task task1 = new Task(1, "Task 1", "desc", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 2", "desc", TaskStatus.NEW);

        hm.add(task1);
        hm.add(task2);
        hm.remove(task1.getId());

        assertEquals(task2, hm.getHistory().getFirst());
    }

    @Test
    public void noDuplicateTasks() {
        Task task1 = new Task(1, "Task 1", "desc", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 3", "desc", TaskStatus.NEW);
        Task task3 = new Task(3, "Task 3", "desc", TaskStatus.NEW);

        hm.add(task1);
        hm.add(task1);

        assertEquals(task1, hm.getHistory().getLast());
        assertEquals(1, hm.getHistory().size());

        hm.add(task2);
        hm.add(task3);

        hm.add(task2);

        assertEquals(task2, hm.getHistory().getLast());
        assertEquals(3, hm.getHistory().size());
    }
}
