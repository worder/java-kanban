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

public class PrioritizedHandlerTest extends BaseHttpHandlerTest {
    @Test
    public void getPrioritizedSuccess() throws IOException, InterruptedException {
        Task t1 = new Task("Task #1 name", "Task #1 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 2, 0, 0, 0));
        Task t2 = new Task("Task #2 name", "Task #2 desc", TaskStatus.NEW, Duration.ofMinutes(59),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        manager.createTask(t1);
        manager.createTask(t2);

        HttpRequest request = HttpRequest.newBuilder().uri(baseURI.resolve(PrioritizedHandler.PATH)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}
