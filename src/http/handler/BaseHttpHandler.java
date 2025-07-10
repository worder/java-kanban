package http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpMethod;
import http.json.adapter.DurationAdapter;
import http.json.adapter.LocalDateTimeAdapter;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    final TaskManager manager;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void get(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    }

    public void post(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    };

    public void delete(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    }

    @Override
    public void handle(HttpExchange exchange) {
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        System.out.println(method + " " + exchange.getRequestURI().toString());

        try {
            switch (method) {
                case GET:
                    this.get(exchange);
                    break;
                case POST:
                    this.post(exchange);
                    break;
                case DELETE:
                    this.delete(exchange);
                    break;
                default:
                    this.sendBadRequest(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendServerError(exchange);
            exchange.close();
        }
    }

    protected void sendText(String text, HttpExchange exchange) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        exchange.close();
    }

    protected void sendNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }

    protected void sendServerError(HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
    }

    protected String[] getUriArgs(HttpExchange exchange) {
        return exchange
                .getRequestURI()
                .getPath()
                .split("/");
    }

    protected boolean hasId(HttpExchange exchange) {
        return getUriArgs(exchange).length > 2;
    }

    protected int getId(HttpExchange exchange) throws NumberFormatException {
        if (this.hasId(exchange)) {
            return Integer.parseInt(getUriArgs(exchange)[2]);
        }

        throw new NumberFormatException();
    }

    protected <T> String toJson(T value) {
        return getGson().toJson(value);
    }

    protected <T> T fromInputJson(HttpExchange exchange, Class<T> type) throws IOException {
        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        return getGson().fromJson(body, type);
    }
}
