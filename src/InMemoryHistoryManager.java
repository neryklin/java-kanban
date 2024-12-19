import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    HistoryLinkedList<Task> historyVeiwList = new HistoryLinkedList<Task>();
    Map<Integer, Node> mapSearchNodeFromList = new HashMap<>();


    @Override
    public boolean isEmpty() {
        return historyVeiwList.isEmpty();
    }

    @Override
    public int size() {
        return historyVeiwList.size();
    }

    @Override
    public void remove(int id) {
        if (mapSearchNodeFromList.containsKey(id)) {
            removeNode(mapSearchNodeFromList.get(id));
            mapSearchNodeFromList.remove(id);
        }

    }

    @Override
    public void removeAll() {
        mapSearchNodeFromList.clear();
        historyVeiwList.clear();
    }

    public void removeNode(Node<Task> node) {
        historyVeiwList.remove(node);
    }

    public void add(Task task) {
        remove(task.getId());
        historyVeiwList.add(task);
        mapSearchNodeFromList.put(task.getId(), historyVeiwList.last);
    }

    @Override
    public List<Task> getHistory() {
        return historyVeiwList.getTasks();
    }


}
