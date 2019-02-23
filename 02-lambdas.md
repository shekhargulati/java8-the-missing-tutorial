Lambdas [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/02-lambdas.md)](http://ttr.myapis.xyz/)
-----

One of the most important features in Java 8 is the introduction of Lambda
expressions. They make your code concise and allow you to pass behavior around.
For some time now, Java has been criticized for being verbose and for lacking
functional programming capabilities. With functional programming becoming more
popular and relevant, Java is forced to embrace the functional style of
programming. Otherwise, Java would become irrelevant.

Java 8 is a big step forward in making the world's most popular language adopt
the functional style of programming. To support a functional programming style,
the language must support functions as first class citizens. Prior to Java 8,
writing a clean functional style code was not possible without the use of an
anonymous inner class boilerplate. With the introduction of Lambda expressions,
functions have become first class citizens and they can be passed around just
like any other variable.

Lambda expressions allow you to define an anonymous function that is not bound
to an identifier. You can use them like any other construct in your programming
language, like variable declaration. Lambda expressions are required if a
programming language needs to support higher order functions. Higher order
functions are functions that either accept other functions as arguments or
returns a function as a result.

> Code for this section is inside [ch02 package](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch02).

Now, with the introduction of Lambda expressions in Java 8, Java supports higher
order functions. Let us look at the  canonical example of Lambda expression -- a
sort function in Java's `Collections` class. The `sort` function has two
variants -- one that takes a `List` and another that takes a `List` and a
`Comparator`. The second `sort` function is an example of a Higher order
function that accepts a lambda expression as shown below in the code snippet.

```java
List<String> names = Arrays.asList("shekhar", "rahul", "sameer");
Collections.sort(names, (first, second) -> first.length() - second.length());
```

The code shown above sorts the `names` by their length. The output of the
program will be as shown below.

```
[rahul, sameer, shekhar]
```

The expression `(first, second) -> first.length() - second.length()` shown above
in the code snippet is a lambda expression of type `Comparator<String>`.

* The `(first, second)` are parameters of the `compare` method of `Comparator`.
* `first.length() - second.length()` is the function body that compares the
length of two names.
* `->` is the lambda operator that separates parameters from the body of the
lambda.

Before we dig deeper into Java 8 Lambdas support, let's look into their history
to understand why they exist.

## History of Lambdas

Lambda expressions have their roots in the Lambda Calculus. [Lambda
calculus](https://en.wikipedia.org/wiki/Lambda_calculus) originated from the
work of [Alonzo Church](https://en.wikipedia.org/wiki/Alonzo_Church) on
formalizing the concept of expressing computation with functions. Lambda
calculus is a Turing complete, mathematically formal way to express
computations. Turing complete means you can express any mathematical computation
via lambdas.

Lambda calculus became the basis for a strong, theoretical foundation of
functional programming languages. Many popular functional programming languages
like Haskell and Lisp are based on Lambda calculus. The idea of higher order
functions, i.e. a function accepting other functions, came from Lambda calculus.

The main concept in Lambda calculus is the expression. An expression can be
expressed as:

```
<expression> := <variable> | <function>| <application>
```

* **variable** -- A variable is a placeholder like x, y, z for values like 1, 2,
etc, or lambda functions.
* **function** -- It is an anonymous function definition that takes one variable
and produces another lambda expression. For example, `λx.x*x` is a function to
compute square of a number.
* **application** -- This is the act of applying a function to an argument.
Suppose you want a square of 10, so in lambda calculus you will write a square
function `λx.x*x` and apply it to 10. This function application  would result in
`(λx.x*x) 10 = 10*10 = 100`.You can not only apply simple values like 10 but,
you can apply a function to another function to produce another function. For
example, `(λx.x*x) (λz.z+10)` will produce a function `λz.(z+10)*(z+10)`. Now,
you can use this function to produce number plus 10 squares. This is an example
of higher order function.

Now, you understand Lambda calculus and its impact on functional programming
languages. Let's learn how it is implemented in Java 8.

## Passing behavior before Java 8

Before Java 8, the only way to pass behavior was to use anonymous classes.
Suppose you want to send an email in another thread after user registration.
Before Java 8, you would write code like one shown below.

```java
sendEmail(new Runnable() {
            @Override
            public void run() {
                System.out.println("Sending email...");
            }
        });
```

Where the `sendEmail` method has following method signature.

```java
public static void sendEmail(Runnable runnable)
```

The problem with the above mentioned code is not only that we have to
encapsulate our action, i.e. `run` method in an object, but, the bigger problem
is that it misses the programmer's intent, i.e. to pass behavior to the
`sendEmail` function. If you have used libraries like Guava, you would have
certainly felt the pain of writing anonymous classes. A simple example of
filtering all the tasks with **lambda** in their title is shown below.

```java
Iterable<Task> lambdaTasks = Iterables.filter(tasks, new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return input.getTitle().contains("lambda");
            }
});
```

With Java 8 Stream API, you can write the above mentioned code without the use
of a third party library like Guava. We will cover streams in [chapter
3](./03-streams.md). So, stay tuned!

## Java 8 Lambda expressions

In Java 8, we would write the code using a lambda expression as shown below. We
have mentioned the same example in the code snippet above.

```java
sendEmail(() -> System.out.println("Sending email..."));
```

The code shown above is concise and does not pollute the programmer's intent to
pass behavior. `()` is used to represent that the lambda has no function
parameters, i.e. `Runnable` interface `run` method does not have any parameters.
`->` is the lambda operator that separates the parameters from the function body
which prints `Sending email...` to the standard output.

Let's look at the Collections.sort example again so that we can understand how
lambda expressions work with the parameters. To sort a list of names by their
length, we passed a `Comparator` to the sort function. The `Comparator` is shown
below.

```java
Comparator<String> comparator = (first, second) -> first.length() - second.length();
```

The lambda expression that we wrote was corresponding to the `compare` method in
the Comparator interface. The signature of the `compare` function is shown
below.

```java
int compare(T o1, T o2);
```

`T` is the type parameter passed to `Comparator` interface. In this case it will
be a `String` as we are working over a List of `String`s, i.e. names.

In the lambda expression, we didn't have to explicitly provide the type --
String. The `javac` compiler inferred the type information from its context. The
Java compiler inferred that both parameters should be String, as we are sorting
a List of String, and the `compare` method specifies only one type, `T`. The act
of inferring the type from the context in this way is called **Type Inference**.
Java 8 improves the already existing type inference system in Java and makes it
more robust and powerful to support lambda expressions. `javac` under the hood
looks for the information close to your lambda expression and uses that
information to find the correct type for the parameters.

> In most cases, `javac` will infer the type from the context. In case it can't
> resolve type because of missing or incomplete context information then the
> code will not compile. For example, if we remove `String` type information
> from `Comparator` then code will fail to compile as shown below.

```java
Comparator comparator = (first, second) -> first.length() - second.length(); // compilation error - Cannot resolve method 'length()'
```

## How do Lambda expressions work in Java 8?

You may have noticed that the type of a lambda expression is some interface like
`Comparator` in the above example. You can't use any arbitrary interface with
lambda expressions. ***Only those interfaces which have only one non-object
abstract method can be used with lambda expressions***. These kinds of
interfaces are called **Functional interfaces**, and they can be annotated with
the `@FunctionalInterface` annotation. `Runnable` interface is an example of a
functional interface, as shown below.

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

`@FunctionalInterface` annotation is not mandatory but, it can help tools know
that an interface is a functional interface and perform meaningful actions. For
example, if you try to compile an interface that annotates itself with the
`@FunctionalInterface` annotation, and has multiple abstract methods, then
compilation will fail with an error ***Multiple non-overriding abstract methods
found***. Similarly, if you add `@FunctionInterface` annotation to an interface
without any method, i.e. a marker interface, then you will get error message
***No target method found***.

Let's answer one of the most important questions that might be coming to your
mind. ***Are Java 8 lambda expressions just the syntactic sugar over anonymous
inner classes, or how do functional interfaces otherwise get translated to
bytecode?***

The short answer is **NO**. Java 8 does not use anonymous inner classes mainly
for two reasons:

1. **Performance impact**: If lambda expressions were implemented using
anonymous classes, then each lambda expression would result in a class file on
disk. If these classes were loaded by the JVM at startup, then the startup time
of the JVM would increase, as all the classes would need to be first loaded and
verified before use.

2. **Possibility to change in future**: If Java 8 designers would have used
anonymous classes from the start, then it would have limited the scope of future
lambda implementation changes.

### Using invokedynamic

Java 8 designers decided to use the `invokedynamic` instruction, added in Java
7, to defer the translation strategy at runtime. When `javac` compiles the code,
it captures the lambda expression and generates an `invokedynamic` call site
(called lambda factory). The `invokedynamic` call site, when invoked, returns an
instance of the functional interface to which the lambda is being converted. For
example, if we look at the byte code of our Collections.sort example, it will
look like as shown below.

```
public static void main(java.lang.String[]);
    Code:
       0: iconst_3
       1: anewarray     #2                  // class java/lang/String
       4: dup
       5: iconst_0
       6: ldc           #3                  // String shekhar
       8: aastore
       9: dup
      10: iconst_1
      11: ldc           #4                  // String rahul
      13: aastore
      14: dup
      15: iconst_2
      16: ldc           #5                  // String sameer
      18: aastore
      19: invokestatic  #6                  // Method java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
      22: astore_1
      23: invokedynamic #7,  0              // InvokeDynamic #0:compare:()Ljava/util/Comparator;
      28: astore_2
      29: aload_1
      30: aload_2
      31: invokestatic  #8                  // Method java/util/Collections.sort:(Ljava/util/List;Ljava/util/Comparator;)V
      34: getstatic     #9                  // Field java/lang/System.out:Ljava/io/PrintStream;
      37: aload_1
      38: invokevirtual #10                 // Method java/io/PrintStream.println:(Ljava/lang/Object;)V
      41: return
}
```

The interesting part of the byte code shown above is the line 23
`23: invokedynamic #7,  0              // InvokeDynamic #0:compare:()Ljava/util/Comparator;`
where a call to `invokedynamic` is made.

The second step is to convert the body of the lambda expression into a method
that will be invoked through the `invokedynamic` instruction. This is the step
where JVM implementers have the liberty to choose their own strategy.

I have only glossed over this topic. You can read about the internals at
http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html.

## Anonymous classes vs lambdas

Let's compare anonymous classes with lambdas to understand the differences
between them.

1. In anonymous classes, `this` refers to the anonymous class itself whereas in
lambda expressions, `this` refers to the class enclosing the lambda expression.

2. You can shadow variables in the enclosing class inside the anonymous class.
This gives a compile time error when done inside a lambda expression.

3. The type of the lambda expression is determined from the context, whereas the
type of the anonymous class is specified explicitly as you create the instance
of anonymous class.

## Do I need to write my own functional interfaces?

By default, Java 8 comes with many functional interfaces which you can use in
your code. They exist inside `java.util.function` package. Let's have a look at
few of them.

### java.util.function.Predicate<T>

This functional interface is used to define a check for some condition, i.e. a
predicate. Predicate interface has one method called `test` which takes a value
of type `T` and returns boolean. For example, from a list of `names` if we want
to filter out all the names which starts with **s** then we will use a predicate
as shown below.

```java
Predicate<String> namesStartingWithS = name -> name.startsWith("s");
```

### java.util.function.Consumer<T>

This functional interface is used for performing actions which do not produce
any output. The consumer interface has one method called `accept` which takes a
value of type `T` and returns nothing, i.e. it is void. For example, sending an
email with given message.

```java
Consumer<String> messageConsumer = message -> System.out.println(message);
```

### java.util.function.Function<T,R>

This functional interface takes one value and produces a result. For example, if
we want to uppercase all the names in our `names` list, we can write a Function
as shown below.

```java
Function<String, String> toUpperCase = name -> name.toUpperCase();
```

### java.util.function.Supplier<T>

This functional interface does not take any input but produces a value. This
could be used to generate unique identifiers as shown below.

```java
Supplier<String> uuidGenerator= () -> UUID.randomUUID().toString();
```

We will cover more functional interfaces throughout this tutorial.

## Method references

There would be times when you will be creating lambda expressions that only
calls a specific method like `Function<String, Integer> strToLength = str ->
str.length();`. The lambda only calls `length()` method on the `String` object.
This could be simplified using method references like `Function<String, Integer>
strToLength = String::length;`. They can be seen as shorthand notation for
lambda expression that only calls a single method. In the expression
`String::length`, `String` is the target reference, `::` is the delimiter, and
`length` is the function that will be called on the target reference. You can
use method references on both static and instance methods.

### Static method references

Suppose we have to find a maximum number from a list of numbers, then we can
write a method reference `Function<List<Integer>, Integer> maxFn =
Collections::max`. `max` is a static method in the `Collections` class that
takes one argument of type `List`. You can then call this like
`maxFn.apply(Arrays.asList(1, 10, 3, 5))`. The above lambda expression is
equivalent to a `Function<List<Integer>, Integer> maxFn = (numbers) ->
Collections.max(numbers);` lambda expression.

### Instance method references

This is used for method reference to an instance method, for example
`String::toUpperCase` calls `toUpperCase` method on a `String` reference. You
can also use method reference with parameters for example `BiFunction<String,
String, String> concatFn = String::concat`. The `concatFn` can be called as
`concatFn.apply("shekhar", "gulati")`. The `String` `concat` method is called on
a String object and passed a parameter like `"shekhar".concat("gulati")`.

## Exercise >> Lambdify me

Let's look at the code shown below and apply what we have learned so far.

```java
public class Exercise_Lambdas {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<String> titles = taskTitles(tasks);
        for (String title : titles) {
            System.out.println(title);
        }
    }

    public static List<String> taskTitles(List<Task> tasks) {
        List<String> readingTitles = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getType() == TaskType.READING) {
                readingTitles.add(task.getTitle());
            }
        }
        return readingTitles;
    }

}
```

The code shown above first fetches all the Tasks from a utility method
`getTasks`. We are not interested in the `getTasks` implementation. The
`getTasks` could fetch tasks by accessing a web-service or database or
in-memory. Once you have tasks, we filter all the reading tasks and extract the
title field from the task. We add extracted title to a list and then finally
return all the reading titles.

Let's start with the simplest refactor -- using `foreach` on a list with method
reference.

```java
public class Exercise_Lambdas {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<String> titles = taskTitles(tasks);
        titles.forEach(System.out::println);
    }

    public static List<String> taskTitles(List<Task> tasks) {
        List<String> readingTitles = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getType() == TaskType.READING) {
                readingTitles.add(task.getTitle());
            }
        }
        return readingTitles;
    }

}
```

Using `Predicate<T>` to filter out tasks.

```java
public class Exercise_Lambdas {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<String> titles = taskTitles(tasks, task -> task.getType() == TaskType.READING);
        titles.forEach(System.out::println);
    }

    public static List<String> taskTitles(List<Task> tasks, Predicate<Task> filterTasks) {
        List<String> readingTitles = new ArrayList<>();
        for (Task task : tasks) {
            if (filterTasks.test(task)) {
                readingTitles.add(task.getTitle());
            }
        }
        return readingTitles;
    }

}
```

Using `Function<T,R>` for extracting out title from the Task.

```java
public class Exercise_Lambdas {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<String> titles = taskTitles(tasks, task -> task.getType() == TaskType.READING, task -> task.getTitle());
        titles.forEach(System.out::println);
    }

    public static <R> List<R> taskTitles(List<Task> tasks, Predicate<Task> filterTasks, Function<Task, R> extractor) {
        List<R> readingTitles = new ArrayList<>();
        for (Task task : tasks) {
            if (filterTasks.test(task)) {
                readingTitles.add(extractor.apply(task));
            }
        }
        return readingTitles;
    }
}
```

Using method reference for extractor

```java
public class Exercise_Lambdas {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<String> titles = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, Task::getTitle);
        titles.forEach(System.out::println);
        List<LocalDate> createdOnDates = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, Task::getCreatedOn);
        createdOnDates.forEach(System.out::println);
        List<Task> filteredTasks = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, Function.identity());
        filteredTasks.forEach(System.out::println);
    }

    public static <R> List<R> filterAndExtract(List<Task> tasks, Predicate<Task> filterTasks, Function<Task, R> extractor) {
        List<R> readingTitles = new ArrayList<>();
        for (Task task : tasks) {
            if (filterTasks.test(task)) {
                readingTitles.add(extractor.apply(task));
            }
        }
        return readingTitles;
    }
}
```

We can also write our own **Functional Interface** that clearly describes the
intent of the developer. We can create an interface `TaskExtractor` that extends
`Function` interface. The input type of interface is fixed to `Task` and output
type depend on the implementing lambda. This way the developer will only have to
worry about the result type, as input type will always remain `Task`.

```java
public class Exercise_Lambdas {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<Task> filteredTasks = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, TaskExtractor.identityOp());
        filteredTasks.forEach(System.out::println);
    }

    public static <R> List<R> filterAndExtract(List<Task> tasks, Predicate<Task> filterTasks, TaskExtractor<R> extractor) {
        List<R> readingTitles = new ArrayList<>();
        for (Task task : tasks) {
            if (filterTasks.test(task)) {
                readingTitles.add(extractor.apply(task));
            }
        }
        return readingTitles;
    }

}


interface TaskExtractor<R> extends Function<Task, R> {

    static TaskExtractor<Task> identityOp() {
        return t -> t;
    }
}
```

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/02-lambdas)](https://github.com/igrigorik/ga-beacon)
