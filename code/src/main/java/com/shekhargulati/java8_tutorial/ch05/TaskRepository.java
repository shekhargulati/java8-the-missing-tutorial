package com.shekhargulati.java8_tutorial.ch05;



import com.shekhargulati.java8_tutorial.ch05.domain.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TaskRepository {

    private final Map<String, Task> db = new HashMap<>();

    public void loadData() {
        db.put("1", new Task("1", "hello java 1"));
        db.put("2", new Task("2", "hello java 2"));
        db.put("3", new Task("3", "hello java 3"));
        db.put("4", new Task("4", "hello java 4"));
        db.put("5", new Task("5", "hello java 5"));
    }

    public Task find(String id) {
        return Optional.ofNullable(db.get(id))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Optional<String> taskAssignedTo(String id) {
        return Optional.ofNullable(find(id))
                .flatMap(task -> task.getAssignedTo())
                .map(user -> user.getUsername());
    }
}
