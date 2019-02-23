Streams [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/03-streams.md)](http://ttr.myapis.xyz/)
------

In [chapter 2](./02-lambdas.md), we learned how lambdas can help us write clean,
concise code, by allowing us to pass behavior without the need to create a
class. Lambdas are a simple language construct that help developers express
their intent on the fly by using functional interfaces. The real power of
lambdas can be experienced when an API is designed while keeping lambdas in
mind, i.e. a fluent API that makes use of Functional interfaces (we discussed
them in the [lambdas chapter](./02-lambdas.md#do-i-need-to-write-my-own-functional-interfaces)).

One such API that makes use of lambdas is the Stream API introduced in JDK 8.
Streams provide a higher level abstraction to express computations on Java
collections in a declarative way similar to how SQL helps you declaratively
query data in a database. Declarative means developers write what they want to
do rather than how it should be done. In this chapter, we will discuss the need
for a new data processing API, the difference between `Collection` and `Stream`,
and how to use the Stream API in your applications.

> Code for this section is inside [ch03 package](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch03).

## Why we need a new data processing abstraction

In my opinion, there are two reasons:

1. The `Collection` API does not provide higher level constructs to query the
data, so developers are forced to write a lot of boilerplate code for trivial
tasks.

2. It has limited language support to process `Collection` data in parallel. It
is left to the developer to use Java language concurrency constructs and process
data effectively and efficiently in parallel.

## Data processing before Java 8

Look at the code shown below and try to predict what it does.

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

The code shown above prints all the reading task titles, sorted by their title
length. Java 7 developers write this kind of code every day. To write such a
simple program, we had to write 15 lines of Java code. The bigger problem with
the above mentioned code is not the number of lines a developer has to write
but, that it misses the developer's intent, i.e. filtering reading tasks,
sorting by title length, and transforming to String List.

## Data processing in Java 8

The above mentioned code can be simplified using the Java 8 `Stream` API, as
shown below.

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

The code shown above constructs a pipeline comprising multiple stream
operations, each discussed below.

* **stream()** - Created a stream pipeline by invoking the `stream()` method on
the source collection, i.e. `tasks` `List<Task>`.
* **filter(Predicate<T>)** - This operation extracted elements in the stream
matching the condition defined by the predicate. Once you have a stream you can
call zero or more intermediate operations on it. The lambda expression `task ->
task.getType() == TaskType.READING` defined a predicate to filter all reading
tasks. The type of lambda expression is `java.util.function.Predicate<Task>`.
* **sorted(Comparator<T>)**: This operation returns a stream consisting of all
the stream elements sorted by the Comparator defined by lambda expression i.e.
`(t1, t2) -> t1.getTitle().length() - t2.getTitle().length()` in the example
shown above.
* **map(Function<T,R>)**: This operation returns a stream after applying the
Function<T,R> on each element of this stream.
* **collect(toList())** - This operation collects results of the operations
performed on the Stream into a List.

### Why Java 8 code is better

In my opinion Java 8 code is better because of following reasons:

1. Java 8 code clearly reflects developer intent of filtering, sorting, etc.

2. Developers express what they want to do rather than how they want do it by
using a higher level abstraction in the form of the Stream API.

3. The Stream API provides a unified language for data processing. Now
developers will have a common vocabulary when talking about data processing.
When two developers talk about a `filter` function, you can be sure that they
both are applying a data filtering operation.

4. No boilerplate code is required to express data processing. Developers no
longer have to write explicit `for` loops, or create temporary collections to
store data. All is taken care by the Stream API itself.

5. Streams do not modify your underlying collection - they are non-mutating.

## What is a Stream?

Stream is an abstract view over some data. For example, Stream can be a view
over a list, or lines in a file, or any other sequence of elements. The Stream
API provides aggregate operations that can be performed sequentially, or in
parallel. ***One thing that developers should keep in mind is that Stream is a
higher level abstraction, not a data structure. Stream does not store your
data.*** Streams are **lazy** by nature, and they are only computed when
accessed. This allows us to produce infinite streams of data. In Java 8, you can
very easily write a Stream that will produce infinite unique identifiers as
shown below.

```
public static void main(String[] args) {
    Stream<String> uuidStream = Stream.generate(() -> UUID.randomUUID().toString());
}
```

There are various static factory methods like `of`, `generate`, and `iterate` in
the Stream interface, that one can use to create Stream instances. The
`generate` method shown above takes a `Supplier`. `Supplier` is a functional
interface to describe a function that does not take any input and produce a
value. We passed the `generate` method a supplier, that, when invoked, generates
a unique identifier.

```java
Supplier<String> uuids = () -> UUID.randomUUID().toString()
```

If you run this program nothing will happen as Streams are lazy and until they
are accessed nothing will be computed. If we update the program to the one shown
below we will see UUID printing to the console. The program will never
terminate.

```java
public static void main(String[] args) {
    Stream<String> uuidStream = Stream.generate(() -> UUID.randomUUID().toString());
    uuidStream.forEach(System.out::println);
}
```

Java 8 allows you to create a Stream from a Collection by calling the `stream`
method on it. Stream supports data processing operations so that developers can
express computations using higher level data processing constructs.

## Collection vs Stream

The table shown below explains the difference between a Collection and a Stream.

![Collection vs Stream](https://whyjava.files.wordpress.com/2015/10/collection_vs_stream.png)

Let's discuss External iteration vs internal iteration, and Lazy evaluation in
detail.

### External iteration vs internal iteration

The difference between Java 8 Stream API code and Collection API code shown
above is who controls the iteration -- the iterator or the client that uses the
iterator. Users of the Stream API just provide the operations they want to
apply, and the iterator applies those operations to every element in the
underlying Collection. When iterating over the underlying collection and the
process is handled by the iterator itself, this is called **internal
iteration**. On the other hand, when iteration is handled by the client it is
called **external iteration**. The use of `for-each` construct in the Collection
API code is an example of **external iteration**.

Some might argue that in the Collection API code we didn't have to work with the
underlying iterator as the `for-each` construct took care of that but,
`for-each` is nothing more than syntactic sugar over manual iteration using the
iterator API. The `for-each` construct, although very simple, has a few
disadvantages -- 1) It is inherently sequential, 2) It leads to imperative code,
and 3) It is difficult to parallelize.

### Lazy evaluation

Streams are not evaluated until a terminal operation is called on them. Most of
the operations in the Stream  API return a Stream. These operations do not
perform any execution -- they just build the pipeline. Let's look at the code
shown below and try to predict its output.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream().map(n -> n / 0).filter(n -> n % 2 == 0);
```

In the code shown above, we are dividing elements in numbers stream by 0. We
might expect that this code will throw `ArithmeticException` when the code is
executed. But, when you run this code no exception will be thrown. This is
because streams are not evaluated until a terminal operation is called on the
stream. If you add terminal operation to the stream pipeline, then the stream is
executed, and an exception is thrown.

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

## Using Stream API

The Stream API provides many operations that developers can use to query data
from collections. Stream operations fall into either of the two categories --
intermediate operation, or terminal operation.

**Intermediate operations** are functions that produce another stream from the
existing stream like `filter`, `map`, `sorted`, etc.

**Terminal operations** are functions that produce a non-stream result from the
Stream like `collect(toList())` , `forEach`, `count` etc.

Intermediate operations allow you to build the pipeline which gets executed when
you call the terminal operation. Below is the list of functions that are part of
the Stream API.

<a href="https://whyjava.files.wordpress.com/2015/07/stream-api.png"><img class="aligncenter size-full wp-image-2983" src="https://whyjava.files.wordpress.com/2015/07/stream-api.png" alt="stream-api" height="450" /></a>

### Example domain

Throughout this tutorial we will use Task management domain to explain concepts.
Our example domain has one class called Task -- a task to be performed by user.
The class is shown below.

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
The sample dataset is given below. We will use this list throughout our Stream
API examples.

```java
Task task1 = new Task("Read Version Control with Git book", TaskType.READING, LocalDate.of(2015, Month.JULY, 1)).addTag("git").addTag("reading").addTag("books");

Task task2 = new Task("Read Java 8 Lambdas book", TaskType.READING, LocalDate.of(2015, Month.JULY, 2)).addTag("java8").addTag("reading").addTag("books");

Task task3 = new Task("Write a mobile application to store my tasks", TaskType.CODING, LocalDate.of(2015, Month.JULY, 3)).addTag("coding").addTag("mobile");

Task task4 = new Task("Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2015, Month.JULY, 4)).addTag("blogging").addTag("writing").addTag("streams");

Task task5 = new Task("Read Domain Driven Design book", TaskType.READING, LocalDate.of(2015, Month.JULY, 5)).addTag("ddd").addTag("books").addTag("reading");

List<Task> tasks = Arrays.asList(task1, task2, task3, task4, task5);
```

> We will not discuss about Java 8 Date Time API in this chapter. For now, just
> think of as the fluent API to work with dates.

### Example 1: Find all reading task titles sorted by their creation date

The first example that we will discuss is to find all the reading task titles
sorted by creation date. The operations that we need to perform are:

1. Filter all the tasks that have TaskType as `READING`.
2. Sort the filtered values tasks by `createdOn` field.
3. Get the value of title for each task.
4. Collect the resulting titles in a List.

The following four operations can be easily translated to the code as shown
below.

```java
private static List<String> allReadingTasks(List<Task> tasks) {
        List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted((t1, t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())).
                map(task -> task.getTitle()).
                collect(Collectors.toList());
        return readingTaskTitles;
}
```

In the code shown above, we used the following methods of the Stream API:

* **filter**:  Allows you to specify a predicate to exclude some elements from
the underlying stream. The predicate **task -> task.getType() ==
TaskType.READING** selects all the tasks whose TaskType is `READING`.
* **sorted**: Allows you to specify a Comparator that will sort the stream. In
this case, you sorted based on the creation date. The lambda expression **(t1,
t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())** provides implementation
of the `compare` method of Comparator functional interface.
* **map**: It takes a lambda that implements `Function<? super T, ? extends R>`
which transforms one stream to another stream. The lambda expression **task ->
task.getTitle()** transforms a task into a title.
* **collect(toList())** It is a terminal operation that collects the resulting
reading titles into a List.

We can improve the above Java 8 code by using `comparing` method of `Comparator`
interface and method references as shown below.

```java
public List<String> allReadingTasks(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            sorted(Comparator.comparing(Task::getCreatedOn)).
            map(Task::getTitle).
            collect(Collectors.toList());

}
```

> From Java 8, interfaces can have method implementations in the form of static and default methods. This is covered in [ch01](./01-default-static-interface-methods.md).

In the code shown above, we used a static helper method `comparing` available in
the `Comparator` interface which accepts a `Function` that extracts a
`Comparable` key, and returns a `Comparator` that compares by that key. The
method reference `Task::getCreatedOn` resolves to `Function<Task, LocalDate>`.

Using function composition, we can very easily write code that reverses the
sorting order by calling the `reversed()` method on Comparator, as shown below.

```java
public List<String> allReadingTasksSortedByCreatedOnDesc(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            sorted(Comparator.comparing(Task::getCreatedOn).reversed()).
            map(Task::getTitle).
            collect(Collectors.toList());
}
```

### Example 2: Find distinct tasks

Suppose, we have a dataset which contains duplicate tasks. We can very easily
remove the duplicates and get only distinct elements by using the `distinct`
method on the stream, as shown below.

```java
public List<Task> allDistinctTasks(List<Task> tasks) {
    return tasks.stream().distinct().collect(Collectors.toList());
}
```

The `distinct()` method converts one stream into another without duplicates. It
uses the Object's `equals` method for determining the object equality. According
to that Object's `equals` method contract, when two objects are equal, they are
considered duplicates, and will be removed from the resulting stream.

### Example 3: Find top 5 reading tasks sorted by creation date

The `limit` function can be used to limit the result set to a given size.
`limit` is a short circuiting operation which means it does not evaluate all the
elements to find the result.

```java
public List<String> topN(List<Task> tasks, int n){
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            sorted(comparing(Task::getCreatedOn)).
            map(Task::getTitle).
            limit(n).
            collect(toList());
}
```

You can use `limit` along with the `skip` method to create pagination, as shown
below.

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

### Example 4: Count all reading tasks

To get the count of all the reading tasks, we can use `count` method on the
stream. This method is a terminal operation.

```java
public long countAllReadingTasks(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            count();
}
```

### Example 5: Find all unique tags from all tasks

To find all the distinct tags we have to perform following operations:

1. Extract tags for each task.
2. Collect all the tags into one stream.
3. Remove the duplicates.
4. Finally collect the result into a list.

The first and second operations can be performed by using the `flatMap`
operation on the `tasks` stream. The `flatMap` operation flattens the streams
returned by each invocation of `tasks.getTags().stream()` into one stream. Once
we have all the tags in one stream, we just used `distinct` method on it to get
all unique tags.

```java
private static List<String> allDistinctTags(List<Task> tasks) {
        return tasks.stream().flatMap(task -> task.getTags().stream()).distinct().collect(toList());
}
```

### Example 6: Check if all reading tasks have tag `books`

The Stream API has methods that allows the user to check if elements in the
dataset match a given property. These methods are `allMatch`, `anyMatch`,
`noneMatch`, `findFirst`, and `findAny`. To check if all reading titles have a
tag with name `books`, we can write code as shown below.

```java
public boolean isAllReadingTasksWithTagBooks(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            allMatch(task -> task.getTags().contains("books"));
}
```

To check whether any reading task has a `java8` tag, then we can use `anyMatch`
operation as shown below.

```java
public boolean isAnyReadingTasksWithTagJava8(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            anyMatch(task -> task.getTags().contains("java8"));
}
```

### Example 7: Creating a summary of all titles

Suppose you want to create a summary of all the titles. Use the `reduce`
operation, which reduces the stream to a value. The `reduce` function takes a
lambda which joins elements of the stream.

```java
public String joinAllTaskTitles(List<Task> tasks) {
    return tasks.stream().
            map(Task::getTitle).
            reduce((first, second) -> first + " *** " + second).
            get();
}
```

### Example 8: Working with primitive Streams

Apart from the generic stream that works over objects, Java 8 also provides
specific streams that work over primitive types like int, long, and double.
Let's look at few examples of primitive streams.

To create a range of values, we can use `range` method that creates a stream
with value starting from 0 and ending at 9. It excludes 10.

```java
IntStream.range(0, 10).forEach(System.out::println);
```

The `rangeClosed` method allows you to create streams that includes the upper
bound as well. So, the below mentioned stream will start at 1 and end at 10.

```java
IntStream.rangeClosed(1, 10).forEach(System.out::println);
```

You can also create infinite streams using iterate method on the primitive
streams as shown below.

```java
LongStream infiniteStream = LongStream.iterate(1, el -> el + 1);
```

To filter out all even numbers in an infinite stream, we can write code as shown
below.

```java
infiniteStream.filter(el -> el % 2 == 0).forEach(System.out::println);
```

We can limit the resulting stream by using the `limit` operation as shown below.

```java
infiniteStream.filter(el -> el % 2 == 0).limit(100).forEach(System.out::println);
```

### Example 9: Creating Streams from Arrays

You can create streams from arrays by using the static `stream` method on the
`Arrays` class as shown below.

```java
String[] tags = {"java", "git", "lambdas", "machine-learning"};
Arrays.stream(tags).map(String::toUpperCase).forEach(System.out::println);
```

You can also create a stream from the array by specifying the start and end
indexes as shown below. Here, starting index is inclusive and ending index is
exclusive.

```java
Arrays.stream(tags, 1, 3).map(String::toUpperCase).forEach(System.out::println);
```

## Parallel Streams

One advantage that you get by using the `Stream` abstraction is that now the
library can effectively manage parallelism, as iteration is internal. You can
make a stream parallel by calling `parallel` method on it. The `parallel` method
underneath uses the fork-join API introduced in JDK 7. By default, it will spawn
up threads equal to the number of CPUs in the host machine. In the code show
below, we are grouping numbers by thread that processed them. You will learn
about `collect` and `groupingBy` functions in chapter 4. For now, just
understand that they allow you to group elements based on a key.

```java
public class ParallelStreamExample {

    public static void main(String[] args) {
        Map<String, List<Integer>> numbersPerThread = IntStream.rangeClosed(1, 160)
                .parallel()
                .boxed()
                .collect(groupingBy(i -> Thread.currentThread().getName()));

        numbersPerThread.forEach((k, v) -> System.out.println(String.format("%s >> %s", k, v)));
    }
}
```

The output of the above program on my machine looks like as shown below.

```
ForkJoinPool.commonPool-worker-7 >> [46, 47, 48, 49, 50]
ForkJoinPool.commonPool-worker-1 >> [41, 42, 43, 44, 45, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130]
ForkJoinPool.commonPool-worker-2 >> [146, 147, 148, 149, 150]
main >> [106, 107, 108, 109, 110]
ForkJoinPool.commonPool-worker-5 >> [71, 72, 73, 74, 75]
ForkJoinPool.commonPool-worker-6 >> [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160]
ForkJoinPool.commonPool-worker-3 >> [21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 76, 77, 78, 79, 80]
ForkJoinPool.commonPool-worker-4 >> [91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145]
```

Not every thread processed the same number of elements. You can control the size
of the fork-join thread pool by setting a system property
`System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
"2")`.

Another example where you can use the `parallel` operation is when you are
processing a list of URLs, as shown below.

```java
String[] urls = {"https://www.google.co.in/", "https://twitter.com/", "http://www.facebook.com/"};
Arrays.stream(urls).parallel().map(url -> getUrlContent(url)).forEach(System.out::println);
```

If you need to understand when to use a Parallel Stream, I recommend reading
this article by Doug Lea et al. [http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html](http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html).


[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/03-streams)](https://github.com/igrigorik/ga-beacon)
