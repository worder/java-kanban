package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    public void getDefaultShouldReturnManager() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());
    }

    @Test
    public void getDefaultHistoryShouldReturnHistoryManager() {
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory());
    }
}