import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void load() {
        FileBackedTaskManager taskManager = Managers.getDefault();
        String pathTo = taskManager.pathTo;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1,class Task,fist task,NEW,paint green button,null,null,null\n");
        stringBuilder.append("2,class Epic,fist epic,NEW,epic green button,null,null,null\n");
        stringBuilder.append("6,class Epic,second  epic,NEW,epic red button,null,null,null\n");
        try (FileWriter fileWriter = new FileWriter(pathTo); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
        taskManager.load();
        assertEquals(taskManager.getTasksList().size(), 1, "Не все задачи Таск загрузились из файла в TaskList");
        assertEquals(taskManager.getEpicsList().size(), 2, "Не все задачи Эпик загрузились из файла в TaskList");
    }

    @Test
    public void testException() {
        assertThrows(ManagerSaveException.class, () -> {
            try (FileReader fileReader = new FileReader("xx:\\pathTo"); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }});
    }

    @Test
    void save() {
        FileBackedTaskManager taskManager = Managers.getDefault();
        Task task = new Task("fist task", "paint green button", TaskStatus.NEW);
        taskManager.addTask(task);
        Epic epic = new Epic("fist epic", "epic green button", TaskStatus.NEW);
        Subtask subtask = new Subtask("subtask 1", "open the color", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("subtask 2", "get brush", TaskStatus.NEW, epic);
        Subtask subtask3 = new Subtask("subtask 3", "paint the button", TaskStatus.NEW, epic);
        Epic epic2 = new Epic("second  epic", "epic red button", TaskStatus.NEW);
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.save();
        assertTrue(Files.exists(Path.of(taskManager.pathTo)), "Не произошло сохранения в файл");
    }
}