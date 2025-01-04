package Api;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import domain.Endpoint;
import domain.Task;
import domain.TaskStatus;
import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;

import java.awt.event.TextEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static FileBackedTaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) throws IOException {
        generateTestData();

        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    static private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }

    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.value((String) null);
            } else {
                jsonWriter.value(duration.toMinutes());
            }

        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(jsonReader.nextInt());
        }
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.value((String) null);
            } else {
                jsonWriter.value(localDateTime.toString());
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString());
        }
    }


    static class TaskHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASKS_ID: {
                    handleGetTasksId(exchange);
                    break;
                }
                case POST_TASKS: {
                    handlePostTasks(exchange);
                    break;
                }
                case DELETE_TASKS: {
                    handleDeleteTasks(exchange);
                    break;
                }

            }

        }


        private Endpoint getEndpoint(String requestPath, String requestMethod) {
            String[] pathParts = requestPath.split("/");
            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASKS;
                }
                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_TASKS;
                }
            }
            if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASKS_ID;
                }
                if (requestMethod.equals("POST")) {
                    return Endpoint.POST_TASKS;
                }
                if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_TASKS;
                }
            }
            return Endpoint.UNKNOWN;
        }

        private Gson getBaseGson(){
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            Gson gson = gsonBuilder.create();
            return gson;
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            super.sendText(exchange, gson.toJson(taskManager.getTasksList()));
        }

        private void handlePostTasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            String stringBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (taskOptinal.isEmpty()) {
                //новый такс
                Task tempTask  = gson.fromJson(stringBody,Task.class);
                taskManager.addTask(tempTask);
               // super.sendText(exchange, gson.toJson(taskToSend));
            }else {
                //обновляем имеющийся
                int taskId = taskOptinal.get();
                Task tempTask  = gson.fromJson(stringBody,Task.class);
                taskManager.addTask(tempTask);
            }
            super.sendText(exchange, gson.toJson(taskManager.getTasksList()));
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            int taskId = taskOptinal.get();
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange,gson.toJson("Task с ID "+taskId+" не найден!"));
            }else {
                //обновляем имеющийся
                taskManager.removeTaskFromId(taskId);
            }
            super.sendText(exchange, gson.toJson(taskManager.getTasksList()));
        }

        private void handleGetTasksId(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange,gson.toJson("Не верный параметр запроса"));
            }else {
                int taskId = taskOptinal.get();
                Task taskToSend = taskManager.getTaskFromId(taskId);
                if (taskToSend!=null) {
                    super.sendText(exchange, gson.toJson(taskToSend));
                } else {
                    super.sendNotFound(exchange,gson.toJson("Task с ID "+taskId+" не найден!"));
                }
            }
        }
    }


    public static void generateTestData() {

        LocalDateTime o = LocalDateTime.of(2024, 01, 01, 00, 00);
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        Task task1 = new Task("second task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(3));
        Task task2 = new Task("second  task", "paint red button", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task1);
        taskManager.addTask(task2);


    }
}
