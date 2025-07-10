package http.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import model.Task;
import service.TaskManager;
import service.exception.InMemoryTaskManagerCreateException;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    public static final String PATH = "/subtasks";

    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }

    void get(HttpExchange exchange) throws IOException {
        if (this.hasId(exchange)) {
            try {
                int id = this.getId(exchange);
                Subtask subtask = manager.getSubtaskById(id);
                if (subtask != null) {
                    sendText(this.toJson(subtask), exchange);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            sendText(this.toJson(manager.getAllSubtasks()), exchange);
        }
    }

    void post(HttpExchange exchange) throws IOException {
        Subtask subtask = fromInputJson(exchange, Subtask.class);
        try {
            if (subtask.getId() != null) {
                manager.updateSubtask(subtask);
            } else {
                manager.createSubtask(subtask);
            }
            sendCreated(exchange);
        } catch (InMemoryTaskManagerCreateException e) {
            sendNotAcceptable(exchange);
        }
    }

    void delete(HttpExchange exchange) throws IOException {
        if (this.hasId(exchange)) {
            try {
                int id = this.getId(exchange);
                manager.deleteSubtask(id);
                sendText("", exchange);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            sendBadRequest(exchange);
        }
    }
}
