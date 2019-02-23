Annotation Improvements [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/12-annotations.md)](http://ttr.myapis.xyz/)
-------

There are couple of improvements made in the annotation mechanism in Java 8.
These are:

1. Repeatable annotations
2. Type annotations

> Code for this section is inside [ch12 package](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch12).

## Repeatable annotations

Before Java 8, it was not possible to use same annotation twice at the same
location i.e.  you can't use annotation `@Foo` twice on a method. If you have
used JPA then you would have use an annotation called `@JoinColumns` that allows
wraps multiple `@JoinColumn` annotation as shown below.

```java
@ManyToOne
@JoinColumns({
    @JoinColumn(name="ADDR_ID", referencedColumnName="ID"),
    @JoinColumn(name="ADDR_ZIP", referencedColumnName="ZIP")
})
public Address getAddress() { return address; }
```

In Java 8, you can write the same as shown below because with Java 8 you can repeat an annotation multiple time at the same location.

```java
@ManyToOne
@JoinColumn(name="ADDR_ID", referencedColumnName="ID"),
@JoinColumn(name="ADDR_ZIP", referencedColumnName="ZIP")
public Address getAddress() { return address; }
```

***With Java 8 you can use same annotation type multiple times possibly with
different arguments at any location(class, method, field declaration) in your
Java code.***

### Writing your own repeatable Annotations

To write your own repeatable annotation you have to do the following:

**Step 1:** Create an annotation with `@Repeatable` annotation as shown below.
`@Repeatable` annotation requires you to specify a mandatory value for the
container type that will contain the annotation. We will create container
annotation in step 2.

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CreateVms.class)
public @interface CreateVm {
    String name();
}
```

**Step 2:** Create a container annotation that holds the annotation.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface CreateVms {
    CreateVm[] value();
}
```

Now you can use the annotation multiple times on any method as shown below.

```java
@CreateVm(name = "vm1")
@CreateVm(name = "vm2")
public void manage() {
    System.out.println("Managing ....");
}
```

If you have to find all the repeatable annotations on a method then you can use
`getAnnotationsByType` method that is now available on `java.lang.Class` and
`java.lang.reflect.Method`. To print all the vm names, you can write code as
shown below.

```java
CreateVm[] createVms = VmManager.class.getMethod("manage").getAnnotationsByType(CreateVm.class);
Stream.of(createVms).map(CreateVm::name).forEach(System.out::println);
```

## Type annotations

You can now apply annotations at two more target locations -- TYPE_PARAMETER and
TYPE_USE.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE_PARAMETER})
public @interface MyAnnotation {
}
```

You can use it like

```java
class MyAnnotationUsage<@MyAnnotation T> {
}
```

You can also use annotations at type usage as shown below.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE_USE})
public @interface MyAnnotation {
}
```

Then you can use them as shown below.

```java
public static void doSth() {
    List<@MyAnnotation String> names = Arrays.asList("shekhar");
}
```

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/12-annotations)](https://github.com/igrigorik/ga-beacon)
