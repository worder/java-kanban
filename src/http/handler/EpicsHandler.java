package http.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.TaskManager;
import service.exception.InMemoryTaskManagerCreateException;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {

    public static final String PATH = "/epics";

    public EpicsHandler(TaskManager manager) {
        super(manager);
    }

    void get(HttpExchange exchange) throws IOException {
        if (this.hasId(exchange)) {
            String[] uriArgs = this.getUriArgs(exchange);
            try {
                int id = this.getId(exchange);
                Epic epic = manager.getEpicById(id);
                if (epic != null) {
                    if (uriArgs.length > 3 && uriArgs[3].equals("subtasks")) {
                        this.sendText(this.toJson(manager.getEpicSubtasks(id)), exchange);
                    } else {
                        this.sendText(this.toJson(epic), exchange);
                    }
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            sendText(this.toJson(manager.getAllEpics()), exchange);
        }
    }

    void post(HttpExchange exchange) throws IOException {
        Epic epic = fromInputJson(exchange, Epic.class);
        try {
            manager.createEpic(epic);
            sendCreated(exchange);
        } catch (InMemoryTaskManagerCreateException e) {
            sendNotAcceptable(exchange);
        }
    }

    void delete(HttpExchange exchange) throws IOException {
        if (this.hasId(exchange)) {
            try {
                int id = this.getId(exchange);
                manager.deleteEpic(id);
                sendText("", exchange);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            sendBadRequest(exchange);
        }
    }
}
