package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static TaskManager tm;

    @BeforeEach
    public void prepareTaskManager() {
        tm = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    public void createTask() {
        Task task = new Task("Task name", "Task desc", TaskStatus.NEW);
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

        Subtask subtask = new Subtask(epicId, "Subtask name", "Subtask desc", TaskStatus.NEW);
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
        Subtask subtask = new Subtask(1, "Subtask name", "Subtask desc", TaskStatus.NEW);
        tm.createSubtask(subtask);
        assertEquals(0, tm.getAllSubtasks().size());
    }

    @Test
    public void idMustBeAssignedByManagerOnTaskCreation() {
        int task1Id = tm.createTask(new Task("Task 1", "Task 1 desc", TaskStatus.NEW));
        int task2Id = tm.createTask(new Task(task1Id, "Task 2", "Task 2 desc", TaskStatus.NEW));
        assertNotEquals(task1Id, task2Id);
    }

    @Test
    public void idMustBeAssignedByManagerOnEpicCreation() {
        int epic1Id = tm.createEpic(new Epic("Epic 1", "Epic 1 desc"));
        int epic2Id = tm.createEpic(new Epic(epic1Id, "Epic 2", "Epic 2 desc"));
        assertNotEquals(epic1Id, epic2Id);
    }

    @Test
    public void idMustBeAssignedByManagerOnSubtaskCreation() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        int st1Id = tm.createSubtask(new Subtask(epicId, "Subtask 2", "Subtask 2 desc", TaskStatus.NEW));
        int st2Id = tm.createSubtask(new Subtask(st1Id, epicId, "Subtask 2", "Subtask 2 desc",
                TaskStatus.NEW));
        assertNotEquals(st1Id, st2Id);
    }

    @Test
    public void updateTask() {
        int taskId = tm.createTask(new Task("Task 1", "Task 1", TaskStatus.NEW));
        Task update = new Task(taskId, "Task 1 updated", "Task 1 desc updated",
                TaskStatus.IN_PROGRESS);
        tm.updateTask(update);

        Task updatedTask = tm.getTaskById(taskId);
        assertEquals(update.getName(), updatedTask.getName());
        assertEquals(update.getDescription(), updatedTask.getDescription());
        assertEquals(update.getStatus(), updatedTask.getStatus());
    }

    @Test
    public void updateEpic() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        Epic update = new Epic(epicId, "Epic name updated", "Epic desc updated");
        tm.updateEpic(update);

        Epic updatedEpic = tm.getEpicById(epicId);
        assertEquals(update.getName(), updatedEpic.getName());
        assertEquals(update.getDescription(), updatedEpic.getDescription());
    }

    @Test
    public void updateSubtask() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        int subtaskId = tm.createSubtask(new Subtask(epicId, "Subtask name", "Subtask desc",
                TaskStatus.NEW));
        Subtask update = new Subtask(subtaskId, epicId, "Subtask name updated", "Subtask desc updated",
                TaskStatus.IN_PROGRESS);
        tm.updateSubtask(update);

        Subtask updatedSubtask = tm.getSubtaskById(subtaskId);
        assertEquals(update.getName(), updatedSubtask.getName());
        assertEquals(update.getDescription(), updatedSubtask.getDescription());
        assertEquals(update.getStatus(), updatedSubtask.getStatus());
    }

    @Test
    public void deleteTask() {
        int taskId = tm.createTask(new Task("Task", "Task desc", TaskStatus.NEW));
        assertNotNull(tm.getTaskById(taskId));
        tm.deleteTask(taskId);
        assertNull(tm.getTaskById(taskId));
    }

    @Test
    public void deleteEpic() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        int subtaskId = tm.createSubtask(new Subtask(epicId, "Subtask name", "Subtask desc",
                TaskStatus.NEW));
        assertNotNull(tm.getEpicById(epicId));
        assertNotNull(tm.getSubtaskById(subtaskId));

        tm.deleteEpic(epicId);
        assertNull(tm.getEpicById(epicId));
        assertNull(tm.getSubtaskById(subtaskId));
    }

    @Test
    public void deleteSubtask() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        int subtaskId = tm.createSubtask(new Subtask(epicId, "Subtask name", "Subtask desc",
                TaskStatus.NEW));
        assertNotNull(tm.getSubtaskById(subtaskId));
        assertEquals(1, tm.getEpicSubtasks(epicId).size());

        tm.deleteSubtask(subtaskId);
        assertNull(tm.getSubtaskById(subtaskId));
        assertEquals(0, tm.getEpicSubtasks(epicId).size());
    }

    @Test
    public void deleteAllTasks() {
        tm.createTask(new Task("Task 1", "Task 1 desc", TaskStatus.NEW));
        tm.createTask(new Task("Task 2", "Task 2 desc", TaskStatus.NEW));
        tm.deleteAllTasks();

        assertEquals(0, tm.getAllTasks().size());
    }

    @Test
    public void deleteAllEpics() {
        int epic1Id = tm.createEpic(new Epic("Epic name", "Epic desc"));
        tm.createEpic(new Epic("Epic name", "Epic desc"));
        tm.createEpic(new Epic("Epic name", "Epic desc"));
        tm.createSubtask(new Subtask(epic1Id, "Subtask", "Subtask desc", TaskStatus.NEW));
        tm.deleteAllEpics();

        assertEquals(0, tm.getAllSubtasks().size());
        assertEquals(0, tm.getAllEpics().size());
    }

    @Test
    public void deleteAllSubtasks() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        tm.createSubtask(new Subtask(epicId, "Subtask 1", "Subtask 1 desc", TaskStatus.NEW));
        tm.createSubtask(new Subtask(epicId, "Subtask 2", "Subtask 2 desc", TaskStatus.NEW));
        tm.deleteAllSubtasks();

        assertEquals(0, tm.getAllSubtasks().size());
        assertEquals(0, tm.getEpicSubtasks(epicId).size());
    }

    @Test
    public void getEpicSubtasks() {
        int epic1Id = tm.createEpic(new Epic("Epic name", "Epic desc"));
        tm.createSubtask(new Subtask(epic1Id, "Subtask 1", "Subtask 1 desc", TaskStatus.NEW));
        tm.createSubtask(new Subtask(epic1Id, "Subtask 2", "Subtask 2 desc", TaskStatus.NEW));
        tm.createSubtask(new Subtask(epic1Id, "Subtask 3", "Subtask 3 desc", TaskStatus.NEW));

        int epic2Id = tm.createEpic(new Epic("Epic name", "Epic desc"));
        tm.createSubtask(new Subtask(epic2Id, "Subtask 4", "Subtask 4 desc", TaskStatus.NEW));
        tm.createSubtask(new Subtask(epic2Id, "Subtask 5", "Subtask 5 desc", TaskStatus.NEW));

        assertEquals(3, tm.getEpicSubtasks(epic1Id).size());
        assertEquals(2, tm.getEpicSubtasks(epic2Id).size());
        assertNull(tm.getEpicSubtasks(100));
    }

    @Test
    public void addTaskToHistoryWhenViewed() {
        int task1id = tm.createTask(new Task("Task 1", "desc", TaskStatus.NEW));
        int epic1Id = tm.createEpic(new Epic("Epic 1", "desc"));
        int subtask1Id = tm.createSubtask(new Subtask(epic1Id, "Subtask 1", "desc", TaskStatus.NEW));

        Task first = tm.getTaskById(task1id);
        tm.getEpicById(epic1Id);
        Subtask last = tm.getSubtaskById(subtask1Id);

        assertEquals(3, tm.getHistory().size());
        assertEquals(first, tm.getHistory().getFirst());
        assertEquals(last, tm.getHistory().getLast());
    }

    @Test
    public void taskShouldNotDuplicateInHistory() {
        int task1id = tm.createTask(new Task("Task 1", "desc", TaskStatus.NEW));
        int task2id = tm.createTask(new Task("Task 2", "desc", TaskStatus.NEW));
        int task3id = tm.createTask(new Task("Task 3", "desc", TaskStatus.NEW));
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
        int task1id = tm.createTask(new Task("Task 1", "desc", TaskStatus.NEW));
        int task2id = tm.createTask(new Task("Task 2", "desc", TaskStatus.NEW));
        tm.getTaskById(task1id);
        tm.getTaskById(task2id);

        tm.deleteTask(task1id);

        assertEquals(1, tm.getHistory().size());
    }

    @Test
    public void deleteAllTasksShouldDeleteTasksFromHistory() {
        int task1id = tm.createTask(new Task("Task 1", "desc", TaskStatus.NEW));
        int task2id = tm.createTask(new Task("Task 2", "desc", TaskStatus.NEW));
        tm.getTaskById(task1id);
        tm.getTaskById(task2id);

        tm.deleteAllTasks();

        assertEquals(0, tm.getHistory().size());
    }

    @Test
    public void deleteSubtaskShouldDeleteSubtaskFromHistory() {
        int epicId = tm.createEpic(new Epic("Epic 1", "desc"));
        int subtask1id = tm.createSubtask(new Subtask(epicId, "Subtask 1", "desc", TaskStatus.NEW));
        int subtask2id = tm.createSubtask(new Subtask(epicId, "Subtask 2", "desc", TaskStatus.NEW));
        tm.getEpicById(epicId);
        tm.getSubtaskById(subtask1id);
        tm.getSubtaskById(subtask2id);

        tm.deleteSubtask(subtask1id);

        assertEquals(2, tm.getHistory().size());
    }

    @Test
    public void deleteAllSubtasksShouldDeleteSubtasksFromHistory() {
        int epicId = tm.createEpic(new Epic("Epic 1", "desc"));
        int subtask1id = tm.createSubtask(new Subtask(epicId, "Subtask 1", "desc", TaskStatus.NEW));
        int subtask2id = tm.createSubtask(new Subtask(epicId, "Subtask 2", "desc", TaskStatus.NEW));
        tm.getEpicById(epicId);
        tm.getSubtaskById(subtask1id);
        tm.getSubtaskById(subtask2id);

        tm.deleteAllSubtasks();

        assertEquals(1, tm.getHistory().size());
    }

    @Test
    public void deleteEpicShouldDeleteEpicAndItsSubtasksFromHistory() {
        int epic1Id = tm.createEpic(new Epic("Epic 1", "desc"));
        int subtask1id = tm.createSubtask(new Subtask(epic1Id, "Subtask 1", "desc", TaskStatus.NEW));
        int subtask2id = tm.createSubtask(new Subtask(epic1Id, "Subtask 2", "desc", TaskStatus.NEW));

        int epic2Id = tm.createEpic(new Epic("Epic 2", "desc"));
        int subtask3id = tm.createSubtask(new Subtask(epic2Id, "Subtask 3", "desc", TaskStatus.NEW));

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
        int subtask1id = tm.createSubtask(new Subtask(epic1Id, "Subtask 1", "desc", TaskStatus.NEW));
        int subtask2id = tm.createSubtask(new Subtask(epic1Id, "Subtask 2", "desc", TaskStatus.NEW));

        int epic2Id = tm.createEpic(new Epic("Epic 2", "desc"));
        int subtask3id = tm.createSubtask(new Subtask(epic2Id, "Subtask 3", "desc", TaskStatus.NEW));

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
        String name = "Task name";
        String desc = "Task desc";
        TaskStatus status = TaskStatus.NEW;

        int taskId = tm.createTask(new Task(name, desc, status));
        Task task = tm.getTaskById(taskId);
        tm.updateTask(new Task(task, "New name", "New desc", TaskStatus.IN_PROGRESS));

        Task historyTask = tm.getHistory().getFirst();
        assertEquals(name, historyTask.getName());
        assertEquals(desc, historyTask.getDescription());
        assertEquals(status, historyTask.getStatus());
    }

    @Test
    public void calculateEpicStatusBasedOnSubtasksStatus() {
        int epicId = tm.createEpic(new Epic("Epic name", "Epic desc"));
        assertEquals(TaskStatus.NEW, tm.getEpicById(epicId).getStatus());

        int st1Id = tm.createSubtask(new Subtask(epicId, "Subtask 1", "desc", TaskStatus.NEW));
        int st2Id = tm.createSubtask(new Subtask(epicId, "Subtask 2", "desc", TaskStatus.NEW));
        int st3Id = tm.createSubtask(new Subtask(epicId, "Subtask 3", "desc", TaskStatus.NEW));
        assertEquals(TaskStatus.NEW, tm.getEpicById(epicId).getStatus());

        Subtask st1 = tm.getSubtaskById(st1Id);
        Subtask st2 = tm.getSubtaskById(st2Id);
        Subtask st3 = tm.getSubtaskById(st3Id);

        tm.updateSubtask(new Subtask(st1, st1.getName(), st1.getDescription(), TaskStatus.IN_PROGRESS));
        tm.updateSubtask(new Subtask(st2, st1.getName(), st2.getDescription(), TaskStatus.DONE));
        assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicById(epicId).getStatus());

        tm.updateSubtask(new Subtask(st1, st1.getName(), st1.getDescription(), TaskStatus.DONE));
        tm.updateSubtask(new Subtask(st2, st1.getName(), st2.getDescription(), TaskStatus.DONE));
        assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicById(epicId).getStatus());

        tm.updateSubtask(new Subtask(st1, st1.getName(), st1.getDescription(), TaskStatus.DONE));
        tm.updateSubtask(new Subtask(st2, st1.getName(), st2.getDescription(), TaskStatus.DONE));
        tm.updateSubtask(new Subtask(st3, st3.getName(), st3.getDescription(), TaskStatus.DONE));
        assertEquals(TaskStatus.DONE, tm.getEpicById(epicId).getStatus());

        tm.deleteSubtask(st1.getId());
        tm.deleteSubtask(st2.getId());
        tm.deleteSubtask(st3.getId());
        assertEquals(TaskStatus.NEW, tm.getEpicById(epicId).getStatus());
    }
}