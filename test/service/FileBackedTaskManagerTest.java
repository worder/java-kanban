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

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    Path tempFileCommon;
    Path tempFileReadTest;
    Path tempFileWriteTest;
    HistoryManager history;

    String[] fileExample = {
            "1,TASK,Task #1,NEW,Task 1 description,0,2025-05-01T09:00,59,0",
            "2,TASK,Task #2,DONE,Task 2 description,0,2025-05-01T10:00,59,0",
            "3,EPIC,Epic #1,NEW,Epic 1 description,0,2025-05-01T11:00,59,2025-05-01T11:59",
            "4,EPIC,Epic #2,NEW,Epic 2 description,0,2025-05-01T12:00,59,2025-05-01T12:59",
            "5,SUBTASK,Subtask #1 for epic #1,NEW,Subtask 1 for epic 1,3,2025-05-01T11:00,59,0",
            "6,SUBTASK,Subtask #2 for epic #2,NEW,Subtask 2 for epic 2,4,2025-05-01T12:00,59,0",
            "" // ensure line break after last task
    };

    String fileExampleString = String.join(FileBackedTaskManager.LINE_SEPARATOR, fileExample);

    @Override
    FileBackedTaskManager getTaskManager() {
        try {
            tempFileCommon = Files.createTempFile("common", ".csv");
            return new FileBackedTaskManager(new InMemoryHistoryManager(), tempFileCommon);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @BeforeEach
    public void prepareTestFiles() {
        history = Managers.getDefaultHistory();
        try {
            tempFileReadTest = Files.createTempFile("readTest", ".csv");
            tempFileWriteTest = Files.createTempFile("writeTest", ".csv");

            BufferedWriter out = Files.newBufferedWriter(tempFileReadTest);
            out.write(fileExampleString);
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
            assertEquals(fileExampleString, fileWrittenLines);
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
        Task task = makeTestTask(TaskStatus.NEW, durationRef, timeRef);
        int id = tmLoaded.createTask(task);

        assertEquals(7, id);
    }
}
