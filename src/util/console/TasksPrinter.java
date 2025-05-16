package util.console;

import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class TasksPrinter {
    public static void printAllTasks(TaskManager tm) {
        System.out.println("-".repeat(60));

        System.out.println("All tasks:");
        for (Task task : tm.getAllTasks()) {
            System.out.println("> " + task);
        }
        System.out.println();

        System.out.println("All epics:");
        for (Epic epic : tm.getAllEpics()) {
            System.out.println("> " + epic);
        }
        System.out.println();

        System.out.println("All subtasks:");
        for (Subtask subtask : tm.getAllSubtasks()) {
            System.out.println("> " + subtask);
        }

        System.out.println("-".repeat(60));
        System.out.println();
    }

    public static void printPrioritizedTasks(TaskManager tm) {
        System.out.println("-".repeat(60));
        System.out.println("Prioritized tasks and subtasks list:");
        System.out.println("-".repeat(60));
        for (Task task : tm.getPrioritizedTasks()) {
            System.out.println("> " + task);
        }
    }
}
