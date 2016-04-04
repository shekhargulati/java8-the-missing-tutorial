Flujos
------

En el [capítulo 2](./02-lambdas.md), aprendimos como los lambdas pueden ayudarnos a escribir código más limpio y conciso permitiéndonos pasar comportamiento sin la necesidad de crear una clase. Los lambdas son construcciones del lenguaje muy simples que ayudan al desarrollador a expresar su intentión al vuelo usando interfaces funcionales. El poder real de los lambdas se puede experimentar cuando se diseña un API pensando en ellos p. ej. un API fluído que hace uso de interfaces funcionales (lo vimos en el [capítulo de los lambdas](./02-lambdas.md#¿Necesito escribir mis propios interfaces funcionales?)).

Una de estas APIs, que hace un uso intensivo de los lambdas, es el API de flujos (`Stream`) introducido en el JDK 8. Los flujos proporcionan un alto nivel de abstracción para expresar cálculos en las colecciones de Java de una manera declarativa similar a como SQL te ayuda en las consultas a la base de datos. Declarativa quiere decir que los desarrolladores escriben lo que quieren hacer en vez de como se haría. En este capítulo hablaremos del porque de la necesidad de un nuevo API de procesamiento de datos, de la diferencia entre una colección (`Collection`) y un flujo (`Stream`) y de como usar el API de flujos en tus aplicaciones.

> El código de esta sección está en el [paquete ch03](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch03).

## ¿Por qué necesitamos una nueva abstracción de proceso de datos?

En mi opinión, existen dos razones:

1. El API de colecciones (`Collection`) no proporciona construcciones de un mayor nivel para consultar los datos por lo que los desarrolladores se ven forzados a escribir un montón de código repetido para la mayoría de las tareas triviales.

2. Tiene un soporte del lenguaje limitado para procesar datos de la clase `Collection` en paralelo. Deja en manos del desarrollador el uso de construcciones concurrentes del lenguaje Java y el proceso eficaz y eficiente de datos en paralelo.

## El proceso de datos antes de Java 8

Observa el código que se muestra a continuación y trata de predecir que hace.

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

El código mostrado imprime todos los títulos de tareas de lectura ordenados por su longitud. Todos los programadores de Java escriben este tipo de código todos los días. Para escribir ese programa tan simple tuvimos que escribir 15 líneas de código Java. El mayor problema de ese código no es el número de líneas que un programador tiene que escribir sino que pierde la intención del programador. P. Ej. filtrar las tareas de lectura, ordenar por la longitud del título y transformarlas a una lista de cadenas.

## El proceso de datos en Java 8

El código anterior se puede simplificar usando el API de flujos de Java 8 como se muestra a continuación.

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

El código mostrado construye una tubería compuesta de múltiples operaciones de flujo que desglosamos ahora.

* **stream()** - Creaste una tubería de flujo invocando el método `stream()` en la colección origen. P. ej. `tasks` `List<Task>`.

* **filter(Predicate<T>)** - Esta operación extrae los elementos del flujo que cumplan la condición definida en el predicado. Una vez que tienes un flujo puedes invocar sobre él cero o más operaciones intermedias. La expresión lambda `task -> task.getType() == TaskType.READING` define un predicado para filtrar todas las tareas de lectura. El tipo de la expresión lambda es `java.util. function.Predicate<Task>`.

* **sorted(Comparator<T>)**: Esta operación devuelve un flujo con todos los elementos ordenados por el comparador definido por la expresión lambda. P. ej. la expresión `(t1, t2) -> t1.getTitle().length() - t2.getTitle().length()` del ejemplo anterior.

* **map(Function<T,R>)**: Esta operación devuelve un flujo tras aplicar `Function<T,R>` a cada elemento del flujo.


* **collect(toList())** - Esta operación recoge en una lista (`List`) el resultado de las operaciones realizadas en el flujo (`Stream`).

### ¿Por qué es mucho mejor el código de Java 8?

En mi opinión, el código de Java 8 es mucho mejor por los siguientes motivos:

1. El código de Java 8 refleja claramente la intención del desarrollador de filtrar, ordenar, etc.

2. Los desarrolladores expresan lo que quieren hacer en vez de como quieren hacerlo usando un nivel de abstracción mucho más alto en forma de API de flujo (`Stream`).

3. El API de flujo proporciona un lenguaje unificado para el proceso de datos. Ahora los desarrolladores tendrán un vocabulario común cuando hablen sobre el proceso de datos. Cuando dos desarrolladores hablen sobre la función filtro (`filter`) puedes estar seguro de que ambos se están refiriendo a una operación de filtrado de datos.

4. No se require duplicar código para expresar el proceso de datos. Los desarrolladores ya no tienen que escribir bucles `for` o crear colecciones temporales para almacenar los datos, todo lo proporciona el propio API.

5. Los flujos no modifican la colección original,

## ¿Qué es un flujo (`Stream`)?

Un flujo es una vista abstracta de algunos datos. Por ejemplo, un flujo puede ser una vista de una lista, de líneas de un fichero o de cualquier secuencia de elementos. El API de flujo proporciona operaciones de agragación que pueden ejecutarse secuencialmente o en paralelo. ***Una cosa que los desarrolladores deberían de tener en cuenta es que `Stream` es un nivel mucho más alto de abstracción no una estructura de datos. `Stream` no guarda tus datos.*** Los flujos son **vagos** por naturaleza y sólo se calculan cuando se accede a ellos. Esto nos permite producir flujos infinitos de datos. En Java 8 puedes escribir de manera muy sencilla un flujo que produzca identificadores únicos infinitos como se muestra a continuación.

```
public static void main(String[] args) {
    Stream<String> uuidStream = Stream.generate(() -> UUID.randomUUID().toString());
}
```

Existen varios métodos estáticos de factoría como `of`, `generate` e `iterate` en el interfaz de `Stream` que se pueden usar para crear instancias de `Stream`. El método `generate` mostrado arriba usa un proveedor (`Supplier`). Un `Supplier` es un interfaz funcional que describe una función que no tiene entrada y que produce un valor. Pasamos al método `generate` un proveedor que genera un identificador único cuando se invoca.

```java
Supplier<String> uuids = () -> UUID.randomUUID().toString()
```

Si ejecutas este programa no ocurrirá nada ya que los flujos son vagos y, mientras no se acceda a ellos, no se calculará nada. Si modificamos el programa como se muestra a continuación veremos UUIDs impresos en la consola. El programa nunca terminará.

```java
public static void main(String[] args) {
    Stream<String> uuidStream = Stream.generate(() -> UUID.randomUUID().toString());
    uuidStream.forEach(System.out::println);
}
```

Java 8 permite crear flujos a partir de una colección invocando el método `stream`. El flujo soporta operaciones de proceso de datos de manera que los desarrolladores puedan expresar cálculos usando construcciones de proceso de datos de más alto nivel.

## Collection vs Stream

La tabla que mostramos a continuación explica las diferencias entre `Collection` y `Stream`.

|Collection|Stream|
|:----------------------------------:|:----------------------------------:|
|Operaciones de lectura y escritura|Sólo operaciones de lectura. No puedes ni añadir ni eliminar elementos|
|Evaluados de forma anticipada|Evaluados de forma demorada|
|Las colecciones son datos|Los flujos son para realizar operaciones sobre datos|
|El cliente tiene que iterar sobre la colección >> iteración externa|Iteración interna|
|Puedes iterar sobre una colección varias veces|Sólo puedes procesar un flujo cada vez|

![Collection vs Stream](https://whyjava.files.wordpress.com/2015/10/collection_vs_stream.png)

Vamos a ver en detalle la iteración externa contra la iteración interna y la evaluación perezosa.

### Iteración externa contra iteración interna

La diferencia entre el código del API Stream de Java 8 y el código del API Collection mostrado anteriormente es quien controla la iteración, el iterador o el cliente que usa el iterador. Los usuarios del API Stream sólo proporcionan las operaciones que quieren usar y el iterador aplica estas operaciones a cada elemento de la colección subyacente. Cuando la iteración sobre la colección subyacente la maneja el propio iterador se denomina **iteración interna**. Por otro lado, cuando la iteración la maneja el cliente de denomina **iteración externa**. El uso de `for-each` en el código del API de Collection es un ejemplo de **iteración externa**.

Algunos podrían argumentar que en el código del API de Collection no tuvimos que usar el iterador, ya que el `for-each`, ya se preocupo de eso, pero el `for-each` no es más que un azúcar sintáctico de la iteración manual usando el API iterador. El `for-each`, aunque es muy sencillo, tiene ciertas desventajas -- 1) Es inherentemente secuencial 2) Conduce a código imperativo 3) Es complicado de paralelizar.

