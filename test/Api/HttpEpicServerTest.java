package Api;

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

public class HttpEpicServerTest {
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
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(10));
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(epic);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Epic> epicsFromManager = manager.getEpicsList();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("fist epic", epicsFromManager.get(epic.getId()).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicList() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Epic epic2 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Epic epic3 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(5));
        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Epic> epicsFromManager = manager.getEpicsList();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(3, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicId() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Epic epic2 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Epic epic3 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(5));
        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addEpic(epic3);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        Epic epic_finde = manager.getEpicFromId(epic2.getId());
        assertNotNull(epic_finde, "Задачи не возвращаются");
        //assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals(epic2.getId(), epic_finde.getId(), "В списке под задач не корректная задача");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
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
    public void testGetSubtaskFromEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic3 = new Epic("third  epic", "third", TaskStatus.NEW);
        Epic epic4 = new Epic("third  epic", "third", TaskStatus.NEW);
        Subtask subtask4 = new Subtask("subtask 4", "TEST 2", TaskStatus.NEW);
        manager.addEpicSubTask(epic3, subtask4);
        manager.addEpic(epic3);
        manager.addEpic(epic4);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic3.getId() + "/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        HashMap<Integer, Subtask> tasksFromManager = manager.getSubTasksListFromEpic(epic3);
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество субтасков по эпику");
        assertEquals(subtask4.getId(), tasksFromManager.get(subtask4.getId()).getId(), "В списке под задач не корректная задача");
    }

    @Test
    public void testCrossingTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().minusDays(10));
        // конвертируем её в JSON
        Gson gson = HttpTaskServer.getBaseGson();
        String taskJson = gson.toJson(epic);
        // создаём HTTP-клиент и запрос
        epic.setId(epic.getId() + 1);
        manager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Epic> tasksFromManager = manager.getEpicsList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач, не сработа проверка на пересечение, задача ошибочно добавлена.");
    }


    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now());
        Epic epic2 = new Epic("fist epic", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(10));
        // создаём HTTP-клиент и запрос
        manager.addEpic(epic);
        manager.addEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что создалась одна задача с корректным именем
        HashMap<Integer, Epic> tasksFromManager = manager.getEpicsList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков, не сработало удаление второго эпика.");
        assertNull(tasksFromManager.get(epic2.getId()), "Ошибка удаление второго эпика., удались не корректная задача");

    }
}