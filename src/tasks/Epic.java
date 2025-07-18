package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subtaskId= new ArrayList<>();

    public Epic(String name, String description, StatusTask status){
        super(name, description, status);
    }
    public Epic(int id, String name, String description, StatusTask status){
        super(id, name, description, status);
    }

    public void addSubtaskId(int id){
        subtaskId.add(id);
    }
    public ArrayList<Integer> getSubtaskId(){
        return subtaskId;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        Epic epic = (Epic) obj;
        return id == epic.id;
    }
}