### Evaluación perezosa

Los flujos no se evalúan hasta que no se invoca una operación final sobre ellos. La mayoría de las operaciones del API de Stram devuelven un `Stream`. Estas operaciones no realizan ninguna ejecución sólo construyen la tubería. Vamos a observar el código siguiente y tratar de predecir su salida.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream().map(n -> n / 0).filter(n -> n % 2 == 0);
```
En el código mostrado, estamos dividiendo los elementos del flujo de números por 0. Podríamos esperar que el código lance una excepción `ArithmeticException` cuando se ejecute pero, cuando ejecutas el código, no se lanza ninguna excepción. Esto se debe a que los flujos no se evalúan hasta que no se invoca una operación final del flujo. Si añades una operación final a la tubería del flujo, entonces se ejecuta el flujo y lanza una excepción.

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream().map(n -> n / 0).filter(n -> n % 2 == 0);
stream.collect(toList());
```

Obtendrás una traza de la pila como esta.

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

## Usando el API Stream

El API Stream proporciona un montón de operaciones que los desarrolladores pueden usar para consultar datos en colecciones. Las operaciones de Stream pueden ser de dos tipos -- operación intermedia u operación final.

**Operaciones intermedias** son funciones que producen otros flujos a partir de uno existente como `filter`, `map`, `sorted`, etc.

