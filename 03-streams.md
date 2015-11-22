## Streams

On [day 1](http://shekhargulati.com/2015/07/25/day-1-lets-learn-about-lambdas/), we learnt how lambdas can help us write clean concise code by allowing us to pass behavior without the need to create a class. Lambdas is a very simple language construct that helps developer express their intent on the fly by using functional interfaces. The real power of lambdas can be experienced when an API is designed keeping lambdas in mind i.e. a fluent API that makes use of Functional interfaces(we discussed them on day 1).

One such API that makes heavy use of lambdas is Stream API introduced in JDK 8. Streams provide a higher level abstraction to express computations on Java collections in a declarative way similar to how SQL helps you declaratively query data in the database. Declarative means developers write what they want to do rather than how it should be done. Almost every Java developer has used `Collection` API for storing, accessing, and manipulating data. In this blog, we will discuss why need a new API, difference between Collection and Stream, and how to use Stream API in your applications.

## Why we need a new data processing abstraction?

1. Collection API is too low level: Collection API does not provide higher level constructs to query the data so developers are forced to write a lot of boilerplate code for the most trivial task.

2. Limited language support to process Collections in parallel


### Data processing before Java 8

Look at the code shown below and try to predict what code does.

```java
public class Example1_Java7 {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();

        List<Task> readingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getType() == TaskType.READING) {
                readingTasks.add(task);
            }
        }
        Collections.sort(readingTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getTitle().length() - t2.getTitle().length();
            }
        });
        for (Task readingTask : readingTasks) {
            System.out.println(readingTask.getTitle());
        }
    }
}
```

The code shown above prints all the reading task titles sorted by their title length. All Java developers write this kind of code everyday. To write such a simple program we had to write 15 lines of Java code. The bigger problem with the above mentioned code is not the number of lines a developer has to write but, it misses the developer's intent i.e. filtering reading tasks, sorting by title length, and transforming to List of String.

### Data processing in Java 8

The above mentioned code can be simplified using Java 8 streams as shown below.

```java
public class Example1_Stream {
    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<String> readingTasks = tasks.stream()
                .filter(task -> task.getType() == TaskType.READING)
                .sorted((t1, t2) -> t1.getTitle().length() - t2.getTitle().length())
                .map(Task::getTitle)
                .collect(Collectors.toList());
        readingTasks.forEach(System.out::println);
    }
}
```

The line `tasks.stream().filter(task ->task.getType() == TaskType.READING).sorted((t1, t2) -> t1.getTitle().length() - t2.getTitle().length()).map(Task::getTitle).collect(Collectors.toList())` constructs a stream pipeline composing of multiple stream operations as discussed below.

* **stream()** - You created a stream pipeline by invoking the `stream()` method on the source collection i.e. `tasks` `List<Task>`.

* **filter(Predicate<T>)** - This operation extract elements in the stream matching the condition defined by the predicate. Once you have a stream you can call zero or more intermediate operations on it. The lambda expression `task -> task.getType() == TaskType.READING` defines a predicate to filter all reading tasks. The type of lambda expression is `java.util.function.Predicate<Task>`.

* **sorted(Comparator<T>)**: This operation returns a stream consisting of all the stream elements sorted by the Comparator defined by lambda expression i.e. `(t1, t2) -> t1.getTitle().length() - t2.getTitle().length()` in the example shown above.

* **map(Function<T,R>)**: This operation returns a stream after applying the Function<T,R> on each element of this stream.

* **collect(toList())** - This operation collects result of the operations performed on the Stream to a List.

### Why Java 8 code is better?

In my opinion Java 8 code is better because of following reasons:

1. Java 8 code clearly reflect developer intent
2. Developer expressed what they want to do rather than how they want do it
3. Stream API provides a unified language for data processing
4. No boilerplate code

### What is a Stream?

Stream is a sequence of elements where elements are computed on demand. Streams are lazy by nature and they are only computed when accessed. This allows us to produce infinite streams. In Java before version 8, there was no way to produce infinite elements. With Java 8, you can very easily write a Stream that will produce infinite unique identifiers as shown below.

```
public static void main(String[] args) {
    Stream<String> uuidStream = Stream.generate(() -> UUID.randomUUID().toString());
}
```

The code shown above will create a Stream that can produce infinite UUID's. If you run this program nothing will happen as Streams are lazy and until they are accessed nothing will be computed. If we update the program to the one shown below we will see UUID printing to the console. The program will never terminate.

```java
public static void main(String[] args) {
    Stream<String> uuidStream = Stream.generate(() -> UUID.randomUUID().toString());
    uuidStream.forEach(System.out::println);
}
```

Java 8 allows you to create Stream from a Collection by calling the `stream` method on it. Stream supports data processing operations so that developers can express computations using higher level data processing constructs.

### Collection vs Stream

The table shown below explains the difference between a Collection and a Stream.

![Collection vs Stream](https://whyjava.files.wordpress.com/2015/10/collection_vs_stream.png)

#### External iteration vs internal iteration

The difference between Java 8 Stream API code and Collection API code shown above is who controls the iteration, the iterator or the client that uses the iterator. Users of the Stream API just provide the operations they want to apply, and iterator applies those operations to every element in the underlying Collection. When iterating over the underlying collection is handled by the iterator itself, it is called **internal iteration**. On the other hand, when iteration is handled by the client then it is called **external iteration**. The use of `for-each` construct in the Collection API code is an example of **external iteration**.

Some might argue that in the Collection API code we didn't have to work with the underlying iterator as the `for-each` construct took care of that but, `for-each` is nothing more than syntactic sugar over manual iteration using the iterator API. The `for-each` construct although very simple has few disadvantages -- 1) It is inherently sequential 2) It leads to imperative code 3) It is difficult to parallelize.


#### Lazy evaluation

When we work with Collection API, every operation that we perform is eagerly evaluated. Look at the example code shown below.

```java
private static List<String> findAllReadingTask(List<Task> tasks) {
        List<Task> readingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getType() == TaskType.READING) {
                readingTasks.add(task);
            }
        }

        Collections.sort(readingTasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getCreatedOn().compareTo(o2.getCreatedOn());
            }
        });

        List<String> readingTaskTitles = new ArrayList<>();
        for (Task readingTask : readingTasks) {
            readingTaskTitles.add(readingTask.getTitle());
        }
        return readingTaskTitles;
    }
```
The code shown above finds all the reading tasks sorted by their creation date. If you look at the code closely you will see that this code does three things -- 1) Filter all reading tasks 2) Sort all filtered tasks by creation date 3) Collect all the titles in a list. These three stages are eagerly evaluated and we had to create temporary variables like `readingTasks` to store the intermediate values.

