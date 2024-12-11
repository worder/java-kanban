public class Main {

    static TaskManager manager;

    public static void main(String[] args) {
        manager = new TaskManager();

        Task task1 = new Task("Task #1", "Task #1 description", TaskStatus.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Task #2", "Task #2 description", TaskStatus.DONE);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic #1", "Epic #1 description");
        manager.createEpic(epic1);
        Epic epic2 = new Epic("Epic #2", "Epic #2 description");
        manager.createEpic(epic2);

        printAllTasks();

        System.out.println("Adding subtasks to epic #1");

        // subtask 1
        Subtask st1 = new Subtask(
                epic1.getId(),
                "Subtask #1",
                "Subtask #1 description",
                TaskStatus.DONE);
        manager.createSubtask(st1);

        // subtask 2
        Subtask st2 = new Subtask(
                epic1.getId(),
                "Subtask #2",
                "Subtask #2 description",
                TaskStatus.DONE);
        manager.createSubtask(st2);

        // subtask 10 for epic 2
        Subtask st10 = new Subtask(
                epic2.getId(),
                "Subtask #10",
                "Subtask #10 description",
                TaskStatus.IN_PROGRESS);
        manager.createSubtask(st10);

        printAllTasks(); // epic #1 status is DONE

        System.out.println("Add subtask with status NEW to epic #1");

        // subtask 3
        Subtask st3 = new Subtask(
                epic1.getId(),
                "New subtask #3",
                "New subtask #3 description",
                TaskStatus.NEW);
        manager.createSubtask(st3);

        printAllTasks(); // epic #1 status is IN_PROGRESS

        System.out.println("Print epic #1 subtasks");
        for (Subtask st : manager.getEpicSubtasks(epic1.getId())) {
            System.out.println(">>> " + st);
        }
        System.out.println();

        System.out.println("Updating subtask #3");

        manager.updateSubtask(new Subtask(
                st3,
                "Done subtask #3",
                "Done subtask #3 description",
                TaskStatus.DONE));


        printAllTasks(); // subtask updated; epic status changed to DONE

        System.out.println("Updating task #1, epic #1");

        manager.updateEpic(new Epic(epic1, "Epic #1 updated name", "Epic #1 updated description"));
        manager.updateTask(new Task(
                task1,
                "Task #1 updated name",
                "Task #1 updated description",
                TaskStatus.IN_PROGRESS));

        printAllTasks();

        System.out.println("Delete subtask #2");

        manager.deleteSubtask(st2.getId());

        printAllTasks();

        System.out.println("Delete epic #1");

        manager.deleteEpic(epic1.getId());

        printAllTasks();

        System.out.println("Delete all subtasks");

        manager.deleteAllSubtasks();

        printAllTasks();

        System.out.println("Delete all epics and tasks");

        manager.deleteAllEpics();
        manager.deleteAllTasks();

        printAllTasks();
    }

    public static void printAllTasks() {
        System.out.println("-".repeat(60));

        System.out.println("All subtasks:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println("> " + subtask);
        }
        System.out.println();

        System.out.println("All epics:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println("> " + epic);
        }
        System.out.println();

        System.out.println("All tasks:");
        for (Task task : manager.getAllTasks()) {
            System.out.println("> " + task);
        }

        System.out.println("-".repeat(60));
        System.out.println();
    }
}