**Operaciones terminales** son funciones que no generan un flujo como resultado `collect(toList())`, `forEach`, `count`, etc.

Las operaciones intermedias te permiten construir la tubería que se ejecutará cuando llames a una operación final. Aquí se muestran la list de funciones que son parte del API Stream.

<a href="https://whyjava.files.wordpress.com/2015/07/stream-api.png"><img class="aligncenter size-full wp-image-2983" src="https://whyjava.files.wordpress.com/2015/07/stream-api.png" alt="stream-api" height="450" /></a>

### Dominio de ejemplo

A través de este tutorial usaremos el dominio de gestión de tarea para explicar los conceptos. Nuestro dominio de ejemplo tiene una clase llamada Task (<i>Tarea</i>) -- una tarea a ser realizada por el usuario. La clase es como se muestra a continuación.

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

    // Eliminados el constructor, los getter y los setter para abreviar
}
```

A continuación listamos el conjunto de datos de ejemplo. Usaremos esta lista en nuestros ejemplos del API Stream.

```java
Task task1 = new Task("Read Version Control with Git book", TaskType.READING, LocalDate.of(2015, Month.JULY, 1)).addTag("git").addTag("reading").addTag("books");

Task task2 = new Task("Read Java 8 Lambdas book", TaskType.READING, LocalDate.of(2015, Month.JULY, 2)).addTag("java8").addTag("reading").addTag("books");

Task task3 = new Task("Write a mobile application to store my tasks", TaskType.CODING, LocalDate.of(2015, Month.JULY, 3)).addTag("coding").addTag("mobile");

