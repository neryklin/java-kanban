import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {


    @Test
    void newSubtast3() {
        Subtask subtask = new Subtask("subtask 1","open the color",TaskStatus.NEW);
        assertEquals(subtask.getName(),"subtask 1", "Ошибка установки наименования subtask");
        assertEquals(subtask.getDescription(),"open the color", "Ошибка установки описания subtask");
        assertEquals(subtask.getStatus(),TaskStatus.NEW, "Ошибка установки описания subtask");
        assertNull(subtask.getEpic(),"Ошибка при создании саб таск, опик должен быть пустым");
    }


    @Test
    void newSubtast4() {
        Epic epic = new Epic("fist epic","epic green button",TaskStatus.NEW);
        Subtask subtask = new Subtask("subtask 1","open the color",TaskStatus.NEW,epic);
        assertEquals(subtask.getName(),"subtask 1", "Ошибка установки наименования subtask");
        assertEquals(subtask.getDescription(),"open the color", "Ошибка установки описания subtask");
        assertEquals(subtask.getStatus(),TaskStatus.NEW, "Ошибка установки описания subtask");
        assertEquals(subtask.getEpic(),epic, "Ошибка установки владельца epic");
        assertNotNull(subtask.getEpic(),"Ошибка при создании саб таск, опик должен быть пустым");
    }

    @Test
    void getEpic() {
        Epic epic = new Epic("fist epic","epic green button",TaskStatus.NEW);
        Subtask subtask = new Subtask("subtask 1","open the color",TaskStatus.NEW,epic);
        Epic epic1 = subtask.getEpic();
        assertEquals(epic1,epic,"Создание subtask с ошибкой ошибка установки epic");
    }

    @Test
    void setEpic() {
        Epic epic = new Epic("fist epic","epic green button",TaskStatus.NEW);
        Subtask subtask = new Subtask("subtask 1","open the color",TaskStatus.NEW,epic);
        Epic epic2 = new Epic("fist epic","epic green button",TaskStatus.NEW);
        subtask.setEpic(epic2);
        Epic epic3 = subtask.getEpic();
        assertEquals(epic2,epic3,"установка subtask с ошибкой ошибка установки epic");
    }

}