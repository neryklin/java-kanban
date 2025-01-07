package Api;

import com.google.gson.Gson;
import domain.Task;
import domain.TaskStatus;
import manager.FileBackedTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    FileBackedTaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeAllTask();
        HttpTaskServer.httpTaskServerGetDefault();
        httpTaskServer.startHttp();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stopHttp();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Task> tasksFromManager = manager.getTasksList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("fist task", tasksFromManager.get(task.getId()).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        task.setStatus(TaskStatus.DONE);
        task.setStartTime(LocalDateTime.now().plusDays(10));
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Task> tasksFromManager = manager.getTasksList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TaskStatus.NEW, tasksFromManager.get(task.getId()).getStatus(), "Задача не обновлилась!");
    }

    @Test
    public void testCrossingTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        task.setId(task.getId() + 1);
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Task> tasksFromManager = manager.getTasksList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач, не сработа проверка на пересечение, задача ошибочно добавлена.");

    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        Task task2 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(1));
        // создаём HTTP-клиент и запрос
        manager.addTask(task);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Task> tasksFromManager = manager.getTasksList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач, не сработало удаление первой задачи.");
        assertNull(tasksFromManager.get(task.getId()), "Ошибка удаление первой задачи., удались не корректная задача");

    }

    @Test
    public void testGetTaskId() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Task task2 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Task task3 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(5));
        manager.addTask(task);
        manager.addTask(task3);
        manager.addTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        Task task_finde = manager.getTaskFromId(task2.getId());
        assertNotNull(task_finde, "Задачи не возвращаются");
        assertEquals(task2.getId(), task_finde.getId(), "В списке под задач не корректная задача");
    }

    @Test
    public void testGetTaskList() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Task task2 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Task task3 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(5));
        manager.addTask(task);
        manager.addTask(task3);
        manager.addTask(task2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Task> epicsFromManager = manager.getTasksList();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(3, epicsFromManager.size(), "Некорректное количество задач");
    }
}