Task task4 = new Task("Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2015, Month.JULY, 4)).addTag("blogging").addTag("writing").addTag("streams");

Task task5 = new Task("Read Domain Driven Design book", TaskType.READING, LocalDate.of(2015, Month.JULY, 5)).addTag("ddd").addTag("books").addTag("reading");

List<Task> tasks = Arrays.asList(task1, task2, task3, task4, task5);
```

> No trataremos el API de fecha y hora de Java 8 en este capítulo. Por ahora sólo tenlo en cuenta como el API fluído para trabajar con fechas.

### Ejemplo 1: Encuentra todas los títulos de tareas de lectura ordenadas por la fecha de creación

El primer ejemplo que trataremos será encontrar todos los títulos de tareas de lectura ordenadas por fecha de creación. Las operaciones que necesitamos realizar son:

1. Filtrar todas las tareas que tengan TaskType de READING.
2. Ordenar las tareas filtradas por el campo `createdOn`.
3. Obtener el título de cada tarea.
4. Recoger los títulos devueltos en una lista.

Las cuatro operaciones se pueden desarrollar de forma sencilla de la forma siguiente.

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

En el código mostra usamos los siguientes métodos del API Stream:

* **filter**: Te permite especificar un predicado para excluir algunos elementos del flujo actual. El predicado **task -> task.getType() == TaskType.READING** selecciona todas las tareas cuyo TaskType sea READING.

*  **sorted**: Te permite especificar un Comparator que ordenará el flujo. Es este caso, ordenamos por la fecha de creación. La expresión lambda **(t1, t2) -> t1.getCreatedOn().compareTo(t2.getCreatedOn())** proporciona la implementación del método `compare` del interfaz funcional de Comparator.

* **map**: Toma un lambda que implementa `Function<? super T, ? extends R>` el cual transforma un flujo en otro. La expresión lambda **task -> task.getTitle()** transforma una tarea en un título.

* **collect(toList())** Es una operación terminal que recoge los títulos de las tareas de lectura resultantes en una lista.

Podemos mejorar el código anterior de Java 8 usanso el método `comparing` del interfaz `Comparator` y referencias a método como se muestra a continuación.

```java
public List<String> allReadingTasks(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            sorted(Comparator.comparing(Task::getCreatedOn)).
            map(Task::getTitle).
            collect(Collectors.toList());

}
```

> A partir de Java 8, los interfaces pueden implementar métodos estáticos o métodos por defecto. Esto se ve en el [capítulo 1](./01-default-static-interface-methods.md).

En el código siguiente usamos un método de ayuda estático `comparing`, disponible en el interfaz `Comparator`, que acepta una `Function` que extrae una clave `Comparable` y devuelve un `Comparator` que compara por esa clave. La referencia al método `Task::getCreatedOn` resuelve a `Function<Task, LocalDate>`.

Usando la composición de funciones podemos escribir muy facilmente el código que invierte el orden de ordenación llamando al método `reversed()` de `Comparator` como se muestra a continuación.

```java
public List<String> allReadingTasksSortedByCreatedOnDesc(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            sorted(Comparator.comparing(Task::getCreatedOn).reversed()).
            map(Task::getTitle).
            collect(Collectors.toList());
}
```

### Ejemplo 2: Encuentra tareas distintas

Supón que tenemos un conjunto de datos que contiene tareas duplicadas. Podemos eliminar fácilmente los duplicados y quedarnos con los elementos diferentes usando el método `distinct` en el flujo.

```java
public List<Task> allDistinctTasks(List<Task> tasks) {
    return tasks.stream().distinct().collect(Collectors.toList());
}
```

El método `distinct()` convierte un flujo en otro sin duplicados. Usa el método `equals` de Object para determinar si son iguales. De acuerdo al contrato del método de Objeto `equals`, cuando dos objetos son iguales, se consideran duplicados y se eliminarán del flujo resultante.

### Ejemplo 3: Encuentra la 5 primeras tareas de lectura ordenadas por fecha de creación

La función `limit` se puede usar para limitar el conjunto de resultados a un tamaño dado. `limit` es una operación de circuito corto lo que significa que no evalúa todos los elementos para encontrar el resultado.

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

Puedes usar el método `limit` junto a `skip` como se muestra a continuación para crear paginación.

```java
// La página comienza en 0. Así que la segunda página (`page`) será 1 y la página n será n-1.
List<String> readingTaskTitles = tasks.stream().
                filter(task -> task.getType() == TaskType.READING).
                sorted(comparing(Task::getCreatedOn).reversed()).
                map(Task::getTitle).
                skip(page * n).
                limit(n).
                collect(toList());
```

### Ejemplo 4: Cuenta todas las tareas de lectura

Para obtener el total de tareas de lecturas, podemos usar el método `count` del flujo. Esta operación es final.

```java
public long countAllReadingTasks(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            count();
}
```

### Ejemplo 5: Encuentra todas las etiquetas únicas de todas las tareas

Para encontrar todas las etiquetas distintias tenemos que realizar las siguientes operaciones:

1. Extraer las etiquetas de cada tarea.
2. Recoger todas las etiquetas en un flujo.
3. Eliminar las duplicadas.
4. Y finalmente recoger el resultado en una lista.

La primera y la segunda operación se pueden realizar usando la operación `flatMap` en el flujo de `tasks`. La operación `flatMap` agrupa los flujos devueltos por cada llamada a `tasks.getTags().stream()` en un único flujo. Una vez que tenemos todas las etiquetas en un sólo flujo, basta con usar el método `distinct` para obtener todas las etiquetas distintas.

```java
private static List<String> allDistinctTags(List<Task> tasks) {
        return tasks.stream().flatMap(task -> task.getTags().stream()).distinct().collect(toList());
}
```

### Ejemplo 6: Comprueba si todas las tareas de lectura tienen la etiqueta `books`

El API Stream tiene métodos que permiten al usuario comprobar si elementos del conjunto de datos contienen una propiedad dada. Estos métodos son `allMatch`, `anyMatch`, `noneMatch`, `findFirst` y `findAny`. Para comprobar si todos los títulos leídos tienen una etiqueta con el nombre `books` podemos escribir lo siguiente.

```java
public boolean isAllReadingTasksWithTagBooks(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            allMatch(task -> task.getTags().contains("books"));
}
```

Para comprobar si alguna tarea de lectura tiene una etiqueta `java8` se puede usar la operación `anyMatch` como se muestra a continuación.

```java
public boolean isAnyReadingTasksWithTagJava8(List<Task> tasks) {
    return tasks.stream().
            filter(task -> task.getType() == TaskType.READING).
            anyMatch(task -> task.getTags().contains("java8"));
}
```

### Ejemplo 7: Crear un resumen de todos los títulos

Supón que quieres crear un resumen de todos los títulos, para eso puedes usar la operación `reduce` que reduce el flujo a un valor. La función `reduce` toma un lambda que une los elementos del flujo.

```java
public String joinAllTaskTitles(List<Task> tasks) {
    return tasks.stream().
            map(Task::getTitle).
            reduce((first, second) -> first + " *** " + second).
            get();
}
```

### Ejemplo 8: Trabajando con flujos de elementales

A parte del flujo genérico que trabaja con objetos, Java 8 también proporciona flujos específicos que trabajan con tipos elementales como int, long y double. Vamos a ver unos pocos ejemplos con flujos de elementales.

Para crear un rango de valores podemos usar el método `range` que crea un flujo con valores comenzando en el 0 y terminando en el 9. Excluye el 10.

```java
IntStream.range(0, 10).forEach(System.out::println);
```

El método `rangeClosed` te permite crear flujos que también incluyen el límite superior. Así que el flujo de antes comenzaría en 1 y terminaría en 10.

```java
IntStream.rangeClosed(1, 10).forEach(System.out::println);
```

Puedes crear flujos infinitos usando el método `iterate` en flujos de elementales.

```java
LongStream infiniteStream = LongStream.iterate(1, el -> el + 1);
```

Para filtrar todos los números pares en un flujo infinito se puede escribir lo siguient

```java
infiniteStream.filter(el -> el % 2 == 0).forEach(System.out::println);
```

Podemos limitar el flujo resultado usando la operación `limit`.

```java
infiniteStream.filter(el -> el % 2 == 0).limit(100).forEach(System.out::println);
```

### Ejemplo 9: Crear flujos a partir de matrices

Puedes crear flujos a partir de matrices usando el método estático `stream` de la clase `Arrays`.

```java
String[] tags = {"java", "git", "lambdas", "machine-learning"};
Arrays.stream(tags).map(String::toUpperCase).forEach(System.out::println);
```

También puedes crear un flujo a partir de una matriz especificando los índices inicial y final. En este caso, el índice inicial se incluye y el final no.

```java
Arrays.stream(tags, 1, 3).map(String::toUpperCase).forEach(System.out::println);
```

## Flujos paralelos

Una ventaja de usar la abstracción Stream es que la biblioteca puede controlar internamente y eficazmente el paralelismo en la iteración. Puedes construir un flujo paralelo llamando al método `parallel`. El método `parallel` usa por debajo el API fork-join introducido en el JDK 7. Por defecto, creará tantos hilos como cpus tenga tu máquina. En el siguiente código, estamos agrupando números por hilos que los procesan. Aprenderás sobre las funciones `collect` y `groupingBy` en el capítulo 4. Por ahora, quedate con que te permiten agrupar elementos basados en una clave.

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

La salida del código anterior, en mi máquina, es la siguiente.

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

Cada hilo no procesa el mismo número de elementos. Puedes controlar el tamaño del agrupamiento (`pool`) configurando una propiedad del sistema `System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "2")`.

Otro ejemplo donde puede usar la operación `parallel` es cuando estás procesando una listsa de URLs.

```java
String[] urls = {"https://www.google.co.in/", "https://twitter.com/", "http://www.facebook.com/"};
Arrays.stream(urls).parallel().map(url -> getUrlContent(url)).forEach(System.out::println);
```

Si necesitas entender cuando usar flujos paralelos, te recomendaría leer este artículo de Doug Lea y otros compañeros de Java [http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html](http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html) para comprenderlo mucho mejor.

[![Analytics](https://ga-beacon.appspot.com/UA-74043032-1/malobato/java8-the-missing-tutorial/03-streams)](https://github.com/igrigorik/ga-beacon)
