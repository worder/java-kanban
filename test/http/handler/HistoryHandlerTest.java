package http.handler;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest extends BaseHttpHandlerTest {
    @Test
    public void getHistorySuccess() throws IOException, InterruptedException {
        Task t1 = new Task("Task #1 name", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 2, 0, 0, 0));
        Task t2 = new Task("Task #2 name", "Task #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        int t1id = manager.createTask(t1);
        int t2id = manager.createTask(t2);
        manager.getTaskById(t1id);
        manager.getTaskById(t2id);

        HttpRequest request = HttpRequest.newBuilder().uri(baseURI.resolve(HistoryHandler.PATH)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}
