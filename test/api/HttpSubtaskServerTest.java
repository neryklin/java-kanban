package api;

import com.google.gson.Gson;
import domain.Epic;
import domain.Subtask;
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

public class HttpSubtaskServerTest {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(10));
        Subtask subtask5 = new Subtask(48, "xcv", TaskStatus.NEW, "sdf", epic.getId(), Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10));
        manager.addEpic(epic);
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(subtask5);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Subtask> epicsFromManager = manager.getAllSubTasksList();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtaskList() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Epic epic2 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Subtask subtask4 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        Subtask subtask5 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        manager.addEpicSubTask(epic, subtask4);
        manager.addEpicSubTask(epic2, subtask5);
        manager.addEpic(epic);
        manager.addEpic(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Subtask> subtaskFromManager = manager.getAllSubTasksList();
        assertNotNull(subtaskFromManager, "Задачи не возвращаются");
        assertEquals(2, subtaskFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicId() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Epic epic2 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Subtask subtask4 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        Subtask subtask5 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        manager.addEpicSubTask(epic, subtask4);
        manager.addEpicSubTask(epic2, subtask5);
        manager.addEpic(epic);
        manager.addEpic(epic2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask5.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        Subtask sub_finde = manager.getSubtaskFromId(subtask5.getId());
        assertNotNull(sub_finde, "Задачи не возвращаются");
        //assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask5.getId(), sub_finde.getId(), "В списке под задач не корректная задача");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(10));
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(epic);
        // создаём HTTP-клиент и запрос
        epic.setStatus(TaskStatus.DONE);
        epic.setStartTime(LocalDateTime.now().plusDays(10));
        manager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Epic> tasksFromManager = manager.getEpicsList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TaskStatus.NEW, tasksFromManager.get(epic.getId()).getStatus(), "Задача не обновлилась!");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Epic epic2 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Subtask subtask4 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        Subtask subtask5 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        manager.addEpicSubTask(epic, subtask4);
        manager.addEpicSubTask(epic2, subtask5);
        manager.addEpic(epic);
        manager.addEpic(epic2);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask4.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Subtask> tasksFromManager = manager.getAllSubTasksList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков, не сработало удаление второго эпика.");
        assertNull(tasksFromManager.get(subtask4.getId()), "Ошибка удаление второго эпика., удались не корректная задача");

    }
}