package Api;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import domain.*;
import manager.FileBackedTaskManager;
import manager.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static FileBackedTaskManager taskManager;
    static HttpServer httpServer;

    public HttpTaskServer(FileBackedTaskManager taskManager) {

        this.taskManager = taskManager;
    }

    public HttpTaskServer() {
        this.taskManager  = Managers.getDefault();
    }
    public static void httpTaskServerGetDefault() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/subtasks", new SubtaskHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());

    }
    public static Gson getBaseGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Duration.class, new HttpTaskServer.DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter());
        Gson gson = gsonBuilder.create();
        return gson;
    }

    public static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && pathParts[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            }
        }

        if (pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            }
        }
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

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPICS;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPICS;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPICS;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtask")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS_ID_SUBTASK;
            }
        }

        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASKS;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASKS;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASKS;
            }
        }

        return Endpoint.UNKNOWN;
    }

    static public Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }

    }

    public static void main(String[] args) throws IOException {
        taskManager = Managers.getDefault();
        generateTestData();

        httpTaskServerGetDefault();

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public FileBackedTaskManager getTaskManager() {
        return taskManager;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void startHttp(){
        httpServer.start();
    }

    public void stopHttp(){
        httpServer.stop(1);
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

    static class HistoryHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_HISTORY: {
                    handleGetHistory(exchange);
                    break;
                }
                case UNKNOWN: {
                    super.sendNotFound(exchange,"Не известный запрос.");
                    break;
                }
            }
        }
        private void handleGetHistory(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            super.sendText(exchange, gson.toJson(taskManager.getHistoryManager().getHistory()),200);
        }
    }

    static class PrioritizedHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_PRIORITIZED: {
                    handleGetPrioritized(exchange);
                    break;
                }
            }
        }
        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            super.sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()),200);
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
                case UNKNOWN: {
                    super.sendNotFound(exchange,"Не известный запрос.");
                    break;
                }

            }

        }



        private void handleGetTasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            super.sendText(exchange, gson.toJson(taskManager.getTasksList()),200);
        }

        private void handlePostTasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            String stringBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task tempTask = gson.fromJson(stringBody, Task.class);
            tempTask.calculateEndTime();
            List<Integer> crossingTask  = taskManager.addTaskToBusyPlan(tempTask);
            if (crossingTask.size()==0) {
                taskManager.addTask(tempTask);
                tempTask.calculateEndTime();
            } else {
                super.sendHasInteractions(exchange,"Такс не добавлен так как он пересекается"+gson.toJson(crossingTask));
                return;
            }
            if (taskOptinal.isEmpty()) {
                //новый такс
                    super.sendText(exchange, "Добвлан новый Таск " + tempTask.getId(),201);
            } else {
                super.sendText(exchange, "Обновлен Таск " + tempTask.getId(),201);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            int taskId = taskOptinal.get();
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Task с ID " + taskId + " не найден!"));
            } else {
                taskManager.removeTaskFromId(taskId);
                super.sendText(exchange, "Удален Таск " + taskId,200);
            }
        }

        private void handleGetTasksId(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Не верный параметр запроса"));
            } else {
                int taskId = taskOptinal.get();
                Task taskToSend = taskManager.getTaskFromId(taskId);
                if (taskToSend != null) {
                    super.sendText(exchange, gson.toJson(taskToSend),200);
                } else {
                    super.sendNotFound(exchange, gson.toJson("Task с ID " + taskId + " не найден!"));
                }
            }
        }
    }


    static class EpicHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_EPICS: {
                    handleGetEpics(exchange);
                    break;
                }
                case GET_EPICS_ID: {
                    handleGetEpicsId(exchange);
                    break;
                }
                case POST_EPICS: {
                    handlePostEpics(exchange);
                    break;
                }
                case DELETE_EPICS: {
                    handleDeleteEpics(exchange);
                    break;
                }
                case GET_EPICS_ID_SUBTASK: {
                    handleGetEpicsIdSubTask(exchange);
                    break;
                }
                case UNKNOWN: {
                    super.sendNotFound(exchange,"Не известный запрос.");
                    break;
                }

            }

        }

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            super.sendText(exchange, gson.toJson(taskManager.getEpicsList()),200);
        }

        private void handlePostEpics(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            String stringBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic tempEpic = gson.fromJson(stringBody, Epic.class);
            tempEpic.calculateEndTime();
            List<Integer> crossingTask  = taskManager.addTaskToBusyPlan(tempEpic);
            if (crossingTask.size()==0) {
                taskManager.addEpic(tempEpic);
                tempEpic.calculateEndTime();
            } else {
                super.sendHasInteractions(exchange,"Такс_не_добавлен_так_как_он_пересекается_c_таск(ми)_"+gson.toJson(crossingTask));
                return;
            }
            if (taskOptinal.isEmpty()) {
                //новый такс
                super.sendText(exchange, "Добавлан новый Epic " + tempEpic.getId(),201);
            } else {
                //обновляем имеющийся
                super.sendText(exchange, "Обновлен Epic " + tempEpic.getId(),201);
            }
        }

        private void handleDeleteEpics(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            int taskId = taskOptinal.get();
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Epic с ID " + taskId + " не найден!"));
            } else {
                taskManager.removeTaskFromId(taskId);
                super.sendText(exchange, "Удален Epic " + taskId,200);
            }
        }

        private void handleGetEpicsId(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Не верный параметр запроса"));
            } else {
                int taskId = taskOptinal.get();
                Task taskToSend = taskManager.getEpicFromId(taskId);
                if (taskToSend != null) {
                    super.sendText(exchange, gson.toJson(taskToSend),200);
                } else {
                    super.sendNotFound(exchange, gson.toJson("Epics с ID " + taskId + " не найден!"));
                }
            }
        }

        private void handleGetEpicsIdSubTask(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Не верный параметр запроса"));
            } else {
                int taskId = taskOptinal.get();
                HashMap<Integer, Subtask> taskToSend = taskManager.getSubTasksListFromEpic(taskManager.getEpicFromId(taskId));
                if (taskToSend != null) {
                    super.sendText(exchange, gson.toJson(taskToSend),200);
                } else {
                    super.sendNotFound(exchange, gson.toJson("Epics с ID " + taskId + " не найден!"));
                }
            }
        }
    }


    static class SubtaskHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_SUBTASKS: {
                    handleGetSubtasks(exchange);
                    break;
                }
                case GET_SUBTASKS_ID: {
                    handleGetSubtasksId(exchange);
                    break;
                }
                case POST_SUBTASKS: {
                    handlePostSubtasks(exchange);
                    break;
                }
                case DELETE_SUBTASKS: {
                    handleDeleteSubtasks(exchange);
                    break;
                }
                case UNKNOWN: {
                    super.sendNotFound(exchange,"Не известный запрос.");
                    break;
                }

            }

        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            super.sendText(exchange, gson.toJson(taskManager.getAllSubTasksList()),200);
        }

        private void handlePostSubtasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            String stringBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask tempSubtask = gson.fromJson(stringBody, Subtask.class);
            tempSubtask.calculateEndTime();
            List<Integer> crossingTask  = taskManager.addTaskToBusyPlan(tempSubtask);
            if (crossingTask.size()==0) {
                taskManager.addEpicIdSubTask(tempSubtask.getEpicTaskId(),tempSubtask);
                tempSubtask.calculateEndTime();
                tempSubtask.getEpic().calculateEndTime();
            } else {
                super.sendHasInteractions(exchange,"Такс_не_добавлен_так_как_он_пересекается_c_таск(ми)_"+gson.toJson(crossingTask));
                return;
            }
            if (taskOptinal.isEmpty()) {
                //новый такс
                super.sendText(exchange, "Добавлан новый Subtask " + tempSubtask.getId(),201);
            } else {
                //обновляем имеющийся
                super.sendText(exchange, "Обновлен Subtask " + tempSubtask.getId(),201);
            }
        }

        private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            int taskId = taskOptinal.get();
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Subtask с ID " + taskId + " не найден!"));
            } else {
                taskManager.removeTaskFromId(taskId);
                super.sendText(exchange, "Удален Subtask " + taskId,200);
            }
        }

        private void handleGetSubtasksId(HttpExchange exchange) throws IOException {
            Gson gson = getBaseGson();
            var taskOptinal = getTaskId(exchange);
            if (taskOptinal.isEmpty()) {
                super.sendNotFound(exchange, gson.toJson("Не верный параметр запроса"));
            } else {
                int taskId = taskOptinal.get();
                Task taskToSend = taskManager.getSubtaskFromId(taskId);
                if (taskToSend != null) {
                    super.sendText(exchange, gson.toJson(taskToSend),200);
                } else {
                    super.sendNotFound(exchange, gson.toJson("Subtasks с ID " + taskId + " не найден!"));
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
        taskManager.getTaskFromId(2);

        Epic epic3 = new Epic("third  epic", "third", TaskStatus.NEW);
        Epic epic4 = new Epic("third  epic", "third", TaskStatus.NEW);
        Subtask subtask4 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        taskManager.addEpicSubTask(epic3, subtask4);
        taskManager.addEpic(epic3);
        taskManager.addEpic(epic4);
    }
}
