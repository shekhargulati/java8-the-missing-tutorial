Métodos Estáticos y por defecto en Interfaces
--------

Todos entendemos que deberíamos programar con interfaces. Los interfaces dan al cliente un contrato que deberían usar sin preocuparse en los detalles de la implementación ( p.e. las clases). Por lo tanto, fomentan **[el bajo acoplamiento](https://en.wikipedia.org/wiki/Loose_coupling)**. Diseñar interfaces limpios es uno de los aspectgos más importantos en el diseño de APIs. Uno de los principios SOLID **[Segregación de interfaces](https://en.wikipedia.org/wiki/Interface_segregation_principle)** habla sobre diseñar interfaces específicos para el cliente más pequeños en vez de un interfaz más genérico. El diseño de interfaces es la clave para tener APIs limpios y efectivos para nuestas librerías y aplicaciones.

> El código de esta sección está en [ch01 package](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch01).

Si has diseñado algún API, con el tiempo, habrás sentido la necesidad de añadirle nuevos métodos. Una vez que se publica el API se hace imposible añadir métodos a un interfaz sin romper las implementaciones existentes. Para aclarar este punto vamos a suponer que estamos desarrollando un API sencillo de una calculadora `Calculator` que soporta las operaciones de sumar `add`, restar `subtract`, dividir `divide` y multiplicar `multiply`. Podemos escribir el interfaz `Calculator`como se muestra abajo. ***Para hacerlo sencillo usaremos enteros.***

```java
public interface Calculator {

    int add(int first, int second);

    int subtract(int first, int second);

    int divide(int number, int divisor);

    int multiply(int first, int second);
}
```

Para respaldar este interfaz `Calculator` desarrollaste la implementación de `BasicCalculator` como se muestra abajo.

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
            throw new IllegalArgumentException("El divisor no puede ser cero.");
        }
        return number / divisor;
    }

    @Override
    public int multiply(int first, int second) {
        return first * second;
    }
}
```

## Métodos de Factoría Estáticos

El API calculadora resultó ser muy útil y fácil de usar. Los usuarios sólo tienen que crear una instancia de `BasicCalculator` y ya pueden usar el API. Comienzas a ver código como el que se muestra abajo.

```java
Calculator calculator = new BasicCalculator();
int sum = calculator.add(1, 2);

BasicCalculator cal = new BasicCalculator();
int difference = cal.subtract(3, 2);
```

¡Vaya! Los usuarios del API están usando la implementación del API en vez de su interfaz `Calculator`. Como la clase `BasicCalculator` era pública, tu API no obligaba a los usuarios a usar los interfaces. Si haces tu paquete `BasicCalculator` protegido tendrías que ofrecer una clase factoría estática que se dedicará a proveer la implementación de `Calculator`. Vamos a mejorar el código para manejar esto.

Primero, haremos el paquete `BasicCalculator` protegido así los usuarios no podrán acceder a la clase directamente.

```java
class BasicCalculator implements Calculator {
  // El resto permanece igual
}
```

Luego, escribiremos una clase factoría que nos  facilite la instancia de `Calculator` como se muestra abajo.

```java
public abstract class CalculatorFactory {

    public static Calculator getInstance() {
        return new BasicCalculator();
    }
}
```

Ahora, los usuarios se verán obligados a usar el interfaz `Calculator` y no tendrán acceso a los detalles de la implementación.

Aunque hemos logrado nuestra meta hemos aumentado el tamaño de nuestra API añadiendo una nueva clase `CalculatorFactory`. Ahora los usuarios del API tendrán que aprender una clase más antes de usar eficazmente nuestro API. Esta era la única solución disponible antes de Java 8.

**Java 8 te permite declarar métodos estáticos dentro de un interfaz**. Esto permitirá a los diseñadores de APIs definir métodos de utilidad estáticos como `getInstance` en el propio interfaz y, por lo tanto, mantener el API sencillo y corto. Los métodos estáticos dentro de un interfaz se podrían usar para sustituir las clases de asistencia <i>helpers</i> estáticas (`CalculatorFactory`) que normalmente creamos para definir métodos de ayuda asociados a un tipo. Por ejemplo, la clase `Collections` es una clase de ayuda que define varios métodos de asistencia para trabajar con colecciones e interfaces asociadas. Los métodos definidos en la clase `Collections` podrían ser añadidos facilmente a `Collection` o a cualquiera de sus interfaces hijos.

El código de abajo se puede mejorar en Java 8 añadiendo un método estático `getInstance` en el propio interfaz `Calculator`.

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

## La Evolución del API con el tiempo

Algunos de los consumidores decidieron o bien, ampliar el API `Calculator` añadiendo métodos como resto `remainder`, o escribit su propia implementación del interfaz `Calculator`. Tras hablar con tus usuarios sacaste la conclusión de que a la mayoría de ellos les gustaría tener un método `remainder` en el interfaz `Calculator`. Parecía un cambio muy simple al API por lo que añadiste un método nuevo.

```java
public interface Calculator {

