Lambdas
-----

Una de las características más importantes de Java 8 es la introducción de las expresiones lambda. Hacen tu código conciso y te permite pasar del comportamiento. Desde hace algún tiempo, se ha criticado a Java por ser verboso y carecer de capacidades de programación funcional. Con la programación funcional volviendose más popular y relevante, se le ha forzado a adoptar el estilo funcional de programación, de otra forma, llegaría a ser irrelevante.

Java 8 es un gran paso para hacer que el lenguaje más popular del mundo adopte el estilo funcional de programación. Para soportar el estilo de programación funcional, un lenguage necesita soportar funciones como entidades de primer nivel, es decir, poder pasar funciones como parámetros a otras funciones. Antes de Java 8, escribir código de estilo funcional limpio no era posible sin el uso repetitivo de una clase interna (<i>inner class</i>) anónima. Con la introducción de las expresiones lambda, las funciones se han convertido en entidades de primer nivel y se pueden pasar como cualquier otra variable.

Las expresiones lambda te permiten definir una función anónima que no este ligada a un identificador. Puedes usarlas como cualquier otro constructor en tu lenguaje de programación como una declaración de variable. Las expresiones lambda son necesarias si el lenguaje de programación necesita soportar funciones de alto nivel. Las funciones de alto nivel son funciones que, o bien aceptan a otras funciones como argumento, o bien devuelven una función como resultado.

> El código para esta sección está en el [paquete ch02](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch02).

Ahora, con la introducción de las expresiones lambda en Java 8, Java soporta funciones de orden superior. Vamos a ver el ejemplo por excelencia de la expresión Lambda -- una función de ordenación en la clase de Java `Collections`. La función `sort` tiene dos variantes -- una que toma una lista (`List`) como parámetro y la otra que toma una lista (`List`) y un comparador (`Comparator`). La segunda función `sort` es un ejemplo de una función de alto nivel que acepta una expresión lambda como se muestra en el trozo de código de abajo.

```java
List<String> names = Arrays.asList("shekhar", "rahul", "sameer");
Collections.sort(names, (first, second) -> first.length() - second.length());
```

El código mostrado ordena los `nombres` por su longitud. La salida del programa será como se muestra abajo.

```
[rahul, sameer, shekhar]
```

La expresión `(first, second) -> first.length() - second.length()` es una expresión lambda del tipo `Comparator<String>`.

* `(first, second)` son parámetros del método `compare` de `Comparator`.
* `first.length() - second.length()` es el cuerpo de la función que compara la longitud de los dos nombres.
* `->` es el operador lambda que separa los parámetros del cuerpo del lambda.

Antes de profundizar en el soporte de lambdas de Java 8, vamos a mirar en su historia para entender el porqué de su existencia.

## Historia de los Lambdas

