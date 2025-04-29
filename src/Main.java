
import service.Managers;
import service.TaskManager;
import util.console.TasksPrinter;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

//        taskManager.createTask(new Task("Task N", "Task N description", TaskStatus.NEW));
//        taskManager.createTask(new Task("Task 2", "Task 2 description", TaskStatus.DONE));
//
//        int epic1Id = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
//        int epic2Id = taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));
//
//        taskManager.createSubtask(new Subtask(epic1Id, "Subtask 1", "Subtask for epic 1", TaskStatus.NEW));
//        taskManager.createSubtask(new Subtask(epic2Id, "Subtask 2", "Subtask for epic 2", TaskStatus.NEW));

        TasksPrinter.printAllTasks(taskManager);
    }
}
