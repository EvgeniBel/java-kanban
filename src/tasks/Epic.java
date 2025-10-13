package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private LocalDateTime endTime;
    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description, StatusTask status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, StatusTask status) {
        super(id, name, description, status);
    }


    public Epic(String name, String description, StatusTask status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        calculateEndTime();
    }

    public Epic(int id, String name, String description, StatusTask status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        calculateEndTime();
    }

    private void calculateEndTime() {
        if (this.startTime != null && this.duration != null) {
            this.endTime = this.startTime.plus(this.duration);
        } else {
            this.endTime = null;
        }
    }

    public void addSubtaskId(int id) {
        subtaskId.add(id);
    }

    public ArrayList<Integer> getSubtaskId() {
        return new ArrayList<>(subtaskId);
    }

    public void removeSubtaskId(int id) {
        subtaskId.remove(Integer.valueOf(id));
    }

    public void clearSubtaskIds() {
        subtaskId.clear();
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.id, this.name, this.description, this.status, this.duration, this.startTime);
        copy.subtaskId = new ArrayList<>(this.subtaskId);
        copy.endTime = this.endTime;
        return copy;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        Epic epic = (Epic) obj;
        return id == epic.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
        calculateEndTime();
    }

    @Override
    public void setDuration(Duration duration) {
        super.setDuration(duration);
        calculateEndTime();
    }
    public void setSubtaskIds(ArrayList<Integer> subtaskIds){
        if (subtaskIds != null) {
            this.subtaskId = new ArrayList<>(subtaskIds);
        } else {
            this.subtaskId = new ArrayList<>();
        }
    }
}
