
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;
import util.console.TasksPrinter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        if (taskManager.getPrioritizedTasks().isEmpty()) {
            createExampleSet(taskManager);
        }

        TasksPrinter.printAllTasks(taskManager);
        TasksPrinter.printPrioritizedTasks(taskManager);
    }

    private static void createExampleSet(TaskManager taskManager) {
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 1, 9, 0);
        Duration duration = Duration.ofMinutes(59);

        taskManager.createTask(new Task("Task #1", "Task 1 description", TaskStatus.NEW, duration, startTime));
        taskManager.createTask(new Task("Task #2", "Task 2 description", TaskStatus.DONE, duration, startTime.plusHours(1)));

        int epic1Id = taskManager.createEpic(new Epic("Epic #1", "Epic 1 description"));
        int epic2Id = taskManager.createEpic(new Epic("Epic #2", "Epic 2 description"));

        taskManager.createSubtask(new Subtask(epic1Id, "Subtask #1 for epic #1", "Subtask 1 for epic 1", TaskStatus.NEW, duration, startTime.plusHours(2)));
        taskManager.createSubtask(new Subtask(epic2Id, "Subtask #2 for epic #2", "Subtask 2 for epic 2", TaskStatus.NEW, duration, startTime.plusHours(3)));
    }
}
