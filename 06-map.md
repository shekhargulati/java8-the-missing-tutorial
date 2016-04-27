Map Improvements
---------

Map is one the most important data structure. In Java 8, a lot of goodies has
been added to the Map API that will make it easy to work with them. We will look
at all the enhancements made to them one by one. Every feature is shown along
with its JUnit test case.

## Create Map from List

Most of the times we want to create a map from existing data. Let's suppose we
have a list of tasks, every task has an id and other associated data like title,
description, etc.

```java
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Test
public void shouldCreateMapFromTaskList() throws Exception {
    Task t1 = new Task("Write blog on Java 8 Map improvements", TaskType.BLOGGING);
    Task t2 = new Task("Write factorial program in Java 8", TaskType.CODING);
    List<Task> tasks = Arrays.asList(t1, t2);

    Map<String, Task> taskIdToTaskMap = tasks.stream().collect(toMap(Task::getId, identity()));

    assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
    assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
}
```

## Using a different Map implementation

The default implementation used by `Collectors.toMap` is `HashMap`. You can also
specify your own Map implementation by providing a supplier.

```java
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
```

## Handling duplicates

One thing that we glossed over in the last example was what should happen if
there are duplicates. To handle duplicates there is a argument

```java
@Test
public void shouldHandleTaskListWithDuplicates() throws Exception {
    Task t1 = new Task("1", "Write blog on Java 8 Map improvements", TaskType.BLOGGING);
    Task t2 = new Task("1", "Write factorial program in Java 8", TaskType.CODING);
    List<Task> tasks = Arrays.asList(t1, t2);

    Map<String, Task> taskIdToTaskMap = tasks.stream().collect(toMap(Task::getId, identity()));

    assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t1)));
    assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
}
```

This test will fail

```
java.lang.IllegalStateException: Duplicate key Task{title='Write blog on Java 8 Map improvements', type=BLOGGING}
```

You can handle the error by specifying your merge function.

```java
@Test
public void shouldHandleTaskListWithDuplicates() throws Exception {
    Task t1 = new Task("1", "Write blog on Java 8 Map improvements", TaskType.BLOGGING);
    Task t2 = new Task("1", "Write factorial program in Java 8", TaskType.CODING);
    List<Task> tasks = Arrays.asList(t1, t2);
    Map<String, Task> taskIdToTaskMap = tasks.stream().collect(toMap(Task::getId, identity(), (k1, k2) -> k2));
    assertThat(taskIdToTaskMap, hasEntry(notNullValue(), equalTo(t2)));
}
```

## Create Map from tuples

```java
public static <T, U> Map<T, U> createMap(SimpleEntry<T, U>... entries) {
    return Stream.of(entries).collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
}
```

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/06-map)](https://github.com/igrigorik/ga-beacon)
