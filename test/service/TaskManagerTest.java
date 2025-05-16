package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {

    static TaskManager tm;
    private int createdTasksCounter = 0;

    // 15.05.2025 09:00
    protected final LocalDateTime timeRef = LocalDateTime.of(2025, 5, 15, 9, 0);
    protected final Duration durationRef = Duration.ofMinutes(59);

    abstract T getTaskManager();

    protected Task createTestTask(TaskStatus status, Duration duration, LocalDateTime startTime) {
        createdTasksCounter++;
        return new Task(
                "Task #" + createdTasksCounter,
                "Task #" + createdTasksCounter + " description",
                status,
                duration,
                startTime);
    }

    protected Subtask createTestSubtask(int epicId, TaskStatus status, Duration duration, LocalDateTime startTime) {
        createdTasksCounter++;
        return new Subtask(
                epicId,
                "Subtask #" + createdTasksCounter + " for epicId:" + epicId,
                "Subtask #" + createdTasksCounter + " description",
                status,
                duration,
                startTime);
    }

    protected Epic createTestEpic() {
        createdTasksCounter++;
        return new Epic("Epic #" + createdTasksCounter, "Epic #" + createdTasksCounter + " description");
    }

    @BeforeEach
    public void prepareTaskManager() {
        createdTasksCounter = 0;
        tm = getTaskManager();
    }

    @Test
    public void createTask() {
        Task task = createTestTask(TaskStatus.NEW, durationRef, timeRef);
        int taskId = tm.createTask(task);

        Task createdTask = tm.getTaskById(taskId);
        assertNotNull(createdTask);
        assertEquals(taskId, createdTask.getId());
        assertEquals(task.getName(), createdTask.getName());
        assertEquals(task.getDescription(), createdTask.getDescription());
        assertEquals(task.getStatus(), createdTask.getStatus());
    }

    @Test
    public void createEpic() {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = tm.createEpic(epic);

        Epic createdEpic = tm.getEpicById(epicId);
        assertNotNull(createdEpic);
        assertEquals(epicId, createdEpic.getId());
        assertEquals(epic.getName(), createdEpic.getName());
        assertEquals(epic.getDescription(), createdEpic.getDescription());
        assertEquals(TaskStatus.NEW, createdEpic.getStatus());
    }

    @Test
    public void createSubtask() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));

        Subtask subtask = createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef);
        int subtaskId = tm.createSubtask(subtask);

        Subtask createdSubtask = tm.getSubtaskById(subtaskId);
        assertNotNull(createdSubtask);
        assertEquals(epicId, createdSubtask.getEpicId());
        assertEquals(subtaskId, createdSubtask.getId());
        assertEquals(subtask.getName(), createdSubtask.getName());
        assertEquals(subtask.getDescription(), createdSubtask.getDescription());
        assertEquals(subtask.getStatus(), createdSubtask.getStatus());
    }

    @Test
    public void shouldNotAddSubtaskForNonExistentEpic() {
        Subtask subtask = createTestSubtask(1, TaskStatus.NEW, durationRef, timeRef);
        tm.createSubtask(subtask);
        assertEquals(0, tm.getAllSubtasks().size());
    }

    @Test
    public void idMustBeAssignedByManagerOnTaskCreation() {
        Task task = createTestTask(TaskStatus.NEW, durationRef, timeRef);
        int task1Id = tm.createTask(task);
        int task2Id = tm.createTask(task.withId(task1Id));
        assertNotEquals(task1Id, task2Id);
    }

    @Test
    public void idMustBeAssignedByManagerOnEpicCreation() {
        Epic epic = createTestEpic();
        int epic1Id = tm.createEpic(epic);
        int epic2Id = tm.createEpic(epic.withId(epic1Id));
        assertNotEquals(epic1Id, epic2Id);
    }

    @Test
    public void idMustBeAssignedByManagerOnSubtaskCreation() {
        int epicId = tm.createEpic(createTestEpic());
        Subtask st1 = createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef);
        int st1Id = tm.createSubtask(st1);
        Subtask st2 = createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef.plus(durationRef)).withId(st1Id);
        int st2Id = tm.createSubtask(st2);
        assertNotEquals(st1Id, st2Id);
    }

    @Test
    public void updateTask() {
        int taskId = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        Task update = createTestTask(
                TaskStatus.IN_PROGRESS,
                durationRef.plusHours(1),
                timeRef.plusHours(1))
                .withId(taskId);
        tm.updateTask(update);

        Task updatedTask = tm.getTaskById(taskId);
        assertEquals(update.getName(), updatedTask.getName());
        assertEquals(update.getDescription(), updatedTask.getDescription());
        assertEquals(update.getStatus(), updatedTask.getStatus());
        assertEquals(update.getStartTime(), updatedTask.getStartTime());
        assertEquals(update.getDuration(), updatedTask.getDuration());
    }

    @Test
    public void updateEpic() {
        int epicId = tm.createEpic(createTestEpic());
        Epic update = createTestEpic().withId(epicId);
        tm.updateEpic(update);

        Epic updatedEpic = tm.getEpicById(epicId);
        assertEquals(update.getName(), updatedEpic.getName());
        assertEquals(update.getDescription(), updatedEpic.getDescription());
    }

    @Test
    public void updateSubtask() {
        int epicId = tm.createEpic(createTestEpic());
        int subtaskId = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));
        Subtask update = createTestSubtask(epicId,
                TaskStatus.IN_PROGRESS,
                durationRef.plusHours(1),
                timeRef.plusHours(1))
                .withId(subtaskId);
        tm.updateSubtask(update);

        Subtask updatedSubtask = tm.getSubtaskById(subtaskId);
        assertEquals(update.getName(), updatedSubtask.getName());
        assertEquals(update.getDescription(), updatedSubtask.getDescription());
        assertEquals(update.getStatus(), updatedSubtask.getStatus());
        assertEquals(update.getDuration(), updatedSubtask.getDuration());
        assertEquals(update.getStartTime(), updatedSubtask.getStartTime());
    }

    @Test
    public void deleteTask() {
        int taskId = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        tm.deleteTask(taskId);
        assertNull(tm.getTaskById(taskId));
    }

    @Test
    public void deleteEpic() {
        int epicId = tm.createEpic(createTestEpic());
        int subtaskId = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));
        assertNotNull(tm.getEpicById(epicId));
        assertNotNull(tm.getSubtaskById(subtaskId));

        tm.deleteEpic(epicId);
        assertNull(tm.getEpicById(epicId));
        assertNull(tm.getSubtaskById(subtaskId));
    }

    @Test
    public void deleteSubtask() {
        int epicId = tm.createEpic(createTestEpic());
        int subtaskId = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));

        tm.deleteSubtask(subtaskId);
        assertNull(tm.getSubtaskById(subtaskId));
        assertEquals(0, tm.getEpicSubtasks(epicId).size());
    }

    @Test
    public void deleteAllTasks() {
        tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.deleteAllTasks();

        assertEquals(0, tm.getAllTasks().size());
    }

    @Test
    public void deleteAllEpics() {
        int epic1Id = tm.createEpic(createTestEpic());
        tm.createEpic(createTestEpic());
        tm.createEpic(createTestEpic());
        tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef));
        tm.deleteAllEpics();

        assertEquals(0, tm.getAllSubtasks().size());
        assertEquals(0, tm.getAllEpics().size());
    }

    @Test
    public void deleteAllSubtasks() {
        int epicId = tm.createEpic(createTestEpic());
        tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));
        tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.deleteAllSubtasks();

        assertEquals(0, tm.getAllSubtasks().size());
        assertEquals(0, tm.getEpicSubtasks(epicId).size());
    }

    @Test
    public void getEpicSubtasks() {
        int epic1Id = tm.createEpic(createTestEpic());
        tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef));
        tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef.plusHours(2)));

        int epic2Id = tm.createEpic(createTestEpic());
        tm.createSubtask(createTestSubtask(epic2Id, TaskStatus.NEW, durationRef, timeRef.plusHours(3)));
        tm.createSubtask(createTestSubtask(epic2Id, TaskStatus.NEW, durationRef, timeRef.plusHours(4)));

        assertEquals(3, tm.getEpicSubtasks(epic1Id).size());
        assertEquals(2, tm.getEpicSubtasks(epic2Id).size());
        assertEquals(0, tm.getEpicSubtasks(100).size());
    }

    @Test
    public void addTaskToHistoryWhenViewed() {
        int task1id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        int epic1Id = tm.createEpic(createTestEpic());
        int subtask1Id = tm.createSubtask(
                createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));

        Task first = tm.getTaskById(task1id);
        tm.getEpicById(epic1Id);
        Subtask last = tm.getSubtaskById(subtask1Id);

        assertEquals(3, tm.getHistory().size());
        assertEquals(first, tm.getHistory().getFirst());
        assertEquals(last, tm.getHistory().getLast());
    }

    @Test
    public void taskShouldNotDuplicateInHistory() {
        int task1id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        int task2id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        int task3id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef.plusHours(2)));
        tm.getTaskById(task1id);
        tm.getTaskById(task2id);

        Task last = tm.getTaskById(task3id);
        assertEquals(last, tm.getHistory().getLast());

        last = tm.getTaskById(task1id);
        assertEquals(last, tm.getHistory().getLast());
        assertEquals(3, tm.getHistory().size());
    }

    @Test
    public void deleteTaskShouldDeleteTaskFromHistory() {
        int task1id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        int task2id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.getTaskById(task1id);
        tm.getTaskById(task2id);

        tm.deleteTask(task1id);

        assertEquals(1, tm.getHistory().size());
    }

    @Test
    public void deleteAllTasksShouldDeleteTasksFromHistory() {
        int task1id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef));
        int task2id = tm.createTask(createTestTask(TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.getTaskById(task1id);
        tm.getTaskById(task2id);

        tm.deleteAllTasks();

        assertEquals(0, tm.getHistory().size());
    }

    @Test
    public void deleteSubtaskShouldDeleteSubtaskFromHistory() {
        int epicId = tm.createEpic(createTestEpic());
        int subtask1id = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));
        int subtask2id = tm.createSubtask(
                createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.getEpicById(epicId);
        tm.getSubtaskById(subtask1id);
        tm.getSubtaskById(subtask2id);

        tm.deleteSubtask(subtask1id);

        assertEquals(2, tm.getHistory().size());
    }

    @Test
    public void deleteAllSubtasksShouldDeleteSubtasksFromHistory() {
        int epicId = tm.createEpic(createTestEpic());
        int subtask1id = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));
        int subtask2id = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        tm.getEpicById(epicId);
        tm.getSubtaskById(subtask1id);
        tm.getSubtaskById(subtask2id);

        tm.deleteAllSubtasks();

        assertEquals(1, tm.getHistory().size());
    }

    @Test
    public void deleteEpicShouldDeleteEpicAndItsSubtasksFromHistory() {
        int epic1Id = tm.createEpic(new Epic("Epic 1", "desc"));
        int subtask1id = tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef));
        int subtask2id = tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));

        int epic2Id = tm.createEpic(new Epic("Epic 2", "desc"));
        int subtask3id = tm.createSubtask(createTestSubtask(epic2Id, TaskStatus.NEW, durationRef, timeRef.plusHours(2)));

        tm.getEpicById(epic1Id);
        tm.getEpicById(epic2Id);
        tm.getSubtaskById(subtask1id);
        tm.getSubtaskById(subtask2id);
        tm.getSubtaskById(subtask3id);

        tm.deleteEpic(epic1Id);

        assertEquals(2, tm.getHistory().size());
    }

    @Test
    public void deleteAllEpicsShouldDeleteAllEpicsAndSubtasksFromHistory() {
        int epic1Id = tm.createEpic(new Epic("Epic 1", "desc"));
        int subtask1id = tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef));
        int subtask2id = tm.createSubtask(createTestSubtask(epic1Id, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));

        int epic2Id = tm.createEpic(new Epic("Epic 2", "desc"));
        int subtask3id = tm.createSubtask(createTestSubtask(epic2Id, TaskStatus.NEW, durationRef, timeRef.plusHours(2)));

        tm.getEpicById(epic1Id);
        tm.getEpicById(epic2Id);
        tm.getSubtaskById(subtask1id);
        tm.getSubtaskById(subtask2id);
        tm.getSubtaskById(subtask3id);

        tm.deleteAllEpics();

        assertEquals(0, tm.getHistory().size());
    }

    @Test
    public void taskInHistoryShouldNotBeUpdatedWhenTaskIsUpdated() {
        TaskStatus status = TaskStatus.NEW;
        int taskId = tm.createTask(createTestTask(status, durationRef, timeRef));

        Task task = tm.getTaskById(taskId);
        String name = task.getName();
        String desc = task.getDescription();

        tm.updateTask(createTestTask(TaskStatus.DONE, durationRef, timeRef).withId(taskId));

        Task historyTask = tm.getHistory().getFirst();
        assertEquals(name, historyTask.getName());
        assertEquals(desc, historyTask.getDescription());
        assertEquals(status, historyTask.getStatus());
    }

    @Test
    public void calculateEpicStatusBasedOnSubtasksStatus() {
        int epicId = tm.createEpic(createTestEpic());
        assertEquals(TaskStatus.NEW, tm.getEpicById(epicId).getStatus());

        // NEW + NEW + NEW = NEW
        int st1Id = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef));
        int st2Id = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef.plusHours(1)));
        int st3Id = tm.createSubtask(createTestSubtask(epicId, TaskStatus.NEW, durationRef, timeRef.plusHours(2)));
        assertEquals(TaskStatus.NEW, tm.getEpicById(epicId).getStatus());

        Subtask st1 = tm.getSubtaskById(st1Id);
        Subtask st2 = tm.getSubtaskById(st2Id);
        Subtask st3 = tm.getSubtaskById(st3Id);

        // IN_PROGRESS + DONE + NEW =
        tm.updateSubtask(st1.withStatus(TaskStatus.IN_PROGRESS));
        tm.updateSubtask(st2.withStatus(TaskStatus.DONE));
        assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicById(epicId).getStatus());

        // DONE + DONE + NEW = IN_PROGRESS
        tm.updateSubtask(st1.withStatus(TaskStatus.DONE));
        tm.updateSubtask(st2.withStatus(TaskStatus.DONE));
        assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicById(epicId).getStatus());

        // DONE + DONE + DONE = DONE
        tm.updateSubtask(st1.withStatus(TaskStatus.DONE));
        tm.updateSubtask(st2.withStatus(TaskStatus.DONE));
        tm.updateSubtask(st3.withStatus(TaskStatus.DONE));
        assertEquals(TaskStatus.DONE, tm.getEpicById(epicId).getStatus());

        tm.deleteSubtask(st1.getId());
        tm.deleteSubtask(st2.getId());
        tm.deleteSubtask(st3.getId());
        assertEquals(TaskStatus.NEW, tm.getEpicById(epicId).getStatus());
    }
}
