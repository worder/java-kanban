package http.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;
import service.exception.InMemoryTaskManagerCreateException;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {

    public static final String PATH = "/tasks";

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void get(HttpExchange exchange) throws IOException {
        if (this.hasId(exchange)) {
            try {
                int id = this.getId(exchange);
                Task task = manager.getTaskById(id);
                if (task != null) {
                    this.sendText(this.toJson(task), exchange);
                } else {
                    this.sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                this.sendBadRequest(exchange);
            }
        } else {
            this.sendText(this.toJson(manager.getAllTasks()), exchange);
        }
    }

    @Override
    public void post(HttpExchange exchange) throws IOException {
        Task task = fromInputJson(exchange, Task.class);
        try {
            if (task.getId() != null) {
                manager.updateTask(task);
            } else {
                manager.createTask(task);
            }
            sendCreated(exchange);
        } catch (InMemoryTaskManagerCreateException e) {
            sendNotAcceptable(exchange);
        }
    }

    @Override
    public void delete(HttpExchange exchange) throws IOException {
        if (this.hasId(exchange)) {
            try {
                int id = this.getId(exchange);
                manager.deleteTask(id);
                sendText("", exchange);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            sendBadRequest(exchange);
        }
    }
}