In Java 8, we can write the above mentioned code as shown below.

```java
private static List<String> findAllReadingTask(List<Task> tasks) {
        return tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted((t1, t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())).
                map(task -> task.getTitle()).
                collect(Collectors.toList());
}
```
The code shown above does the same work but, the user is not concerned about the iteration and creating temporary variables for storing intermediate results. Java 8 lazily evaluates the stream pipeline when terminal operation(`collect(toList())`) is called. We will not worry about Stream API methods like `map`, `filter`, `sorted` for now as they will be covered later in the blog. To make sure you understand the lazy evaluation concept, let's look at another example as shown below.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream().map(n -> n / 0).filter(n -> n % 2 == 0);
```
As you can see in the code above, we are dividing by 0 so it will throw `ArithmeticException` when the code is executed. But, when you run the code no exception is thrown. This is because streams are not evaluated until a terminal operation is called on the stream. If you add terminal operation to the stream pipeline, then stream is executed, and exception is thrown.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream().map(n -> n / 0).filter(n -> n % 2 == 0);
stream.collect(toList());
```

You will get stack trace as shown below.

```
Exception in thread "main" java.lang.ArithmeticException: / by zero
	at org._7dayswithx.java8.day2.EagerEvaluationExample.lambda$main$0(EagerEvaluationExample.java:13)
	at org._7dayswithx.java8.day2.EagerEvaluationExample$$Lambda$1/1915318863.apply(Unknown Source)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
	at java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:948)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:512)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:502)
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
```

### Using Stream API

Stream API provides a lot of operations that developers can use to query data from collections. Stream operations fall into either of the two categories -- intermediate operation or terminal operation. **Intermediate operations** are functions that produce another stream from the existing stream like `filter`, `map`, `sorted`, etc. **Terminal operations** are functions that produce a non-stream result from the Stream like `collect(toList())` , `forEach`, `count` etc. Intermediate operations allows you to build the pipeline which gets executed when you call the terminal operation. Below is the list of functions that are part of the Stream API.

<a href="https://whyjava.files.wordpress.com/2015/07/stream-api.png"><img class="aligncenter size-full wp-image-2983" src="https://whyjava.files.wordpress.com/2015/07/stream-api.png" alt="stream-api" height="450" /></a>

### Example domain

Throughout the series we will use Task management domain to explain the concepts. Our example domain has one class called Task -- a task to be performed by user. The class is shown below.

