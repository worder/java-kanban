package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    public void equalsWithSameId() {
        Task task1 = new Task(1, "Task 1 name", "Task 1 desc", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 2 name", "Task 2 desc", TaskStatus.NEW);
        assertEquals(task1, task2);
    }
}