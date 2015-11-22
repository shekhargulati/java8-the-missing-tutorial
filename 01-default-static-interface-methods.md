## Default and Static Methods for Interfaces

We all understand that we should code to interfaces. Interfaces give client a contract which they should use without relying on the implementation details(i.e. classes). Hence, promoting **loose coupling**. Designing clean interfaces is one of the most important aspect of API design. One of the SOLID principle **[Interface segregation](https://en.wikipedia.org/wiki/Interface_segregation_principle)** talks about designing smaller client-specific interfaces instead of designing one general purpose interface. Interface design is the key to clean and effective API's for your libraries and applications.

You can follow the **7 Days with Java 8** series at http://shekhargulati.com/7-days-with-java-8/

If you have designed any API then with time you would have felt the need to add new methods to the API. Once API is published it becomes impossible to add methods to an interface without breaking existing implementations. To make this point clear, let's suppose you are building a client API for a social network and one of the API's that you want to design is to offer user suggestions based on user initials. So, if someone calls your API method with initial `sh` then you would suggest users like `Shekhar`, `Shankar`, `Shane`. So, you came up with `UserSuggester` interface as shown below.

```java
public interface UserSuggester {
    List<User> suggestions(String initials);
}
```

API turned out to be very useful to the API consumers and some of the consumers decided to have their own implementation of the `UserSuggester` interface. After talking to your users you came to know that most of them would like to have a way to specify filter criteria as well so that they can filter down user suggestions. It looked a very simple API change so you added one more method to the API -- `suggestions(String initials, Predicate<User> predicate)`.

```java
public interface UserSuggester {
    List<User> suggestions(String initials);
    List<User> suggestions(String initials, Predicate<User> predicate);
}
```

Adding a method to an interface broke the source compatibility of the API. Adding methods to an interface leads to compilation failures when classes that depend on the interface don't implement the method. This means users who were implementing `UserSuggester` interface would have to add implementation for `suggestions(String initials, Predicate<User> predicate)` otherwise their code will not compile. This is a big problem for API designers as it makes difficult to evolve API. Prior to Java 8, it was not possible to have method implementations inside interfaces. This often becomes a problem when it was required to extend an API i.e. adding one or more methods to the interface definition.  

Java 8 introduced two ways to declare method with implementations inside an interface. These are:

* **static methods**: This allows users to declare static methods inside an interface. `Stream` interface has few static helper methods like `of`, `empty`, `concat`, etc. The static methods inside an interface could be used to replace static helper classes that we normally create to define helper methods associated with a type. For example, `Collections` class is a helper class that defines various helper methods to work with Collection and associated interfaces. The methods define in `Collections` class could easily be added to `Collection` or its child interface. For example, `addAll` method in the `Collections` class can be added to `Collection` interface as a static method.

```java
public static<T> Stream<T> of(T... values) {
  return Arrays.stream(values);
}
```

* **default methods**: This allows users to provide default implementations to methods defined in the interface. The class implementing the interface is not required to provide implementation of the method. If implementing class provides the implementation then that implementation would be used else default implementation is used. `List` interface has few default methods defined in it like `replaceAll`, `sort`, and `splitIterator`.

```java
default void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    final ListIterator<E> li = this.listIterator();
    while (li.hasNext()) {
        li.set(operator.apply(li.next()));
    }
}
```

We can solve our API problem by defining a default method as shown below.

```java
default List<User> suggestions(String initials, Predicate<User> predicate) {
    Objects.requireNonNull(initials);
    Objects.requireNonNull(predicate);
    return suggestions(initials).stream().filter(predicate).collect(toList());
}
```

## Multiple inheritance

A class can extend a single class but can implement multiple interfaces. Now that it is feasible to have method implementations in interfaces Java has multiple inheritance of behavior. Java already has multiple inheritance at type level but now it also has multiple inheritance at behavioral level. There are three resolution rules that helps decide which method will be picked:

1. Methods declared in classes win over method defined in interfaces.
```java
interface A {
    default void doSth(){
        System.out.println("inside A");
    }
}
class App implements A{

    @Override
    public void doSth() {
        System.out.println("inside App");
    }

    public static void main(String[] args) {
        new App().doSth();
    }
}
```
This will print `inside App` as methods declared in class have precedence over methods declared in interfaces.

1. Otherwise, the most specific interface is selected
```java
interface A {
    default void doSth() {
        System.out.println("inside A");
    }
}
interface B {}
interface C extends A {
    default void doSth() {
        System.out.println("inside C");
    }
}
class App implements C, B, A {

    public static void main(String[] args) {
        new App().doSth();
    }
}
```
This will print `inside C`.

1. Otherwise, class has to call the desired implementation explicitly
```java
interface A {
    default void doSth() {
        System.out.println("inside A");
    }
}
interface B {
    default void doSth() {
        System.out.println("inside B");
    }
}
class App implements B, A {

    @Override
    public void doSth() {
        B.super.doSth();
    }

    public static void main(String[] args) {
      new App().doSth();
    }
}
```
This will print `inside B`.
