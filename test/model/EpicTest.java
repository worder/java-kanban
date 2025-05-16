package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void equalsWithSameId() {
        LocalDateTime time = LocalDateTime.of(2025, 5, 1, 9, 0);

        Epic epic1 = new Epic(
                1,
                "Epic 1",
                "Epic 1 desc",
                TaskStatus.NEW,
                new ArrayList<>(),
                Duration.ofHours(1),
                time,
                time.plusHours(1));

        Epic epic2 = new Epic(
                1,
                "Epic 2",
                "Epic 2 desc",
                TaskStatus.NEW,
                new ArrayList<>(),
                Duration.ofHours(1),
                time,
                time.plusHours(1));

        assertEquals(epic1, epic2);
    }

}