Las expresiones lambda tienen sus orígenes en el Cálculo Lambda. [Lambda calculus](https://en.wikipedia.org/wiki/Lambda_calculus) surgido del trabajo de [Alonzo Church](https://en.wikipedia.org/wiki/Alonzo_Church) sobre formalizar el concepto de cálculo de expresiones con funciones. El cálculo lambda es un camino matemático formal completo de turing para expresar cálculos. Completo de turing quiere decir que puede expresar cualquier cálculo matemático con lambdas.

El cálculo lambda llego a ser la base de fundamentos teóricos fuertes de lenguajes de programación funcional. Muchos lenguajes de programación funcional populares como Haskell o Lisp se basan en el cálculo lambda. La idea de funciones de orden superior, por ejemplo, una función aceptando otras funciones como parámetros, viene del cálculo lambda.

El concepto principal en el cálculo lambda es la expresión. Una expresión se puede expresar como:

```
<expresión> := <variable> | <función>| <aplicación>
```

* **variable** -- Una variable es un marcador de posición como x, y, z para valores como 1, 2, etc o funciones lambda.

* **función** -- Es una definición de función anónima que toma una variable y produce otra expresión lambda. Por ejemplo, `λx.x*x` es una función para calcular el cuadrado de un número.

* **aplicación** -- Es el acto de aplicar una función a un argumento. Supón que quieres obtener el cuadrado de 10, en cálculo lambda escribirías una función cuadrado `λx.x*x` y la aplicarías a 10. Esta aplicación de una función daría como resultado en `(λx.x*x) 10 = 10*10 = 100`. No sólo puedes aplicar valores simplea como 10 sino que puedes aplicar una función a otra función para producir otra función. Por ejemplo, `(λx.x*x) (λz.z+10)` producirá una función `λz.(z+10)*(z+10)`. Ahora puedes usar esta función para producir cuadrados de un número más 10. Esto es un ejemplo de funciones de orden superior.

Ahora que entiendes el cálculo lambda y su impacto en los lenguajes de programación funcional vamos a aprender como se implementan en Java 8.

## Pasar el comportamiento antes de Java 8

Antes de Java 8, la única forma de pasar el comportamiento era usando clases anónimas. Supón que quieres enviar un email en otro hilo después del registro de un usuario. Antes de Java 8 esribirías código como el que se muestra abajo.

```java
sendEmail(new Runnable() {
            @Override
            public void run() {
                System.out.println("Enviando email...");
            }
        });
```

El método sendEmail tiene la siguiente firma.

```java
public static void sendEmail(Runnable runnable)
```

El problema con el código de arriba no es sólo que tenemos que encapsular nuestra acción p. ej. el método `run` en un objeto, sino que el mayor problema es que pierde la intención del programador, p. ej. pasar el comportamiento a la función `sendEmail`. Si has usado bibliotecas como Guava, habrás sufrido de verdad el dolor de escribir clases anónimas. A continuación se muestra un sencillo ejemplo de como filtrar todas las tareas con **lambda** en su título.

```java
Iterable<Task> lambdaTasks = Iterables.filter(tasks, new Predicate<Task>() {
            @Override
            public boolean apply(Task task) {
                return input.getTitle().contains("lambda");
            }
});
```
Con el API Stream de Java 8, puedes escribir el código mostrado arriba sin el uso de bibliotecas de terceros como Guava. Veremos los streams in [Capítulo 3](./03-streams.md), así que, ¡permanece atento!

## Expresiones lambda en Java 8

En Java 8, podríamos escribir el código usando una expresión lambda como se muestra abajo. Es el mismo ejemplo usado en el código de arriba.

```java
sendEmail(() -> System.out.println("Enviando email..."));
```

El código mostrado es conciso y no contamina la intención del programador de pasar el comportamiento. `()` se usa para representar la ausencia de parámetros en la función p. ej. el método `run` del interfaz `Runnable` no tiene ningún parámetro. `->` es el operador lambda que separa los parámetros del cuerpo de la función que imprime `Enviando email...` por la salida estándar.

Vamos a observar de nuevo el ejemplo Collections.sort para poder entender como funcionan las expresiones lambda con parámetros. Pasamos `Comparator` a la función de ordenación para ordenar una lista de nombres por su longitud.

```java
Comparator<String> comparator = (first, second) -> first.length() - second.length();
```

La expresión lambda que escribimos se correspondía al método `compare` del interfaz Comparator. La firma de la función `compare`se muestra a continuación.

```java
int compare(T o1, T o2);
```

`T` es el tipo de parámetro pasado al interfaz `Comparator`. En este caso será un `String` ya que estamos trabajando con una lista de `String` p. ej. names.

En la expresión lambda no tuvimos que poner el tipo explicitamente -- String, el compilador `javac` lo dedujo del contexto. El compilador de Java dedujo que ambos parámetros deberían ser String ya que estamos ordenando una lista de String y el método `compare` sólo usa un tipo T. El acto de deducir el tipo del contexto se llama **Type Inference** (inferencia de tipo). Java 8 mejora el sistema de inferencia de tipos ya existente y lo hace más robusto y potente para soportar expresiones lambda. El compilador `Javac` busca internamente la información cerca de tu expresión lambda y la usa para encontrar el tipo correcto de los parámetros.

> En la mayoría de los casos, `javac` deducirá el tipo del contexto. En caso de que no pueda resolver el tipo debido a una perdida de contexto o a un contexto incompleto el código no compilará. Por ejemplo, si quitamos la información del tipo `String` de `Comparator`, el código fallará al compilar como se muestra a continuación.

```java
Comparator comparator = (first, second) -> first.length() - second.length(); // Error de compilación - Cannot resolve method 'length()'
```

## ¿Cómo funcionan las expresiones lambda en Java 8?

Puedes haber notado que el tipo de la expresión lambda es algún interfaz, como `Comparator` en el ejemplo anterior. No puedes usar cualquier interfaz con una expresión lambda. ***Sólo aquellos interfaces con un único método abstracto se pueden usar con expresiones lambda***. Estas clases de interfaces se llaman **Interfaces funcionales** y se pueden anotar con la anotación `@FunctionalInterface`. El interfaz `Runnable` es un ejemplo de intefaz funcional como se muestra abajo.

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

La anotación `@FuncionalInterface` no es obligatoria pero puede ayudar a las herramientas a saber que un interfaz es un interfaz funcional y realizar acciones significativas. Por ejemplo, si tratas de compilar un interfaz anotado con `@FunctionalInterface` y tiene varios métodos abstractos la compilación fallará con un error ***Se han encontrado varios métodos abstractos no anulables***. Igualmente, si añades la anotación `@FunctionaInterface` a una interfaz sin ningún método, p. ej. un interfaz marcador, obtendrás el mensaje de error ***No se encuentra un método objetivo***.

Vamos a responder a una de las preguntas más importantes que te pueden venir a la mente, ***¿Son las expresiones lambda simplemente el azucar sintáctico sobre las clases internas anónimas o cómo se traducen a bytecode los interfaces funcionales?***.

La respuesta corta es **NO**, Java 8 no usa las clases internas anónimas principalmente por dos motivos:

1. **Impacto en el rendimiento**: Si las expresiones lambda fuesen implementadas mediante clases anónimas entonces tendríamos un fichero en disco por cada expresión lambda. Como estas clases se cargan en el arranque, el tiempo de arranque de la JVM se incrementaría, ya que todas las clases necesitan ser cargadas y verificadas antes de poder usarlas.

2. **Posibilidad de cambiar en el futuro**: Si los diseñadores de Java 8 hubieran usado clases anónimas desde el principio, habrían limitado el ámbito de los futuros cambios en la implementación lambda.

### El uso de invokedynamic

Los diseñadores de Java 8 tomaron la decisión de usar la instrucción añadida en Java 7 `invokedynamic` para posponer la estrategia de traducción en tiempo de ejecución. Cuando `javac` compila el código, captura la expresión lambda y genera un lugar de llamada `invokedynamic` (llamado factoría lambda). Cuando se invoca el lugar de llamada `invokedynamic`, devuelve  una instancia del interfaz funcional al que se convierte el lambda. Por ejemplo, si observamos el código byte de nuestro ejemplo Collections.sort, se mostrará como aparece abajo.

```
public static void main(java.lang.String[]);
    Code:
       0: iconst_3
       1: anewarray     #2                  // Clase java/lang/String
       4: dup
       5: iconst_0
       6: ldc           #3                  // Cadena shekhar
       8: aastore
       9: dup
      10: iconst_1
      11: ldc           #4                  // Cadena rahul
      13: aastore
      14: dup
      15: iconst_2
      16: ldc           #5                  // Cadena sameer
      18: aastore
      19: invokestatic  #6                  // Método java/util/Arrays.asList:([Ljava/lang/Object;)Ljava/util/List;
      22: astore_1
      23: invokedynamic #7,  0              // InvokeDynamic #0:compare:()Ljava/util/Comparator;
      28: astore_2
      29: aload_1
      30: aload_2
      31: invokestatic  #8                  // Método java/util/Collections.sort:(Ljava/util/List;Ljava/util/Comparator;)V
      34: getstatic     #9                  // Campo java/lang/System.out:Ljava/io/PrintStream;
      37: aload_1
      38: invokevirtual #10                 // Método java/io/PrintStream.println:(Ljava/lang/Object;)V
      41: return
}
```

La parte interesante del código byte mostrado arriba está en la línea 23 `23: invokedynamic #7,  0              // InvokeDynamic #0:compare:()Ljava/util/Comparator;` donde se hace una llamada a  `invokedynamic`.

El segundo paso es convertir el cuerpo de la expresión lambda en un método que se invocará a través de la instrucción `invokeDynamic`. Este es el paso donde los que implementan la JVM tienen la libertad de elegir su propia estrategia.

Sólo he pasado por alto este tema. Puedes leer sobre el mismo en http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html.

## Clases anónimas vs lambdas

Vamos a comparar las clases anónimas con los lambdas para entender las diferencias entre ellos.

1. En las clases anónimas, `this` hace referencia a la propia clase anónima mientras que en una expresión lambda hace referencia a la clase que encierra a la expresión lambda.

2. Puedes esconder variables en la clase contenedora dentro de la clase anónima. Esto da un error en tiempo de compilación cuando lo haces dentro de una expresión lambda.

3. El tipo de la expresión lambda se determina desde el contexto donde el tipo de la clase anónima se especifica explicitamente cuando creas la instancia de la clase anónima.

## ¿Necesito escribir mis propios interfaces funcionales?

Por defecto, Java 8 incluye muchos interfaces funcionales que puedes usar en tu código. Están incluídos dentro del paquete `java.util.function`. Vamos a echarle un vistazo a alguno de ellos.

### java.util.function.Predicate<T>

Este interfaz funcional se usa para definir una comprobación de alguna condición, p. ej. un predicado. El interfaz `Predicate` tiene un método llamado `test` que toma un valor de tipo `T` y devuelve un booleano. Por ejemplo, si queremos filtrar todos los nombres que comienzen con **s** de una lista de nombres usaremos un predicado como se muestra a continuación.

```java
Predicate<String> namesStartingWithS = name -> name.startsWith("s");
```

### java.util.function.Consumer<T>

Este interfaz funcional se usa para realizar acciones que no producen ninguna salida. El interfaz Consumer tiene un método llamado `accept` que toma una valor de tipo `T` y no devuelve nada, p. ej. Está vacío. Por ejemplo, enciar un email con un mensaje dado.

```java
Consumer<String> messageConsumer = message -> System.out.println(message);
```

### java.util.function.Function<T,R>


Este interfaz funcional toma un valor y produce un resultado. Por ejemplo, si queremos poner en mayúsculas todos los nombres en nuestra lista `names`, podemos escribir una función como esta. 

```java
Function<String, String> toUpperCase = name -> name.toUpperCase();
```

### java.util.function.Supplier<T>

Este interfaz funcional no toma ninguna entrada pero produce un valor. Se podría usar para generar identificadores únicos como se muestra abajo.

```java
Supplier<String> uuidGenerator= () -> UUID.randomUUID().toString();
```

Veremos más interfaces funcionales a lo largo del tutorial.

## Referencias de métodos

Habrá veces que crearás expresiones lambda que sólo llames a un método específico como `Function<String, Integer> strToLength = str -> str.length();`.El lambda sólo llama al método `length()` del objeto `String`. Esto se podría simplificar usando referencias de métodos com `Function<String, Integer> strToLength = String::length;`. Se puede ver como una notación abreviada de la expresión abreviada que sólo llama a un método. En la expresión `String::length`, `String` es la referencia destino, `::` es el delimitador y `length` es la función que se invocará en la referencia destino. Puedes usar referencias de métodos tanto en métodos estáticos como en métodos de instancias.

### Referencias de métodos estáticos

Supón que tenemos que encontrar el número máximo de una lista de números, podemos escribir una referencia de método `Function<List<Integer>, Integer> maxFn = Collections::max`. `max` es un método estático de la clase `Collections` que toma un argumento de tipo `List`. Puedes invocarlo de la siguiente forma `maxFn.apply(Arrays.asList(1, 10, 3, 5))`. La expresión lambda de arriba es equivalente a esta otra `Function<List<Integer>, Integer> maxFn = (numbers) -> Collections.max(numbers);`

### Referencias de métodos de instancia

Se usa en referencias de métodos a un método de instancia, por ejemplo `String::toUpperCase` llama al método `toUpperCase` en una referencia a `String`. También puedes usar un método de referencia con parámetros, por ejemplo `BiFunction<String, String, String> concatFn = String::concat`. `concatFn` se puede invocar con `concatFn.apply("shekhar", "gulati")`. El método `concat` de `String` se invoca sobre un objeto `String` y se pasa como parámetro `"shekhar".concat("gulati")`.

## Ejercicio >> Lambdificame

Vamos a ver el código que se muestra a continuación y aplicar lo que hemos aprendido hasta ahora.

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

El código anterior primero recupera todas las tareas, `Tasks`, con el método `GetTasks`. No nos interesa la implementación de `getTasks`, este método nos devolvería las tareas accediento a un servicio web, a una base de datos o a la memoria. Una vez que tenemos las tareas, las filtraremos y extraeremos el campo título, `title` de cada una. Añadiremos el título extraído a una lista para, finalmente, devolver todos los titulos leídos.

Vamos a empreza con la refactorización más simple -- usar `forEach` en una lista con una referencia de método.

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

Usaremos `Predicate<T>` para filtrar las tareas.

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

Con `Function<T,R>` extraeremos el título de la tarea.

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

Usar una referencia de método para el extractor.

```java
public static void main(String[] args) {
    List<Task> tasks = getTasks();
    List<String> titles = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, Task::getTitle);
    titles.forEach(System.out::println);
    List<LocalDate> createdOnDates = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, Task::getCreatedOn);
    createdOnDates.forEach(System.out::println);
    List<Task> filteredTasks = filterAndExtract(tasks, task -> task.getType() == TaskType.READING, Function.identity());
    filteredTasks.forEach(System.out::println);
}
```

También podemos escribir nuestro propio **Interfaz Funcional** que le indique claramente al lector la intención del desarrollador. Podemos crear un interfaz `TaskExtractor` que extienda el interfaz `Function`. El tipo de la entrada se establece a `Task` y el tipo de la salida depende de la implementación del lambda. De esta forma el desarrollador sólo tendrá que preocuparse del tipo del resultado ya que el tipo de la entrada siempre será `Task`.

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
[![Analytics](https://ga-beacon.appspot.com/UA-74043032-1/malobato/java8-the-missing-tutorial/02-lambdas)](https://github.com/igrigorik/ga-beacon)
