package com.shekhargulati.java8_tutorial.ch06;

import com.shekhargulati.java8_tutorial.domain.Task;
import com.shekhargulati.java8_tutorial.domain.TaskType;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

public class MapExampleTest {

    @Test
    public void shouldCreateMapFromTaskList() throws Exception {
        Task t1 = new Task("Write blog on Java 8 Map improvements", TaskType.BLOGGING);
        Task t2 = new Task("Write factorial program in Java 8", TaskType.CODING);
        List<Task> tasks = Arrays.asList(t1, t2);

        Map<String, Task> taskIdToTaskMap = tasks.stream().collect(toMap(Task::getId, identity()));

        assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
        assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
    }

    @Test
    public void shouldCreateLinkedMapFromTaskList() throws Exception {
        Task t1 = new Task("Write blog on Java 8 Map improvements", TaskType.BLOGGING);
        Task t2 = new Task("Write factorial program in Java 8", TaskType.CODING);
        List<Task> tasks = Arrays.asList(t1, t2);

        Map<String, Task> taskIdToTaskMap = tasks.stream().collect(toMap(Task::getId, identity(), (k1, k2) -> k1, LinkedHashMap::new));

        assertThat(taskIdToTaskMap, instanceOf(LinkedHashMap.class));
        assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
        assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
    }

    @Test
    public void shouldHandleTaskListWithDuplicates() throws Exception {
        Task t1 = new Task("1", "Write blog on Java 8 Map improvements", TaskType.BLOGGING);
        Task t2 = new Task("1", "Write factorial program in Java 8", TaskType.CODING);
        List<Task> tasks = Arrays.asList(t1, t2);
        Map<String, Task> taskIdToTaskMap = tasks.stream().collect(toMap(Task::getId, identity(), (k1, k2) -> k2));
        assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
    }
}