Collectors
------

En el [capítulo 2](https://github.com/malobato/java8-the-missing-tutorial/blob/master/03-streams.md), aprendiste que el API Stream puede ayudarte a trabajar con colecciones de manera declarativa. Observamos el método `collect()`, que es una operación terminal que acumula el conjunto de resultados de una tubería de flujo en un `List`; es una operación de reducción que reduce un flujo a un valor. El valor podría ser un `Collection`, un `Map` o un objeto valor. Puedes usar `collect()` para obtener lo siguiente:

1. **Reducir un flujo a un simple valor:** El resultado de la ejecución del flujo se puede reducir a un simple valor. Un valor simple podría ser una `Collection` o un valor numérico como un *int*, *double*, etc o un objeto de valor personalizado.

2. **Agrupar elementos en un flujo:** Agrupar todas las tareas de un flujo por tipo de tarea. El resultado será un `Map<TaskType, List<Task>>` con cada entrada conteniendo el tipo de tarea y sus tareas asociadas. También puedes usar cualquier otra colección en vez de un `List`. Si no necesitas todas las tareas asociadas con un tipo de tarea, también puedes producir `Map<TaskType, Task>`. Un ejemplo podría ser agrupar las tareas por tipo y obtener la primera tarea creada.

3. **Dividir los elementos de un flujo:** Puedes dividir un flujo en dos grupos: tareas esperadas y completadas.

## Collector en Acción

Para demostrar el poder de `Collector` vamos a observar el ejemplo donde tenemos que agrupar tareas por su tipo. En Java 8, podemos conseguir agrupar por tipo de tarea con el siguiente código. **(Revisar el [capítulo 2](https://github.com/malobato/java8-the-missing-tutorial/blob/master/02-lambdas.md) donde hablamos sobre el ejemplo que seguimos en esta serie).**

```java
private static Map<TaskType, List<Task>> groupTasksByType(List<Task> tasks) {
    return tasks.stream().collect(Collectors.groupingBy(task -> task.getType()));
}
```

El código siguiente usa el método `groupingBy()` de `Collector` definido en la clase de utilidad `Collectors`. Crea un mapa con clave `TaskType` y valor una lista que contiene todas las tareas con el mismo `TaskType`. Para conseguir lo mismo en Java 7 habría que escribir el siguiente código:

```java
public static void main(String[] args) {
    List<Task> tasks = getTasks();
    Map<TaskType, List<Task>> allTasksByType = new HashMap<>();
    for (Task task : tasks) {
        List<Task> existingTasksByType = allTasksByType.get(task.getType());
        if (existingTasksByType == null) {
            List<Task> tasksByType = new ArrayList<>();
            tasksByType.add(task);
            allTasksByType.put(task.getType(), tasksByType);
        } else {
            existingTasksByType.add(task);
        }
    }
    for (Map.Entry<TaskType, List<Task>> entry : allTasksByType.entrySet()) {
        System.out.println(String.format("%s =>> %s", entry.getKey(), entry.getValue()));
    }
}
```

## Collectors: Operaciones de reducción comunes

La clase utilidad `Collectors` proporciona un montón de métodos estáticos de utilidad para crear **acumuladores** para la mayoría de casos de uso comunes como acumular elementos en una colección, agrupar y particionar elementos, y resumir elementos de acuerdo a varios criterios. Cubriremos la mayoría de casos comunes de `Collector` en este blog.


## Reducir a un simple valor

Como ya vimos, los **acumuladores** se pueden usar para recoger la salida de un flujo en una colección o generar un valor simple.

### Recoger datos en una lista

Vamos a escribir nuestro primer caso de prueba: dada una lista de tareas queremos recoger sus títulos en una lista.

```java
import static java.util.stream.Collectors.toList;

public class Example2_ReduceValue {
    public List<String> allTitles(List<Task> tasks) {
        return tasks.stream().map(Task::getTitle).collect(toList());
    }
}
```

El acumulador `toList()` usa el método `add()` de `List` para añadir elementos dentro de la lista resultante. Usa un `ArrayList` como implementación de la lista.

### Recoger datos en un conjunto

Si queremos estar seguros de que sólo recogemos títulos únicos y no nos preocupa el orden, podemos usar el acumulador `toSet()`.

```java
import static java.util.stream.Collectors.toSet;

public Set<String> uniqueTitles(List<Task> tasks) {
    return tasks.stream().map(Task::getTitle).collect(toSet());
}
```

El método `toSet()` usa un `HashSet` como implementación del conjunto para guardar el resultado.

### Recoger datos en un mapa

Puedes convertir un flujo en un mapa usando el acumulador `toMap()`. El acumulador `toMap()` toma como parámetros dos funciones de mapeado para extraer la clave y los valores del mapa. En el código siguiente `Task::getTitle` es una función que toma una tarea y produce una clave con un único título. **task -> task** es una expresión lambda que se devuelve a si misma. P. ej. la tarea en este caso.

```java
private static Map<String, Task> taskMap(List<Task> tasks) {
  return tasks.stream().collect(toMap(Task::getTitle, task -> task));
}
```

Podemos mejorar el código anterior usando el método por defecto `identity()` en la interfaz `Function` para generar código más limpio y que transmita mucho mejor la intención del programador.

```java
import static java.util.function.Function.identity;

private static Map<String, Task> taskMap(List<Task> tasks) {
  return tasks.stream().collect(toMap(Task::getTitle, identity()));
}
```

El código para crear un mapa a partir de un flujo lanzará una excepción si existen claves duplicadas. Obtendrás un error como el siguiente.

```
Exception in thread "main" java.lang.IllegalStateException: Duplicate key Task{title='Read Version Control with Git book', type=READING}
at java.util.stream.Collectors.lambda$throwingMerger$105(Collectors.java:133)
```

Puedes controlar los duplicados usando otra variante de la función `toMap()` que nos permite especificar una función de unión. La función de unión permite al cliente especificar como quiere resolver la colisión entre valores asociados a la misma clave. En el código siguiente, sólo nos quedamos con el último valor, pero puedes escribir un algoritmo para resolver la colisión.

```java
private static Map<String, Task> taskMap_duplicates(List<Task> tasks) {
  return tasks.stream().collect(toMap(Task::getTitle, identity(), (t1, t2) -> t2));
}
```

Puedes usar cualquier otra implementación de mapa usando la tercera variante del método `toMap()`. Esto require que especifiques el `Map` `Supplier` que se usará para guardar el resultado.

```
public Map<String, Task> collectToMap(List<Task> tasks) {
    return tasks.stream().collect(toMap(Task::getTitle, identity(), (t1, t2) -> t2, LinkedHashMap::new));
}
```

Similar al acumulador `toMap()` también está el acumulador `toConcurrentMap()` que produce un `ConcurrentMap` en vez de un `HashMap`.

### Usando otras colecciones

Los acumuladores específicos como `toList()` y `toSet()` no te permiten especificar la implementación de la lista o del conjunto. Puedes usar el acumulador `toCollection()` cuando quieras recoger el resultado en otros tipos de colecciones como se muestra a continuación.

```
private static LinkedHashSet<Task> collectToLinkedHaskSet(List<Task> tasks) {
  return tasks.stream().collect(toCollection(LinkedHashSet::new));
}
```

### Encontrando la tarea con el título más largo

```java
public Task taskWithLongestTitle(List<Task> tasks) {
    return tasks.stream().collect(collectingAndThen(maxBy((t1, t2) -> t1.getTitle().length() - t2.getTitle().length()), Optional::get));
}
```

### Contando el número total de etiquetas

```java
public int totalTagCount(List<Task> tasks) {
    return tasks.stream().collect(summingInt(task -> task.getTags().size()));
}
```

### Generando un resumen de títulos de tarea

```java
public String titleSummary(List<Task> tasks) {
    return tasks.stream().map(Task::getTitle).collect(joining(";"));
}
```

## Agrupando acumuladores

Uno de los casos de uso más comunes es agrupar elementos. Vamos a ver varios ejemplos para comprender como podemos realizar agrupaciones.

### Ejemplo 1: Agrupando tareas por tipo

Vamos a ver el ejemplo mostrado abajo donde queremos agrupar todas las tareas basándonos en su `TaskType`. Puedes realizar esta tarea de una forma muy sencilla usando el acumulador `groupingBy()` de la clase de utilidad `Collectors`. Puedes acortarlo más usando referencias a método e importaciones estáticas.

```java
import static java.util.stream.Collectors.groupingBy;

private static Map<TaskType, List<Task>> groupTasksByType(List<Task> tasks) {
       return tasks.stream().collect(groupingBy(Task::getType));
}
```

Producirá la siguiente salida.

```
{CODING=[Task{title='Write a mobile application to store my tasks', type=CODING, createdOn=2015-07-03}], WRITING=[Task{title='Write a blog on Java 8 Streams', type=WRITING, createdOn=2015-07-04}], READING=[Task{title='Read Version Control with Git book', type=READING, createdOn=2015-07-01}, Task{title='Read Java 8 Lambdas book', type=READING, createdOn=2015-07-02}, Task{title='Read Domain Driven Design book', type=READING, createdOn=2015-07-05}]}
```

### Ejemplo 2: Agrupando por etiquetas

```java
private static Map<String, List<Task>> groupingByTag(List<Task> tasks) {
        return tasks.stream().
                flatMap(task -> task.getTags().stream().map(tag -> new TaskTag(tag, task))).
                collect(groupingBy(TaskTag::getTag, mapping(TaskTag::getTask,toList())));
}

    private static class TaskTag {
        final String tag;
        final Task task;

        public TaskTag(String tag, Task task) {
            this.tag = tag;
            this.task = task;
        }

        public String getTag() {
            return tag;
        }

        public Task getTask() {
            return task;
        }
    }
```

### Ejemplo 3: Agrupando tareas por etiqueta y número

Combinando clasificadores y acumuladores.

```java
private static Map<String, Long> tagsAndCount(List<Task> tasks) {
        return tasks.stream().
        flatMap(task -> task.getTags().stream().map(tag -> new TaskTag(tag, task))).
        collect(groupingBy(TaskTag::getTag, counting()));
    }
```

### Ejemplo 4: Agrupando por tipo de tarea y fecha de creación

```java
private static Map<TaskType, Map<LocalDate, List<Task>>> groupTasksByTypeAndCreationDate(List<Task> tasks) {
        return tasks.stream().collect(groupingBy(Task::getType, groupingBy(Task::getCreatedOn)));
    }
```

## Dividiendo en partes

Hay veces en las que quieres partir un conjunto de datos en dos basándote en un predicado. Por ejemplo, podemos partir tareas en dos grupos definiendo una función de reparto que parta las tareas en dos grupos: uno con fecha de vencimiento anterior a hoy y otro con fecha de vencimiento posterior a hoy.

```java
private static Map<Boolean, List<Task>> partitionOldAndFutureTasks(List<Task> tasks) {
  return tasks.stream().collect(partitioningBy(task -> task.getDueOn().isAfter(LocalDate.now())));
}
```

## Generando estadísticas

Otro grupo de acumuladores que son muy útiles son los acumuladores que producen estadísticas. Estos trabajan con tipos de datos elementales como int, double, long y pueden usarse para generar estadísticas como la que se muestra a continuación.

```java
IntSummaryStatistics summaryStatistics = tasks.stream().map(Task::getTitle).collect(summarizingInt(String::length));
System.out.println(summaryStatistics.getAverage()); //32.4
System.out.println(summaryStatistics.getCount()); //5
System.out.println(summaryStatistics.getMax()); //44
System.out.println(summaryStatistics.getMin()); //24
System.out.println(summaryStatistics.getSum()); //162
```

También existen otras variantes para otros tipos elementales como `LongSummaryStatistics` y `DoubleSummaryStatistics`.

También puedes combinar un `IntSummaryStatistics` con otro usando la operación `combine`.

```java
firstSummaryStatistics.combine(secondSummaryStatistics);
System.out.println(firstSummaryStatistics)
```

## Uniendo todos los títulos

```java
private static String allTitles(List<Task> tasks) {
  return tasks.stream().map(Task::getTitle).collect(joining(", "));
}
```

## Escribiendo un acumulador personalizado

```java
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MultisetCollector<T> implements Collector<T, Multiset<T>, Multiset<T>> {

    @Override
    public Supplier<Multiset<T>> supplier() {
        return HashMultiset::create;
    }

    @Override
    public BiConsumer<Multiset<T>, T> accumulator() {
        return (set, e) -> set.add(e, 1);
    }

    @Override
    public BinaryOperator<Multiset<T>> combiner() {
        return (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        };
    }

    @Override
    public Function<Multiset<T>, Multiset<T>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
}
```

```java
import com.google.common.collect.Multiset;

import java.util.Arrays;
import java.util.List;

public class MultisetCollectorExample {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("shekhar", "rahul", "shekhar");
        Multiset<String> set = names.stream().collect(new MultisetCollector<>());

        set.forEach(str -> System.out.println(str + ":" + set.count(str)));

    }
}
```

## Contador de palabras en Java 8

Terminaremos esta sección escribiendo en Java 8 el famoso ejemplo de contar palabras usando flujos y acumuladores.

```java
public static void wordCount(Path path) throws IOException {
    Map<String, Long> wordCount = Files.lines(path)
            .parallel()
            .flatMap(line -> Arrays.stream(line.trim().split("\\s")))
            .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim())
            .filter(word -> word.length() > 0)
            .map(word -> new SimpleEntry<>(word, 1))
            .collect(groupingBy(SimpleEntry::getKey, counting()));
    wordCount.forEach((k, v) -> System.out.println(String.format("%s ==>> %d", k, v)));
}
```

[![Analytics](https://ga-beacon.appspot.com/UA-74043032-1/malobato/java8-the-missing-tutorial/04-collectors)](https://github.com/igrigorik/ga-beacon)
