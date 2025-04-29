package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FileBackedTaskManagerTest {

    static final String NL = System.lineSeparator();

    Path tempFileReadTest;
    Path tempFileWriteTest;
    HistoryManager history;

    String fileExample = "1,TASK,Task 1,NEW,Task 1 description," + NL
            + "2,TASK,Task 2,DONE,Task 2 description," + NL
            + "3,EPIC,Epic 1,NEW,Epic 1 description," + NL
            + "4,EPIC,Epic 2,NEW,Epic 2 description," + NL
            + "5,SUBTASK,Subtask 1,NEW,Subtask for epic 1,3" + NL
            + "6,SUBTASK,Subtask 2,NEW,Subtask for epic 2,4" + NL;

    @BeforeEach
    public void prepareTestFiles() {
        history = Managers.getDefaultHistory();
        try {
            tempFileReadTest = Files.createTempFile("readTest", ".csv");
            tempFileWriteTest = Files.createTempFile("writeTest", ".csv");

            BufferedWriter out = Files.newBufferedWriter(tempFileReadTest);
            out.write(fileExample);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void loadFromFile() {
        TaskManager tm = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);

        assertEquals(2, tm.getAllTasks().size());
        assertEquals(2, tm.getAllSubtasks().size());
        assertEquals(2, tm.getAllEpics().size());
    }

    @Test
    public void createTasksAndSaveToFile() {
        TaskManager tmLoaded = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);

        TaskManager tm = new FileBackedTaskManager(history, tempFileWriteTest);
        tm.createTask(tmLoaded.getTaskById(1));
        tm.createTask(tmLoaded.getTaskById(2));
        tm.createEpic(tmLoaded.getEpicById(3));
        tm.createEpic(tmLoaded.getEpicById(4));
        tm.createSubtask(tmLoaded.getSubtaskById(5));
        tm.createSubtask(tmLoaded.getSubtaskById(6));

        try {
            String fileWrittenLines = Files.readString(tempFileWriteTest);
            assertEquals(fileExample, fileWrittenLines);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void deletionRemovesFromFile() {
        TaskManager tm = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        tm.deleteTask(1);
        tm.deleteEpic(3); // also deletes subtask id:5
        tm.deleteSubtask(6);

        TaskManager tmLoaded = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        assertNull(tmLoaded.getTaskById(1));
        assertNull(tmLoaded.getEpicById(3));
        assertNull(tmLoaded.getSubtaskById(5));
        assertNull(tmLoaded.getSubtaskById(6));
    }

    @Test
    public void deleteAllRemovesAllFromFile() {
        TaskManager tm = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        TaskManager tmLoaded;

        tm.deleteAllTasks();
        tmLoaded = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        assertEquals(0, tmLoaded.getAllTasks().size());

        tm.deleteAllSubtasks();
        tmLoaded = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        assertEquals(0, tmLoaded.getAllSubtasks().size());

        tm.deleteAllEpics();
        tmLoaded = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        assertEquals(0, tmLoaded.getAllEpics().size());
    }

    @Test
    public void idAssignmentIsCorrectAfterLoad() {
        TaskManager tmLoaded = FileBackedTaskManager.loadFromFile(history, tempFileReadTest);
        Task task = new Task("Task with expected id 7", "desc", TaskStatus.NEW);
        int id = tmLoaded.createTask(task);

        assertEquals(7, id);
    }
}
