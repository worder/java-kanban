package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import http.json.adapter.DurationAdapter;
import http.json.adapter.LocalDateTimeAdapter;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int SERVER_PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        this.server.createContext(TasksHandler.PATH, new TasksHandler(manager));
        this.server.createContext(SubtasksHandler.PATH, new SubtasksHandler(manager));
        this.server.createContext(EpicsHandler.PATH, new EpicsHandler(manager));
        this.server.createContext(HistoryHandler.PATH, new HistoryHandler(manager));
        this.server.createContext(PrioritizedHandler.PATH, new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }

    public void start() {
        this.server.start();
        System.out.println("HTTP server started on port: " + SERVER_PORT);
    }

    public void stop() {
        this.server.stop(0);
        System.out.println("HTTP server stopped");
    }

    public URI getBaseURI() {
        try {
            return new URI("http://localhost:" + SERVER_PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
