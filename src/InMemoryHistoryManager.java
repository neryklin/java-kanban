import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    ArrayList<Task> historyVeiwList = new ArrayList<>();

    public void add(Task task){
        if(historyVeiwList.size()==10) {
            historyVeiwList.remove(0);
        }
        historyVeiwList.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyVeiwList;
    }
}
