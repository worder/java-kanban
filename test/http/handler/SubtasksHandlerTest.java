package http.handler;

import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtasksHandlerTest extends BaseHttpHandlerTest {
    @Test
    public void createSubtaskSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseURI.resolve(SubtasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals(subtask.getName(), manager.getAllSubtasks().getFirst().getName());
    }

    @Test
    public void createSubtaskNotAcceptable() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        Subtask s1 = new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        manager.createSubtask(s1);

        Subtask s2 = new Subtask(epicId, "Subtask #2", "Subtask #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 10, 0));

        String s2json = gson.toJson(s2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseURI.resolve(SubtasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(s2json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    public void editSubtaskSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        manager.createSubtask(new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0)));
        Subtask s1 = manager.getAllSubtasks().getFirst();

        Subtask s1e = new Subtask(epicId, "Subtask #1 name edited", s1.getDescription(), s1.getStatus(),
                s1.getDuration(), s1.getStartTime()).withId(s1.getId());
        String s1eJson = gson.toJson(s1e);
        HttpRequest editRequest = HttpRequest.newBuilder()
                .uri(baseURI.resolve(SubtasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(s1eJson))
                .build();
        HttpResponse<String> editResponse = client.send(editRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, editResponse.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals(s1e.getName(), manager.getAllSubtasks().getFirst().getName());
    }

    @Test
    public void editSubtaskNotAcceptable() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        manager.createSubtask(new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW,
                Duration.ofMinutes(59), LocalDateTime.of(2025, 1, 1, 0, 0, 0)));
        Subtask s1 = manager.getAllSubtasks().getFirst();

        manager.createSubtask(new Subtask(epicId, "Subtask #2", "Subtask #2 desc", TaskStatus.NEW,
                Duration.ofMinutes(59), LocalDateTime.of(2025, 1, 1, 1, 0, 0)));

        Subtask s1e = new Subtask(epicId, "Subtask #1 name edited", s1.getDescription(), s1.getStatus(),
                s1.getDuration(), LocalDateTime.of(2025, 1, 1, 1, 0, 0)).withId(s1.getId());
        String s1eJson = gson.toJson(s1e);
        HttpRequest editRequest = HttpRequest.newBuilder()
                .uri(baseURI.resolve(SubtasksHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(s1eJson))
                .build();
        HttpResponse<String> editResponse = client.send(editRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, editResponse.statusCode());
    }

    @Test
    public void getAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        Subtask s1 = new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        manager.createSubtask(s1);

        Subtask s2 = new Subtask(epicId, "Subtask #2", "Subtask #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 1, 0, 0));
        manager.createSubtask(s2);

        URI uri = taskServer.getBaseURI().resolve(SubtasksHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSingleSubtaskSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        Subtask s1 = new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        int subtaskId = manager.createSubtask(s1);

        URI uri = baseURI.resolve(SubtasksHandler.PATH + "/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSingleSubtaskNotFound() throws IOException, InterruptedException {
        URI uri = baseURI.resolve(SubtasksHandler.PATH + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        Subtask s1 = new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        int taskId = manager.createSubtask(s1);

        URI uri = taskServer.getBaseURI().resolve(SubtasksHandler.PATH + "/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }
}
