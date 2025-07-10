package http.handler;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public static final String PATH = "/prioritized";

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    public void get(HttpExchange exchange) throws IOException {
        this.sendText(this.toJson(manager.getPrioritizedTasks()), exchange);
    }
}