    static Calculator getInstance() {
        return new BasicCalculator();
    }

    int add(int first, int second);

    int subtract(int first, int second);

    int divide(int number, int divisor);

    int multiply(int first, int second);

    int remainder(int number, int divisor); // Nuevo método añadido al API
}
```

Añadir un método rompió la compatibilidad origen del API. Esto quiere decir que los usuarios que implementaron el interfaz `Calculator` tendrían que añadir el método `remainder` o su código no compilará. Este es un grave problema para los diseñadores de APIs ya que hace que la evolución de los mismos sea complicada. Antes de Java 8, no era posible tener implementación de métodos en los interfaces, lo que es un problema cuando se requiere ampliar un API, p.e. añadiendo uno o más métodos a la definición de la interfaz.

Para permitir a los APIs evolucionar con el tiempo, Java 9 permite a los usuarios proporcionar implementaciones por defecto a métodos definidos en el interfaz. Estos se llaman métodos por defecto **<i>default</i>** o de defensa **<i>defender</i>**. La clase que implementa el interfaz no necesita proporcionar implementación para estos métodos. Si la clase que implementa el interfaz proporciona la implementación del método entonces se usará esta en vez de la implementación por defecto del interfaz. El interfaz `List` tiene definidos algunos métodos por defecto como `replaceAll`, `sort` y `splitIterator`.

```java
default void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    final ListIterator<E> li = this.listIterator();
    while (li.hasNext()) {
        li.set(operator.apply(li.next()));
    }
}
```

Podemos resolver nuestro problema del API definiendo un método por defecto como se muestra abajo. Los métodos por defecto se definen normalmente usando métodos ya existentes -- `remainder` se define usando los métodos `subtract`, `multiply` y `divide`.

```java
default int remainder(int number, int divisor) {
    return subtract(number, multiply(divisor, divide(number, divisor)));
}
```

## Herencia múltiple

Una clase puede extender sólo una clase pero puede implementar múltiples interfaces. Ahora que es posible tener implementación de métodos en interfaces Java tiene herencia múltiple de comportamiento. Java ya tenía herencia múltiple a nivel de tipo y ahora tambén a nivel de comportamiento. Existen tres reglas de resolución que ayudan a decidir que método será elegido:

**Regla 1: Los métodos declarados en las clases tendrán preferencia sobre los definidos en las interfaces.**

```java
interface A {
    default void doSth(){
        System.out.println("Dentro de A");
    }
}

class App implements A{

    @Override
    public void doSth() {
        System.out.println("Dentro de App");
    }

    public static void main(String[] args) {
        new App().doSth();
    }
}
```

Esto imprimirá `Dento de App` ya que los métodos declarados en la clase tienen prioridad sobre los métodos declarados en el interfaz.

**Regla 2: En otro caso, se eligirá el interfaz más específico**

```java
interface A {
    default void doSth() {
        System.out.println("Dentro de A");
    }
}

interface B {}

interface C extends A {
    default void doSth() {
        System.out.println("Dentro de C");
    }
}

class App implements C, B, A {

    public static void main(String[] args) {
        new App().doSth();
    }
}
```

Esto imprimirá `Dentro de C`.

**Regla 3: Sino, la clase tiene que llamar explicitamente a la implementación que desea**

```java
interface A {
    default void doSth() {
        System.out.println("Dentro de A");
    }
}

interface B {
    default void doSth() {
        System.out.println("Dentro de B");
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
Esto imprimirá `Dentro de B`.

[![Analytics](https://ga-beacon.appspot.com/UA-74043032-1/malobato/java8-the-missing-tutorial/01-default-static-interface-methods)](https://github.com/igrigorik/ga-beacon)
