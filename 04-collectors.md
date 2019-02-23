Collectors [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/04-collectors.md)](http://ttr.myapis.xyz/)
------

On [day 2](http://shekhargulati.com/2015/07/26/day-2-lets-learn-about-streams/),
you learned that the Stream API can help you work with collections in a
declarative manner. We looked at `collect`, which is a terminal operation that
collects the result set of a stream pipeline in a `List`. `collect` is a
reduction operation that reduces a stream to a value. The value could be a
Collection, Map, or a value object. You can use `collect` to achieve following:

1. **Reducing stream to a single value:** Result of the stream execution can be
reduced to a single value. Single value could be a `Collection` or numeric value
like int, double, etc or a custom value object.

2. **Group elements in a stream:** Group all the tasks in a stream by TaskType.
This will result in a `Map<TaskType, List<Task>>` with each entry containing a
TaskType and its associated Tasks. You can use any other Collection instead of a
List as well. If you don't need all the tasks associated with a TaskType, you
can alternatively produce a `Map<TaskType, Task>`. One example could be grouping
tasks by type and obtaining the first created task.

3. **Partition elements in a stream:** You can partition a stream into two
groups -- e.g. due and completed tasks.

## Collector in Action

To feel the power of `Collector` let us look at the example where we have to
group tasks by their type. In Java 8, we can achieve grouping by TaskType by
writing code shown below. **Please refer to [day 2](http://shekhargulati.com/2015/07/26/day-2-lets-learn-about-streams/)
blog where we talked about the example domain we will use in this series**

```java
private static Map<TaskType, List<Task>> groupTasksByType(List<Task> tasks) {
    return tasks.stream().collect(Collectors.groupingBy(task -> task.getType()));
}
```

The code shown above uses `groupingBy` `Collector` defined in the `Collectors`
utility class. It creates a Map with key as the `TaskType` and value as the list
containing all the tasks which have same `TaskType`. To achieve the same in Java
7, you would have to write the following.

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

## Collectors: Common reduction operations

The `Collectors` utility class provides a lot of static utility methods for
creating collectors for most common use cases like accumulating elements into a
Collection, grouping and partitioning elements, or summarizing elements
according to various criteria. We will cover the most common `Collector`s in
this blog.

## Reducing to a single value

As discussed above, collectors can be used to collect stream output to a
Collection or produce a single value.

### Collecting data into a List

Let's write our first test case -- given a list of Tasks we want to collect all
the titles into a List.

```java
import static java.util.stream.Collectors.toList;

public class Example2_ReduceValue {
    public List<String> allTitles(List<Task> tasks) {
        return tasks.stream().map(Task::getTitle).collect(toList());
    }
}
```

The `toList` collector uses the List's `add` method to add elements into the
resulting List. `toList` collector uses `ArrayList` as the List implementation.

### Collecting data into a Set

If we want to make sure only unique titles are returned and we don't care about
order then we can use `toSet` collector.

```java
import static java.util.stream.Collectors.toSet;

public Set<String> uniqueTitles(List<Task> tasks) {
    return tasks.stream().map(Task::getTitle).collect(toSet());
}
```

The `toSet` method uses a `HashSet` as the Set implementation to store the
result set.

### Collecting data into a Map

You can convert a stream to a Map by using the `toMap` collector. The `toMap`
collector takes two mapper functions to extract the key and values for the Map.
In the code shown below, `Task::getTitle` is `Function` that takes a task and
produces a key with only title. The **task -> task** is a lambda expression that
just returns itself i.e. task in this case.

```java
private static Map<String, Task> taskMap(List<Task> tasks) {
  return tasks.stream().collect(toMap(Task::getTitle, task -> task));
}
```

We can improve the code shown above by using the `identity` default method in
the `Function` interface to make code cleaner and better convey developer
intent, as shown below.

```java
import static java.util.function.Function.identity;

private static Map<String, Task> taskMap(List<Task> tasks) {
  return tasks.stream().collect(toMap(Task::getTitle, identity()));
}
```

The code to create a Map from the stream will throw an exception when duplicate
keys are present. You will get an error like the one shown below.

```
Exception in thread "main" java.lang.IllegalStateException: Duplicate key Task{title='Read Version Control with Git book', type=READING}
at java.util.stream.Collectors.lambda$throwingMerger$105(Collectors.java:133)
```

You can handle duplicates by using another variant of the `toMap` function which
allows us to specify a merge function. The merge function allows a client to
specify how they want to resolve collisions between values associated with the
same key. In the code shown below, we just used the newer value, but you can
equally write an intelligent algorithm to resolve collisions.

```java
private static Map<String, Task> taskMap_duplicates(List<Task> tasks) {
  return tasks.stream().collect(toMap(Task::getTitle, identity(), (t1, t2) -> t2));
}
```

You can use any other Map implementation by using the third variant of `toMap`
method. This requires you to specify `Map` `Supplier` that will be used to store
the result.

```
public Map<String, Task> collectToMap(List<Task> tasks) {
    return tasks.stream().collect(toMap(Task::getTitle, identity(), (t1, t2) -> t2, LinkedHashMap::new));
}
```

Similar to the `toMap` collector, there is also `toConcurrentMap` collector,
which produces a `ConcurrentMap` instead of a `HashMap`.

### Using other collections

The specific collectors like `toList` and `toSet` do not allow you to specify
the underlying List or Set implementation. You can use the `toCollection`
collector when you want to collect the result to other types of collections, as
shown below.

```
private static LinkedHashSet<Task> collectToLinkedHaskSet(List<Task> tasks) {
  return tasks.stream().collect(toCollection(LinkedHashSet::new));
}
```

### Finding Task with longest title

```java
public Task taskWithLongestTitle(List<Task> tasks) {
    return tasks.stream().collect(collectingAndThen(maxBy((t1, t2) -> t1.getTitle().length() - t2.getTitle().length()), Optional::get));
}
```

### Count total number of tags

```java
public int totalTagCount(List<Task> tasks) {
    return tasks.stream().collect(summingInt(task -> task.getTags().size()));
}
```

### Generate summary of Task titles

```java
public String titleSummary(List<Task> tasks) {
    return tasks.stream().map(Task::getTitle).collect(joining(";"));
}
```

## Grouping Collectors

One of the most common use case of Collector is to group elements. Let's look at
various examples to understand how we can perform grouping.

### Example 1: Grouping tasks by type

Let's look at the example shown below, where we want to group all the tasks
based on their `TaskType`. You can very easily perform this task by using the
`groupingBy` Collector of the `Collectors` utility class. You can make it more
succinct by using method references and static imports.

```java
import static java.util.stream.Collectors.groupingBy;
private static Map<TaskType, List<Task>> groupTasksByType(List<Task> tasks) {
       return tasks.stream().collect(groupingBy(Task::getType));
}
```

It will produce the output shown below.

```
{CODING=[Task{title='Write a mobile application to store my tasks', type=CODING, createdOn=2015-07-03}], WRITING=[Task{title='Write a blog on Java 8 Streams', type=WRITING, createdOn=2015-07-04}], READING=[Task{title='Read Version Control with Git book', type=READING, createdOn=2015-07-01}, Task{title='Read Java 8 Lambdas book', type=READING, createdOn=2015-07-02}, Task{title='Read Domain Driven Design book', type=READING, createdOn=2015-07-05}]}
```

### Example 2: Grouping by tags

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

### Example 3: Group task by tag and count

Combining classifiers and Collectors

```java
private static Map<String, Long> tagsAndCount(List<Task> tasks) {
        return tasks.stream().
        flatMap(task -> task.getTags().stream().map(tag -> new TaskTag(tag, task))).
        collect(groupingBy(TaskTag::getTag, counting()));
    }
```

### Example 4: Grouping by TaskType and createdOn

```java
private static Map<TaskType, Map<LocalDate, List<Task>>> groupTasksByTypeAndCreationDate(List<Task> tasks) {
        return tasks.stream().collect(groupingBy(Task::getType, groupingBy(Task::getCreatedOn)));
    }
```

## Partitioning

There are times when you want to partition a dataset into two datasets based on
a predicate. For example, we can partition tasks into two groups by defining a
partitioning function that partitions tasks into two groups -- one with due date
before today, and one with the others.

```java
private static Map<Boolean, List<Task>> partitionOldAndFutureTasks(List<Task> tasks) {
  return tasks.stream().collect(partitioningBy(task -> task.getDueOn().isAfter(LocalDate.now())));
}
```

## Generating statistics

Another group of collectors that are very helpful are collectors that produce
statistics. These work on the primitive datatypes like `int`, `double`, and
`long`; and can be used to produce statistics like those shown below.

```java
IntSummaryStatistics summaryStatistics = tasks.stream().map(Task::getTitle).collect(summarizingInt(String::length));
System.out.println(summaryStatistics.getAverage()); //32.4
System.out.println(summaryStatistics.getCount()); //5
System.out.println(summaryStatistics.getMax()); //44
System.out.println(summaryStatistics.getMin()); //24
System.out.println(summaryStatistics.getSum()); //162
```

There are other variants as well for other primitive types like
`LongSummaryStatistics` and `DoubleSummaryStatistics`

You can also combine one `IntSummaryStatistics` with another using the `combine`
operation.

```java
firstSummaryStatistics.combine(secondSummaryStatistics);
System.out.println(firstSummaryStatistics)
```

## Joining all titles

```java
private static String allTitles(List<Task> tasks) {
  return tasks.stream().map(Task::getTitle).collect(joining(", "));
}
```

## Writing a custom Collector

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

## Word Count in Java 8

We will end this section by writing the famous word count example in Java 8
using Streams and Collectors.

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

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/04-collectors)](https://github.com/igrigorik/ga-beacon)