```java
import java.time.LocalDate;
import java.util.*;

public class Task {
    private final String id;
    private final String title;
    private final TaskType type;
    private final LocalDate createdOn;
    private boolean done = false;
    private Set<String> tags = new HashSet<>();
    private LocalDate dueOn;

    // removed constructor, getter, and setter for brevity
}
```
The sample dataset is given below. We will use this list throughout our Stream API examples.

```java
Task task1 = new Task("Read Version Control with Git book", TaskType.READING, LocalDate.of(2015, Month.JULY, 1)).addTag("git").addTag("reading").addTag("books");

Task task2 = new Task("Read Java 8 Lambdas book", TaskType.READING, LocalDate.of(2015, Month.JULY, 2)).addTag("java8").addTag("reading").addTag("books");

Task task3 = new Task("Write a mobile application to store my tasks", TaskType.CODING, LocalDate.of(2015, Month.JULY, 3)).addTag("coding").addTag("mobile");

Task task4 = new Task("Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2015, Month.JULY, 4)).addTag("blogging").addTag("writing").addTag("streams");

Task task5 = new Task("Read Domain Driven Design book", TaskType.READING, LocalDate.of(2015, Month.JULY, 5)).addTag("ddd").addTag("books").addTag("reading");

List<Task> tasks = Arrays.asList(task1, task2, task3, task4, task5);
```

> We will not discuss about Java 8 Date Time API today. For now, just think of as the fluent API to work with dates.

#### Example 1 -- Find all the reading task titles sorted by their creation date

The first example that we will discuss is to find all the reading task titles sorted by creation date. The operations that we need to perform to code this example are:

1. Filter all the tasks that have TaskType as READING.
2. Sort the filtered values tasks by `createdOn` field.
3. Get the value of title for each task.
4. Collect the resulting titles in a List.

The following four operations can be easily translated to the code as shown below.

```java
private static List<String> findAllReadingTitlesSortedByCreationDate(List<Task> tasks) {
        List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted((t1, t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())).
                map(task -> task.getTitle()).
                collect(Collectors.toList());
        return readingTaskTitles;
}
```
In the code shown above, we used following methods of the Stream API:

* **filter**:  Allows you to specify a predicate to exclude some elements from the underlying stream. The predicate **task -> task.getType() == TaskType.READING** selects all the tasks whose TaskType is READING.
* **sorted**: Allows you to specify a Comparator that will sort the stream. In this case, you sorted based on the creation date. The lambda expression **(t1, t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())** provides implementation of the `compare` method of Comparator functional interface.
* **map**: It takes a lambda that implements `Function<? super T, ? extends R>` which transforms one stream to another stream. The lambda expression **task -> task.getTitle()** transforms a task into a title.
* **collect(toList())** It is a terminal operation that collects the resulting reading titles into a List.

We can improve the above Java 8 code by using `comparing` method of `Comparator` interface and method references as shown below.

```java
List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted(Comparator.comparing(Task::getCreatedOn)).
                map(Task::getTitle).
                collect(Collectors.toList());
```

> From Java 8, interfaces can have method implementations in the form of static and default methods. We will cover them later in this series.

In the code shown above, we used a static helper method `comparing` available in the `Comparator` interface which accepts a `Function` that extracts a `Comparable` key, and returns a `Comparator` that compares by that key. The method reference `Task::getCreatedOn` resolves to `Function<Task, LocalDate>`.

Using function composition, we can very easily write code that reverses the sorting order by calling `reversed()` method on Comparator as shown below.

```java
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted(comparing(Task::getCreatedOn).reversed()).
                map(Task::getTitle).
                collect(toList());
```

#### Example 2 -- Finding distinct tasks

Suppose, we have a dataset which contains duplicate tasks. We can very easily remove the duplicates and get only distinct elements by using the `distinct` method on the stream as shown below.

```java
tasks.stream().distinct().collect(toList());
```

The `distinct()` method converts one stream into another without duplicates. It uses the Object's `equals` method for determining the object equality. According to Object's equal method contract, when two objects are equal, they are considered duplicates and will be removed from the resulting stream.

#### Example 3 -- Find top 5 reading tasks sorted by creation date

The `limit` function can be used to limit the result set to a given size. `limit` is a short circuiting operation which means it does not evaluate all the elements to find the result.

```java
List<String> top5 = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted(comparing(Task::getCreatedOn)).
                map(Task::getTitle).
                limit(5).
                collect(toList());
```

You can use `limit` along with `skip` method to create pagination as shown below.

