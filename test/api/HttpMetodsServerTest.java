package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Epic;
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
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpMetodsServerTest {
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
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(1));
        Task task2 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Task task3 = new Task("fist task", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(5));
        manager.addTask(task);
        manager.addTask(task3);
        manager.addTask(task2);
        manager.getTaskFromId(task.getId());
        manager.getTaskFromId(task2.getId());
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Gson gson = HttpTaskServer.getBaseGson();
        List<Task> task_Json = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        // проверяем, что создалась одна задача с корректным именем
        List<Task> task_history = (manager.getHistoryManager().getHistory());
        assertNotNull(task_Json, "История не возвращаются");
        assertEquals(task_history.size(), task_Json.size(), "Список истории отличается от фактического по колву");
    }

    @Test
    public void testGetPrioritizws() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("task1", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(10));
        Task task2 = new Task("task2", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(3));
        Task task3 = new Task("task3", "paint green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(5));
        Epic epic = new Epic("epic4", "epic green button", TaskStatus.NEW, Duration.of(30, ChronoUnit.HOURS), LocalDateTime.now().plusDays(7));
        manager.addTask(task);
        manager.addTask(task3);
        manager.addTask(task2);
        manager.addEpic(epic);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        Gson gson = HttpTaskServer.getBaseGson();
        Set<Task> task_Json = gson.fromJson(response.body(), new TaskSetTypeToken().getType());
        // проверяем, что создалась одна задача с корректным именем
        Set<Task> task_history = (manager.getPrioritizedTasks());
        assertNotNull(task_Json, "Список приоритетов не возвращается");
        assertEquals(task_history.size(), task_Json.size(), "Список приоритетов отличается от фактического по колву");
        assertEquals(task_Json.iterator().next().getName(), "task2", "первый в списке приоритетов не корректный таск");


    }

    class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    class TaskSetTypeToken extends TypeToken<Set<Task>> {
    }

}