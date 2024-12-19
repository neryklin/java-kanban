import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    boolean isEmpty();

    int size();

    void remove(int id);

    void removeAll();

    void add(Task task);

    List<Task> getHistory();

}
