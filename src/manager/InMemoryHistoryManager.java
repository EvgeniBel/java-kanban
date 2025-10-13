package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public String toString() {
            String prevId = (prev != null && prev.task != null) ? String.valueOf(prev.task.getId()) : "null";
            String nextId = (next != null && next.task != null) ? String.valueOf(next.task.getId()) : "null";
            String taskId = (task != null) ? String.valueOf(task.getId()) : "null";

            return "Node{ task=" + taskId + ", prev=" + prevId + ", next=" + nextId + "}";
        }
    }

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node value = first;
        while (value != null) {
            tasks.add(value.task);
            value = value.next;
        }
        return tasks;
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task, last, null);
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
        }
        if (next == null) {
            last = null;
        } else {
            next.prev = prev;
            node.next = null;
        }
        nodeMap.remove(node.task.getId());
        node.task = null;
    }

    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        removeNode(nodeMap.get(id));
        linkLast(task);
        nodeMap.put(id, last);
    }

}
