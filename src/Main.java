import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {

    static TaskManager taskManager;

    public static void main(String[] args) {
        taskManager = Managers.getDefault();

//        taskManager.createTask(new Task("Task N", "Task N description", TaskStatus.NEW));
//        taskManager.createTask(new Task("Task 2", "Task 2 description", TaskStatus.DONE));
//
//        int epic1Id = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
//        int epic2Id = taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));
//
//        taskManager.createSubtask(new Subtask(epic1Id, "Subtask 1", "Subtask for epic 1", TaskStatus.NEW));
//        taskManager.createSubtask(new Subtask(epic2Id, "Subtask 2", "Subtask for epic 2", TaskStatus.NEW));

        printAllTasks();
    }

    public static void printAllTasks() {
        System.out.println("-".repeat(60));

        System.out.println("All tasks:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println("> " + task);
        }
        System.out.println();

        System.out.println("All epics:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println("> " + epic);
        }
        System.out.println();

        System.out.println("All subtasks:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println("> " + subtask);
        }

        System.out.println("-".repeat(60));
        System.out.println();
    }
}
