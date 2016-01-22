package com.shekhargulati.java8_tutorial.domain;

import java.time.LocalDate;
import java.util.*;

public class Task {

    private final String id;
    private final String title;
    private final String description;
    private final TaskType type;
    private LocalDate createdOn;
    private Set<String> tags = new HashSet<>();

    public Task(final String id, final String title, final TaskType type) {
        this.id = id;
        this.title = title;
        this.description = title;
        this.type = type;
        this.createdOn = LocalDate.now();
    }

    public Task(final String title, final TaskType type) {
        this(title, title, type, LocalDate.now());
    }

    public Task(final String title, final TaskType type, final LocalDate createdOn) {
        this(title, title, type, createdOn);
    }

    public Task(final String title, final String description, final TaskType type, final LocalDate createdOn) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.type = type;
        this.createdOn = createdOn;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public Task addTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) &&
                Objects.equals(type, task.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, type);
    }
}
