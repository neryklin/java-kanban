import java.io.*;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager{
final String pathToSave;

    public FileBackedTaskManager(String pathToSave) {
        this.pathToSave = pathToSave;
    }

    public  void load(){
        try (
                FileInputStream fileInputStream = new FileInputStream(pathToSave);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
             this.tasks = (HashMap) objectInputStream.readObject();
        } catch (IOException e) {
//            throw new ManagerSaveException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try (
        FileOutputStream outputStream = new FileOutputStream(pathToSave);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {

            objectOutputStream.writeObject(this.tasks);
        } catch (IOException e) {
//            throw new ManagerSaveException(e.getMessage());
        }
    }

    public class ManagerSaveException extends RuntimeException{

        public ManagerSaveException() {
        }

        public ManagerSaveException(String message) {
            super(message);
        }
    }
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void addEpicSubTask(Epic epic, Subtask subtask) {
        super.addEpicSubTask(epic, subtask);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public Task removeTaskFromId(int id) {

        super.removeTaskFromId(id);
        save();
        return super.removeTaskFromId(id);
      //  save();
    }
}
