import java.util.ArrayList;
import java.util.HashMap;

public class Managers {

    public static InMemoryTaskManager getDefault(){
      return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager  getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
