import java.util.ArrayList;
import java.util.LinkedList;

public class HistoryLinkedList<E> {
    public Node<E> first;
    public Node<E> last;
    private int size = 0;

    public void add(E element) {
        final Node<E> oldTail = last;
        final Node<E> newNode = new Node<>(oldTail, element, null);
        last = newNode;
        if (oldTail == null)
            first = newNode;
        else
            oldTail.next = newNode;
        size++;
    }

    public void clear() {
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return  size==0 ? true : false;
    }

    public ArrayList<Task> getTasks(){
        ArrayList<Task> listHistoryTask = new ArrayList<>();
        if (size==0) {
            return listHistoryTask;
        }
        listHistoryTask.add((Task) first.item);
        Node<E> element = first.next;
        while (element!=null) {
            listHistoryTask.add((Task) element.item);
            element = element.next;
        }
        return listHistoryTask;
    }

    public void remove(Node<E> e) {
        final Node<E> next = e.next;
        final Node<E> prev = e.prev;
        if (prev==null) {
            first=next;
        } else{
            prev.next = next;
            e.prev=null;
        }
        if (next==null) {
            last = prev;
        }else{
            next.prev = prev;
            e.next=null;
        }
        e.item = null;
        size--;
    }
}