```java
// page starts from 0. So to view a second page `page` will be 1 and n will be 5.
List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted(comparing(Task::getCreatedOn).reversed()).
                map(Task::getTitle).
                skip(page * n).
                limit(n).
                collect(toList());
```

#### Example 4: Count all reading tasks

To get the count of all the reading tasks, we can use `count` method on the stream. This method is a terminal operation.

```java
private static long countAllReadingTasks(List<Task> tasks) {
        return tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                count();
    }
```

#### Example 5: Find all the unique tags from all tasks

To find all the distinct tags we have to perform the following operations:

1. Extract tags for each task.
2. Collect all the tags into one stream.
3. Remove the duplicates.
4. Finally collect the result into a list.

The first and second operations can be performed by using the `flatMap` operation on the `tasks` stream. The `flatMap` operation flattens the streams returned by each invocation of `tasks.getTags().stream()` into one stream. Once we have all the tags in one stream, we just used `distinct` method on it to get all unique tags.

```java
private static List<String> allDistinctTags(List<Task> tasks) {
        return tasks.stream().flatMap(task -> task.getTags().stream()).distinct().collect(toList());
}
```

#### Example 6 -- Check if all reading tasks have tag `books`

Stream API has methods that allows the users to check if elements in the dataset match a given property. These methods are `allMatch`, `anyMatch`, `noneMatch`, `findFirst`, and `findAny`. To check if all reading titles have a tag with name `books` we can write code as shown below.

```java
private static boolean isAllReadingTasksWithTagBooks(List<Task> tasks) {
        return tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                allMatch(task -> task.getTags().contains("books"));
}
```

To check whether any reading task has a `java8` tag, then we can use `anyMatch` operation as shown below.

```java
private static boolean isAnyReadingTasksWithTagJava8(List<Task> tasks) {
        return tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                anyMatch(task -> task.getTags().contains("java8"));
}
```

#### Example 7 -- Creating a summary of all titles

Suppose, you want to create a summary of all the titles then you can use `reduce` operation, which reduces the stream to a value. The `reduce` function takes a lambda which joins elements of the stream.

```java
private static String joinAllTaskTitles(List<Task> tasks) {
  return tasks.stream().
              map(Task::getTitle).
              reduce((first, second) -> first + " *** " + second).
              get();
}
```

Another example of reduce operation is when you have to find the product of squares of all numbers from given a list of numbers.  This is a second variant of reduce which takes two values -- 1) An initial value like 1 in this case 2) A BinaryOperator<T> that combines two elements to produce a new value.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Integer result = numbers.stream().map(number -> number * number).reduce(1, (acc, element) -> acc * element);
```

#### Example 8 - Working with primitive Streams

Apart from the generic stream that works over objects, Java 8 also provides specific streams that work over primitive types like int, long, and double. Let's look at few examples of primitive streams.

To create a range of values, we can use `range` method that creates a stream with value starting from 0 and ending at 9. It excludes 10.

```java
IntStream.range(0, 10).forEach(System.out::println);
```

The `rangeClosed` method allows you to create streams that includes the upper bound as well. So, the below mentioned stream will start at 1 and end at 10.
```java
IntStream.rangeClosed(1, 10).forEach(System.out::println);
```

You can also create infinite streams using iterate method on the primitive streams as shown below.

```java
LongStream infiniteStream = LongStream.iterate(1, el -> el + 1);
```

To filter out all even numbers in an infinite stream, we can write code as shown below.

```java
infiniteStream.filter(el -> el % 2 == 0).forEach(System.out::println);
```

We can limit the resulting stream by using the `limit` operation as shown below.

```java
infiniteStream.filter(el -> el % 2 == 0).limit(100).forEach(System.out::println);
```

#### Creating Streams from Arrays

You can create streams from arrays by using the static `stream` method on the `Arrays` class as shown below.
```java
String[] tags = {"java", "git", "lambdas", "machine-learning"};
Arrays.stream(tags).map(String::toUpperCase).forEach(System.out::println);
```

You can also create a stream from the array by specifying the start and end indexes as shown below. Here, starting index is inclusive and ending index is exclusive.

```java
Arrays.stream(tags, 1, 3).map(String::toUpperCase).forEach(System.out::println);
```

### Parallel Streams

One advantage that you get by using Stream abstraction is that now library can effectively manage parallelism as iteration is internal. So, to process a stream in parallel it is as easy as shown below.

```java
String[] urls = {"https://www.google.co.in/", "https://twitter.com/", "http://www.facebook.com/"};
Arrays.stream(urls).parallel().map(url -> getUrlContent(url)).forEach(System.out::println);
```
