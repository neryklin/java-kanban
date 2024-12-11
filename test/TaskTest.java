import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void newTask() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        assertEquals(task.getName(),"fist task", "Ошибка установки наименования subtask");
        assertEquals(task.getDescription(),"paint green button", "Ошибка установки описания subtask");
        assertEquals(task.getStatus(),TaskStatus.NEW, "Ошибка установки описания subtask");
    }

    @Test
    void checkIdIncrement() {
        //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        //не понимаю требования equals переопределен и даже с одним ID обьекты будут разные.
        //проверяю как раз что ID генерируется под каждый новый обьект разный +1
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        int id1 = task.getId();
        id1++;
        Task task2 = new Task("fist task","paint green button",TaskStatus.NEW);
        int id2 = task2.getId();
        assertEquals(id1,id2,"счетчик таксков работает не корректно");
    }


    @Test
    void setId() {
        Task task = new Task("fist task","paint green button",TaskStatus.NEW);
        task.setId(148);
        assertEquals(148,task.getId(),"установка ID таксков работает не корректно");
    }
}