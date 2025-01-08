package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void equalsWithSameId() {
        Epic epic1 = new Epic(1, new ArrayList<>(), "Epic 1", "Epic 1 desc");
        Epic epic2 = new Epic(1, new ArrayList<>(), "Epic 2", "Epic 2 desc");

        assertEquals(epic1, epic2);
    }

}