package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void equalsWithSameId() {
        Subtask st1 = new Subtask(1, 3, "Subtask 1", "Subtask 1 desc", TaskStatus.NEW);
        Subtask st2 = new Subtask(1, 3, "Subtask 2", "Subtask 2 desc", TaskStatus.DONE);

        assertEquals(st1, st2);
    }
}