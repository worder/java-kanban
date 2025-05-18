package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void equalsWithSameId() {
        LocalDateTime time = LocalDateTime.of(2025, 5, 1, 9,0);

        Subtask st1 = new Subtask(1, 3, "Subtask 1", "Subtask 1 desc", TaskStatus.NEW, Duration.ofHours(1), time);
        Subtask st2 = new Subtask(1, 3, "Subtask 2", "Subtask 2 desc", TaskStatus.DONE, Duration.ofHours(1), time);

        assertEquals(st1, st2);
    }
}