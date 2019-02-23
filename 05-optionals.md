Optionals [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/05-optionals.md)](http://ttr.myapis.xyz/)
----

Every Java developer, whether beginner, novice, or seasoned, has in their lifetime experienced `NullPointerException`. We all have wasted or spent many hours trying to fix bugs caused by `NullPointerException`. According to `NullPointerException`'s JavaDoc, ***NullPointerException is thrown when an application attempts to use null in a case where an object is required.***. This means if we invoke a method or try to access a property on ***null*** reference, then our code will explode and `NullPointerException` is thrown. In this chapter, you will learn how to write null-free code using Java 8's `Optional`.

> On a lighter note, if you look at the JavaDoc of NullPointerException you will find that author of this exception is ***unascribed***. If the author is unknown, unascribed is used (nobody wants to take ownership of NullPointerException ;)).

## What are null references?

In 2009, at QCon conference, ***[Sir Tony Hoare](https://en.wikipedia.org/wiki/Tony_Hoare)*** stated that he invented the `null` reference type while designing the ***ALGOL W*** programming language. `null` was designed to signify absence of a value. He called *null references* as a *billion-dollar mistake*. You can watch the full video of his presentation on Infoq http://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare.

Most of the programming languages like C, C++, C#, Java, Scala, etc. have a nullable type as part of their type system, which allows the value to be set to a special value, **Null**, instead of other possible data type values.

## Why null references are a bad thing

Let's look at the example Task management domain classes shown below. Our domain model is very simple with only two classes -- Task and User. A task can be assigned to a user.

> Code for this section is inside [ch05 package](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch05).

```java
public class Task {
    private final String id;
    private final String title;
    private final TaskType type;
    private User assignedTo;

    public Task(String id, String title, TaskType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Task(String title, TaskType type) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public TaskType getType() {
        return type;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }
}

public class User {

    private final String username;
    private String address;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
```

Given the above domain model, if we have to find the User who is assigned a task with id `taskId` then we would write code as shown below.

```java
public String taskAssignedTo(String taskId) {
  return taskRepository.find(taskId).getAssignedTo().getUsername();
}
```

The biggest problem with the code shown above is that absence of the value is not visible in the API, i.e. if the `task` is not assigned to any user, then the code will throw `NullPointerException` when `getAssignedTo` is called. The `taskRepository.find(taskId)` and `taskRepository.find(taskId).getAssignedTo()` could return `null`. This forces clients of the API to program defensively, and check for null frequently, as shown below.

```java
public String taskAssignedTo(String taskId) throws TaskNotFoundException {
        Task task = taskRepository.find(taskId);
        if (task != null) {
            User assignedTo = task.getAssignedTo();
            if (assignedTo != null)
                return assignedTo.getUsername();
            return "NotAssigned";
        }
        throw new TaskNotFoundException(String.format("No task exist with id '%s'", taskId));
}
```

The code shown above misses developer intent and bloats client code with `if-null` checks. The developer somehow wanted to use optional data type but he was forced to write `if-null` checks. I am sure you would have written this kind of code in your day to day programming.

## Null Object pattern

A common solution to working with `null` references is to use [Null Object pattern](https://en.wikipedia.org/wiki/Null_Object_pattern). The idea behind this pattern is very simple -- instead of returning null, you should return a null object that implements your interface or class. So, you can create a `NullUser` as shown below.

```java
public class NullUser extends User {

    public NullUser(String username) {
        super("NotAssigned");
    }
}
```

So now we could return a `NullUser` when no user is assigned a task. We can change the `getAssignedTo` method to return `NullUser` when no user is assigned a task.

```java
public User getAssignedTo() {
  return assignedTo == null ? NullUser.getInstance() : assignedTo;
}
```

Now client code can be simplified to not use a null check for `User`, as shown below. In this example, it does not make sense to use Null Object pattern for `Task` because non-existence of task in the repository is an exception situation. Also, by adding `TaskNotFoundException` in the `throws` clause, we have made it explicit for the client that this code can throw an exception.

```java
public String taskAssignedTo(String taskId) throws TaskNotFoundException {
        Task task = taskRepository.find(taskId);
        if (task != null) {
            return task.getAssignedTo().getUsername();
        }
        throw new TaskNotFoundException(String.format("No task exist with id '%s'", taskId));
}
```

## Java 8 -- Introduction of Optional data type

Java 8 introduced a new data type, `java.util.Optional<T>`, which encapsulates an empty value. It makes the intent of the API clear. If a function returns a value of type `Optional<T>`, then it tells the clients that a value might not be present. Use of the `Optional` data type makes it explicit to the API client when it should expect an optional value. The purpose of using the `Optional` type is to help API designers make it visible to their clients, by looking at the method signature, whether they should expect an optional value or not.

Let's update our domain model to reflect optional values.

```java
import java.util.Optional;

public class Task {
    private final String title;
    private final User assignedTo;
    private final String id;

    public Task(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Task(String id, String title, User assignedTo) {
        this.id = id;
        this.title = title;
        this.assignedTo = assignedTo;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Optional<User> getAssignedTo() {
        return assignedTo;
    }
}

import java.util.Optional;

public class User {

    private final String username;
    private final String address;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String address) {
        this.username = username;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public Optional<String> getAddress() {
        return address;
    }
}
```

Since `Task.assignedTo` and `User.username` are nullable fields, each getter should return `Optional<User>` and `Optional<String>`. Now whoever tries to work with `assignedTo` User would know that it might not be present and they can handle it in a declarative way. We will talk about `Optional.empty` and `Optional.of` methods in the next section.

## Working with creational methods in the java.util.Optional API

In the domain model shown above, we used a couple of creational methods of the Optional class, but didn't discuss them. Three creational methods which are part of the `Optional` API follow below.

* `Optional.empty`: This is used to create an Optional when a value is not present, like we did above (`this.assignedTo = Optional.empty();`) in the constructor.

* `Optional.of(T value)`: This is used to create an Optional from a non-null value. It throws `NullPointerException` when `value` is null. We used it in the code shown above (`this.address = Optional.of(address);`).

* `Optional.ofNullable(T value)`: This static factory method which works for both null and non-null values. For null values it will create an empty Optional and for non-null values it will create an Optional using the value.

Below is a simple example of how you can write an API using Optional.

```java
public class TaskRepository {

    private static final Map<String, Task> TASK_STORE = new ConcurrentHashMap<>();

    public Optional<Task> find(String taskId) {
        return Optional.ofNullable(TASK_STORE.get(taskId));
    }

    public void add(Task task) {
        TASK_STORE.put(task.getId(), task);
    }
}
```

## Using Optional values

Optional can be thought of as a Stream with one element. It has methods similar to Stream API like `map`, `filter`, and `flatMap`, which we can use to work with values contained in the `Optional`.

### Getting title for a Task

To read the value of title for a Task, we would write code as shown below. The `map` function was used to transform from `Optional<Task>` to `Optional<String>`. The `orElseThrow` method is used to throw a custom business exception when no Task is found.

```java
public String taskTitle(String taskId) {
    return taskRepository.
                find(taskId).
                map(Task::getTitle).
                orElseThrow(() -> new TaskNotFoundException(String.format("No task exist for id '%s'",taskId)));
}
```

There are three variants of `orElse*` method:

1. `orElse(T t)`: This is used to return the value if it exists, or returns the value `t` passed as parameter, like `Optional.ofNullable(null).orElse("NoValue")`. This will return `"NoValue"` as no value exists.

2. `orElseGet`: This will return the value if it is present, otherwise invokes the `Supplier`'s `get` method to produce a new value. For example, `Optional.ofNullable(null).orElseGet(() -> UUID.randomUUID().toString()` could be used to lazily produce a value only when none is present.

3. `orElseThrow`: This allow clients to throw their own custom exception when a value is not present.

The find method shown above returns an `Optional<Task>` that the client can use to get the value. Suppose we want to get the task's title from the `Optional<Task>` -- we can do that by using the `map` function, as shown below.

### Getting username of the assigned user

To get the username of the user who is assigned a task, we can use the `flatMap` method, as shown below.

```java
public String taskAssignedTo(String taskId) {
  return taskRepository.
              find(taskId).
              flatMap(task -> task.getAssignedTo().map(user -> user.getUsername())).
              orElse("NotAssigned");
}
```

### Filtering with Optional

The third Stream API like operation supported by `Optional` is `filter`, which allows you to filter an Optional based on some property, as shown in the example below.

```java
public boolean isTaskDueToday(Optional<Task> task) {
        return task.flatMap(Task::getDueOn).filter(d -> d.isEqual(LocalDate.now())).isPresent();
}
```

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/05-optionals)](https://github.com/igrigorik/ga-beacon)
