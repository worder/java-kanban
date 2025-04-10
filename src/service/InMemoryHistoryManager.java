package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<T> {
        public Node<T> prev;
        public Node<T> next;
        public T data;

        public Node(T data) {
            this.data = data;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private final Map<Integer, Node<Task>> nodesById = new HashMap<>();

    public List<Task> getHistory() {
        return getTasks();
    }

    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(nodesById.get(task.getId()));
        linkLast(task);
        size++;
    }

    public void remove(int id) {
        Node<Task> node = nodesById.get(id);
        if (node == null) {
            return;
        }
        removeNode(node);
        nodesById.remove(id);
    }

    public void remove(Iterable<Integer> ids) {
        for (int id : ids) {
            remove(id);
        }
    }

    private void linkLast(Task task) {
        Node<Task> node = new Node<>(task);
        if (size == 0) {
            head = node;
        } else {
            node.prev = tail;
            tail.next = node;
        }
        tail = node;
        nodesById.put(task.getId(), node);
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (size == 1) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.next;
            head.prev = null;
        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size--;
    }

    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> nextNode = head;
        while (nextNode != null) {
            tasksList.add(nextNode.data);
            nextNode = nextNode.next;
        }
        return tasksList;
    }
}