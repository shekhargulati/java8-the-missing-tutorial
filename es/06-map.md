Mejoras en Map
---------

Map es una de las estructuras de datos más importantes. En Java 8, se han añadido un montón de mejoras al API Map que harán más fácil el trabajar con él. Veremos todas estas mejoras una a una. Cada característica se mostrará con su correspondiente caso de prueba JUnit.

## Crear un Map a partir de un List

La mayoría de las veces queremos crear un mapa a partir de datos ya existentes. Vamos a suponer que tenemos una lista de tareas, cada tarea tiene un identificador y otros datos asociados como el título, la descripción, etc.

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

## Usar una implementación diferente de Map

La implementación por defecto usada por `Collectors.toMap` es `HashMap`. Puedes especificar tu propia implementación de Map suministrando un proveedor.

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

## Manejando duplicados

Una cosa que pasamos por alto en el último ejemplo es que pasaría si hubiese duplicados. Para controlar los duplicados tenemos un argumento.

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

El test fallará.

```
java.lang.IllegalStateException: Duplicate key Task{title='Write blog on Java 8 Map improvements', type=BLOGGING}
```

Puedes controlar el error especificando tu función de unión.

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

## Crear un Map a partir de tuplas

```java
public static <T, U> Map<T, U> createMap(SimpleEntry<T, U>... entries) {
    return Stream.of(entries).collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
}
```

[![Analytics](https://ga-beacon.appspot.com/UA-74043032-1/malobato/java8-the-missing-tutorial/06-map)](https://github.com/igrigorik/ga-beacon)
