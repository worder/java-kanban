package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    public void equalsWithSameId() {
        LocalDateTime time = LocalDateTime.of(2025, 5, 1, 9,0);

        Task task1 = new Task(1, "Task 1 name", "Task 1 desc", TaskStatus.NEW, Duration.ofHours(1), time);
        Task task2 = new Task(1, "Task 2 name", "Task 2 desc", TaskStatus.NEW, Duration.ofHours(1), time);

        assertEquals(task1, task2);
    }
}