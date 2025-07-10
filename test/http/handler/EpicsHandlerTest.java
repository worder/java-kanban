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

public class EpicsHandlerTest extends BaseHttpHandlerTest {
    @Test
    public void createEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        String subtaskJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(baseURI.resolve(EpicsHandler.PATH))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(epic.getName(), manager.getAllEpics().getFirst().getName());
    }


    @Test
    public void getAllEpicsSuccess() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Epic #1 name", "Epic #1 desc"));
        manager.createEpic(new Epic("Epic #2 name", "Epic #2 desc"));

        URI uri = taskServer.getBaseURI().resolve(EpicsHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSingleEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        URI uri = baseURI.resolve(EpicsHandler.PATH + "/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSingleEpicNotFound() throws IOException, InterruptedException {
        URI uri = baseURI.resolve(EpicsHandler.PATH + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void getAllEpicSubtasksSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        manager.createSubtask(new Subtask(epicId, "Subtask #1", "Subtask #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0)));
        manager.createSubtask(new Subtask(epicId, "Subtask #2", "Subtask #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 1, 0, 0)));

        URI uri = baseURI.resolve(EpicsHandler.PATH + "/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getAllEpicSubtasksNotFound() throws IOException, InterruptedException {
        URI uri = baseURI.resolve(EpicsHandler.PATH + "/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic desc");
        int epicId = manager.createEpic(epic);

        URI uri = taskServer.getBaseURI().resolve(EpicsHandler.PATH + "/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllEpics().size());
    }
}
