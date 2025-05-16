package service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}