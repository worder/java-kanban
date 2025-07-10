package http.handler;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TasksHandlerTest extends BaseHttpHandlerTest {
    @Test
    public void createTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseURI.resolve(TasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(task.getName(), manager.getAllTasks().getFirst().getName());
    }

    @Test
    public void createTaskNotAcceptable() throws IOException, InterruptedException {
        Task firstTask = new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        manager.createTask(firstTask);

        Task secondTask = new Task("Task #2", "Task #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 10, 0));

        String secondTaskJson = gson.toJson(secondTask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseURI.resolve(TasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(secondTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void editTaskSuccess() throws IOException, InterruptedException {
        manager.createTask(new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0)));
        Task createdTask = manager.getAllTasks().getFirst();

        Task editedTask = new Task("Task #1 name edited", createdTask.getDescription(), createdTask.getStatus(),
                createdTask.getDuration(), createdTask.getStartTime()).withId(createdTask.getId());
        String editedTaskJson = gson.toJson(editedTask);
        HttpRequest editRequest = HttpRequest.newBuilder()
                .uri(baseURI.resolve(TasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(editedTaskJson))
                .build();
        HttpResponse<String> editResponse = client.send(editRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, editResponse.statusCode());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(editedTask.getName(), manager.getAllTasks().getFirst().getName());
    }

    @Test
    public void editTaskNotAcceptable() throws IOException, InterruptedException {
        manager.createTask(new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0)));
        Task createdTask = manager.getAllTasks().getFirst();

        manager.createTask(new Task("Task #2", "Task #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 1, 0, 0)));

        Task editedTask = new Task("Task #1 name edited", createdTask.getDescription(), createdTask.getStatus(),
                createdTask.getDuration(), LocalDateTime.of(2025, 1, 1, 1, 0, 0)).withId(createdTask.getId());
        String editedTaskJson = gson.toJson(editedTask);
        HttpRequest editRequest = HttpRequest.newBuilder()
                .uri(baseURI.resolve(TasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(editedTaskJson))
                .build();
        HttpResponse<String> editResponse = client.send(editRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, editResponse.statusCode());
    }

    @Test
    public void getAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        manager.createTask(task1);

        Task task2 = new Task("Task #2", "Task #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 1, 0, 0));
        manager.createTask(task2);

        URI uri = taskServer.getBaseURI().resolve(TasksHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSingleTaskSuccess() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        int taskId = manager.createTask(task1);

        URI uri = baseURI.resolve(TasksHandler.PATH + "/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSingleTaskNotFound() throws IOException, InterruptedException {
        URI uri = baseURI.resolve(TasksHandler.PATH + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task #1", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        int taskId = manager.createTask(task1);

        URI uri = taskServer.getBaseURI().resolve(TasksHandler.PATH + "/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllTasks().size());
    }
}
