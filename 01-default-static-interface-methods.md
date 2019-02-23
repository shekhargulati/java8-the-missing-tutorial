Default and Static Methods for Interfaces [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/01-default-static-interface-methods.md)](http://ttr.myapis.xyz/)
--------

We all understand that we should code to interfaces. Interfaces give the client
a contract which they should use without relying on implementation details (i.e.
classes). Hence, promoting **[loose coupling](https://en.wikipedia.org/wiki/Loose_coupling)**.
Designing clean interfaces is one of the most important aspect of API design.
One of the SOLID principle **[Interface segregation](https://en.wikipedia.org/wiki/Interface_segregation_principle)**
talks about designing smaller client-specific interfaces, instead of designing
one general purpose interface. Interface design is the key to clean and
effective APIs for your libraries and applications.

> Code for this section is inside [ch01 package](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch01).

If you have designed any API then with time you would have felt the need to add
new methods to the API. Once an API is published, it becomes difficult to add
methods to an interface without breaking existing implementations. To make this
point clear, suppose you are building a simple `Calculator` API that supports
`add`,`subtract`, `divide`, and `multiply` operations. We can write a
`Calculator` interface, as shown below. ***To keep things simple we will use
`int`.***

```java
public interface Calculator {

    int add(int first, int second);

    int subtract(int first, int second);

    int divide(int number, int divisor);

    int multiply(int first, int second);
}
```

To back this `Calculator` interface, you created a `BasicCalculator`
implementation, as shown below.

```java
public class BasicCalculator implements Calculator {

    @Override
    public int add(int first, int second) {
        return first + second;
    }

    @Override
    public int subtract(int first, int second) {
        return first - second;
    }

    @Override
    public int divide(int number, int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("divisor can't be zero.");
        }
        return number / divisor;
    }

    @Override
    public int multiply(int first, int second) {
        return first * second;
    }
}
```

## Static Factory Methods

Suppose the Calculator API turned out to be very useful and easy to use. Users
just have to create an instance of `BasicCalculator`, and then they can use the
API. You start seeing code like that shown below.

```java
Calculator calculator = new BasicCalculator();
int sum = calculator.add(1, 2);

BasicCalculator cal = new BasicCalculator();
int difference = cal.subtract(3, 2);
```

Oh no! Users of the API are not coding to `Calculator` interface -- instead,
they are coding to its implementation. Your API didn't enforce users to code to
interfaces, as the `BasicCalculator` class was public. If you make
`BasicCalculator` package protected, then you would have to provide a static
factory class that will take care of providing the `Calculator` implementation.
Let's improve the code to handle this.

First, we will make `BasicCalculator` package protected so that users can't
access the class directly.

```java
class BasicCalculator implements Calculator {
  // rest remains same
}
```

Next, we will write a factory class that will give us the `Calculator` instance,
as shown below.

```java
public abstract class CalculatorFactory {

    public static Calculator getInstance() {
        return new BasicCalculator();
    }
}
```

Now, users will be forced to code to the `Calculator` interface, and they will
not have access to implementation details.

Although we have achieved our goal, we have increased the surface area of our
API by adding the new class `CalculatorFactory`. Now users of the API have to
learn about one more class before they can use the API effectively. This was the
only solution available before Java 8.

**Java 8 allows you to declare static methods inside an interface**. This allows
API designers to define static utility methods like `getInstance` in the
interface itself, hence keeping the API short and lean. The static methods
inside an interface could be used to replace static helper classes
(`CalculatorFactory`) that we normally create to define helper methods
associated with a type. For example, the `Collections` class is a helper class
that defines various helper methods to work with Collection and its associated
interfaces. The methods defined in the `Collections` class could easily be added
to `Collection` or any of its child interfaces.

The above code can be improved in Java 8 by adding a static `getInstance` method
in the `Calculator` interface itself.

```java
public interface Calculator {

    static Calculator getInstance() {
        return new BasicCalculator();
    }

    int add(int first, int second);

    int subtract(int first, int second);

    int divide(int number, int divisor);

    int multiply(int first, int second);

}
```

## Evolving API with time

Some of the consumers decided to either extend the `Calculator` API by adding
methods like `remainder`, or write their own implementation of the `Calculator`
interface. After talking to your users you came to know that most of them would
like to have a `remainder` method added to the `Calculator` interface. It looked
a very simple API change, so you added one more method to the API.

```java
public interface Calculator {

    static Calculator getInstance() {
        return new BasicCalculator();
    }

    int add(int first, int second);

    int subtract(int first, int second);

    int divide(int number, int divisor);

    int multiply(int first, int second);

    int remainder(int number, int divisor); // new method added to API
}
```

Adding a method to an interface broke the source compatibility of the API. This
means users who were implementing `Calculator` interface would have to add
implementation for the `remainder` method, otherwise their code would not
compile. This is a big problem for API designers, as it makes APIs difficult to
evolve. Prior to Java 8, it was not possible to have method implementations
inside interfaces. This often became a problem when it was required to extend an
API, i.e. adding one or more methods to the interface definition.

To allow API's to evolve with time, Java 8 allows users to provide default
implementations to methods defined in the interface. These are called
**default**, or **defender** methods. The class implementing the interface is not
required to provide an implementation of these methods. If an implementing class
provides the implementation, then the implementing class method implementation
will be used -- otherwise the default implementation will be used. The `List`
interface has a few default methods defined, like `replaceAll`, `sort`, and
`splitIterator`.

```java
default void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    final ListIterator<E> li = this.listIterator();
    while (li.hasNext()) {
        li.set(operator.apply(li.next()));
    }
}
```

We can solve our API problem by defining a default method, as shown below.
Default methods are usually defined using already existing methods --
`remainder` is defined using the `subtract`, `multiply`, and `divide` methods.

```java
default int remainder(int number, int divisor) {
    return subtract(number, multiply(divisor, divide(number, divisor)));
}
```

## Multiple inheritance

A class can extend a single class, but can implement multiple interfaces. Now
that it is feasible to have method implementation in interfaces, Java has
multiple inheritance of behavior. Java already had multiple inheritance at the
type level, but now it also has multiple inheritance at the behavior level.
There are three resolution rules that help decide which method will be picked:

**Rule 1: Methods declared in classes win over methods defined in interfaces.**

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

This will print `inside App`, as methods declared in the implementing class have
precedence over methods declared in interfaces.

**Rule 2: Otherwise, the most specific interface is selected**

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

**Rule 3: Otherwise, the class has to call the desired implementation
unambiguously**

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

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/01-default-static-interface-methods)](https://github.com/igrigorik/ga-beacon)
