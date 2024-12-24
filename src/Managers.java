public class Managers {

    public static FileBackedTaskManager getDefault() {
        //return new InMemoryTaskManager();
        return new FileBackedTaskManager("d:\\save.txt");
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
