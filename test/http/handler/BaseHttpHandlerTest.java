package http.handler;

import com.google.gson.Gson;
import http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

abstract public class BaseHttpHandlerTest {
    TaskManager manager;
    HttpTaskServer taskServer;
    HttpClient client;
    URI baseURI;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        this.manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        this.taskServer = new HttpTaskServer(manager);
        this.baseURI = taskServer.getBaseURI();
        this.client = HttpClient.newHttpClient();
        this.gson = TasksHandler.getGson();

        this.taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        this.taskServer.stop();
    }
}
