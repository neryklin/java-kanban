import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    final String pathTo;

    public FileBackedTaskManager(String pathToSave) {
        this.pathTo = pathToSave;
    }

    public void load() {
        int maxIndexTast = 0;
        try (FileReader fileReader = new FileReader(pathTo); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String[] str = new String[6];
            while (bufferedReader.ready()) {
                str = bufferedReader.readLine().split(",");
                if (str[1].equals("class Task")) {
                    Task task = new Task(Integer.parseInt(str[0]), str[2], TaskStatus.valueOf(str[3]), str[4]);
                    tasks.put(task.getId(), task);
                    maxIndexTast = Integer.max(maxIndexTast, task.getId());
                } else if (str[1].equals("class Epic")) {
                    Epic epic = new Epic(Integer.parseInt(str[0]), str[2], TaskStatus.valueOf(str[3]), str[4]);
                    epics.put(epic.getId(), epic);
                    maxIndexTast = Integer.max(maxIndexTast, epic.getId());
                } else if (str[1].equals("class Subtask")) {
                    Subtask subtask = new Subtask(Integer.parseInt(str[0]), str[2], TaskStatus.valueOf(str[3]), str[4], epics.get(Integer.parseInt(str[5])));
                    maxIndexTast = Integer.max(maxIndexTast, subtask.getId());
                }

            }

            Task.setCountId(++maxIndexTast);
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }


    public void save() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer i : tasks.keySet()) {
            stringBuilder.append(tasks.get(i).prepareToSave() + "\n");
        }
        for (Integer i : epics.keySet()) {
            stringBuilder.append(epics.get(i).prepareToSave() + "\n");
            var sublist = epics.get(i).subTaskList;
            for (Integer j : sublist.keySet()) {
                stringBuilder.append(sublist.get(j).prepareToSave() + "\n");
            }
        }

        try (FileWriter fileWriter = new FileWriter(pathTo); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
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
    public void removeTaskFromId(int id) {
        super.removeTaskFromId(id);
        save();
    }
}
