package http.handler;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;


public class HistoryHandler extends BaseHttpHandler {

    public static final String PATH = "/history";

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    public void get(HttpExchange exchange) throws IOException {
        this.sendText(this.toJson(manager.getHistory()), exchange);
    }
}