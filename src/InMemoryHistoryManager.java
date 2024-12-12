import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    List<Task> historyVeiwList = new ArrayList<>();

    public void add(Task task){
        if(historyVeiwList.size()==10) {
            historyVeiwList.remove(0);
        }
        historyVeiwList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyVeiwList;
    }
}
