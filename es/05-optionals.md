Optionals
----

Cada programador de Java, ya sea principiante, novato o experto, ha experimentado en su vida un `NullPointerException`. Es un hecho que ningún programador de Java puede negar. Todos hemos malgastado o perdido muchas horas tratando de corregir errores debidos a un `NullPointerException`. De acuerdo al JavaDoc de `NullPointerException`, ***Una excepción NullPointerException se arroja cuando una aplicación intenta usar un nulo en vez de un objeto.*** Esto quiere decir que si invocamos un método o intentamos acceder a una propiedad sobre una referencia ***nula***, nuestro código reventará y se lanzará un `NullPointerException`. En este capítulo, aprenderás como escribir código libre de nulos usando el `Optional` de Java 8.

> Como comentario, si miras en el JavaDoc de `NullPointerException` encontrarás que el autor de esta excepción está ***sin atribuir***. Si se usa, el autor es desconocido o está sin atribuir, quiere decir que nadie quiere responsabilizarse del `NullPointerException` ;).


## ¿Qué son las referencias nulas?

En el 2009 en la conferencia QCon ***[Sir Tony Hoare](https://en.wikipedia.org/wiki/Tony_Hoare)***
declaró que él inventó el tipo de referencia nulo mientras diseñaba el lenguaje de programación ***ALGOL W***. El nulo fue diseñado para indicar la ausencia de un valor. Designó a las *referencias nulas* como *el error del billón de dolares*. Puedes ver el video completo de su presentación en [Infoq](http://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare).

La mayoría de los lenguajes de programación, como C, C++, C#, Java, Scala, etc, tienen tipos nulos como parte de su sistema de tipado lo que permite establecer como valor un **Nulo** en vez de otros posibles valores de tipos de datos.

## ¿Por qué las referencias nulas son malas?

Vamos a ver el siguiente ejemplo sobre las clases del dominio de gestión de tareas. Nuestro modelo de dominio es muy sencillo, sólo tiene dos clases: Task y User. Una tarea se puede asignar a un usuario.

> El código de esta sección está en el [paquete ch05](https://github.com/shekhargulati/java8-the-missing-tutorial/tree/master/code/src/main/java/com/shekhargulati/java8_tutorial/ch05).

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

Dado el modelo de dominio anterior, si tenemos que encontrar al usuario asignado a una tarea con identificador `taskId` tendríamos que escribir el siguiente código.

```java
public String taskAssignedTo(String taskId) {
  return taskRepository.find(taskId).getAssignedTo().getUsername();
}
```

El problema más grave del código mostrado es que la ausencia de un valor no es visible en el API. P. ej. si `task` no está asignada a algún usuario el código lanzará la excepción `NullPointerException` cuando se invoque a `getAssignedTo()`. Y `taskRepository.find(taskId)` también podría devolver un `null`. Esto fuerza a los clientes del API a programar de manera defensiva y comprobar los nulos como se muestra a continuación.

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

El código mostrado omite la intención del desarrollador y aumenta el código con comprobaciones `if-null`. El programador, de algún modo, quería usar tipos de datos opcionales pero se vió forzado a escribir comprobaciones `if-null`. Estoy seguro de que has escrito este tipo de código en tu día a día como programador.

## Patrón de Objeto Nulo

Una solución típica para trabajar con referencias `null` es usar [el patrón de Objeto Nulo](https://en.wikipedia.org/wiki/Null_Object_pattern). La idea detrás de este patrón es muy simple, en vez de devolver un nulo, deberías de devolver un objeto nulo que implemente tu interfaz o tu clase. De este modo puedes crear un `NullUser` como se muestra a continuación.

```java
public class NullUser extends User {

    public NullUser(String username) {
        super("NotAssigned");
    }
}
```

Ahora sí que podríamos devolver un `NullUser` cuando no haya asignado un usuario a una tarea. Podemos cambiar el método `getAssignedTo` para que devuelva un `NullUser` cuando no haya asignado un usuario a la tarea.

```java
public User getAssignedTo() {
  return assignedTo == null ? NullUser.getInstance() : assignedTo;
}
```

Ahora el código del cliente se puede simplificar para que no compruebe los nulos como sigue. En este ejemplo, no tiene sentido usar el patrón Objeto Nulo en `Task`, ya que la ausencia de la tarea en el repositorio sería una situación de excepción. Además, al añadir `TaskNotFoundException` en la sección de `throws`, hemos hecho explícito para el cliente que el código puede arrojar una excepción.

```java
public String taskAssignedTo(String taskId) throws TaskNotFoundException {
        Task task = taskRepository.find(taskId);
        if (task != null) {
            return task.getAssignedTo().getUsername();
        }
        throw new TaskNotFoundException(String.format("No task exist with id '%s'", taskId));
}
```

## Java 8 -- Presentación del tipo de dato Optional

Java 8 presenta un nuevo tipo de dato ***java.util.Optional<T>*** que encapsula un valor vacío. Aclara la intención del API. Si una función devuelve un valor de tipo Optional<T> le dice al cliente que podría no devolver un valor. Cuando usas el tipo `Optional`, como desarrollador, haces visible a través del sistema de tipos que el valor puede no estar presente y el cliente puede trabajar con él limpiamente. El propósito de usar el tipo `Optional` es ayudar a los diseñadores de APIs a hacer visible a sus clientes de forma explícita si deberían de esperar un valor opcional o no mirando la firma del método.

Vamos a actualizar nuestro modelo de dominio para reflejar valores opcionales.

```java
import java.util.Optional;

public class Task {
    private final String title;
    private final Optional<User> assignedTo;
    private final String id;

    public Task(String id, String title) {
        this.id = id;
        this.title = title;
        assignedTo = Optional.empty();
    }

    public Task(String id, String title, User assignedTo) {
        this.id = id;
        this.title = title;
        this.assignedTo = Optional.ofNullable(assignedTo);
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
    private final Optional<String> address;

    public User(String username) {
        this.username = username;
        this.address = Optional.empty();
    }

    public User(String username, String address) {
        this.username = username;
        this.address = Optional.ofNullable(address);
    }

    public String getUsername() {
        return username;
    }

    public Optional<String> getAddress() {
        return address;
    }
}
```

El uso del tipo de dato `Optional` en el modelo de datos hace explícito que `Task` referencia a un ***Optional<User>*** y también que `User` tiene una dirección **Optional<String>**. Ahora quien quiera tratar de trabajar con `assignedTo()` debería saber que podría no estar presente y lo podría controlar de manera declarativa. Hablaremos de los métodos `Optional.empty()` y `Optional.of()` en la próxima sección.

## Trabajando con métodos de creación en el API java.util.Optional

En el modelo de dominio mostrado arriba, usamos un par de métodos de creación de la clase `Optional` pero no hablé sobre ellos. Ahora vamos a hablar sobre tres métodos de creación que forman parte del API `Optional`.

* **Optional.empty()**: Se usa para crear un `Optional` cuando no existe un valor, como hicimos arriba en el constructor `this.assignedTo = Optional.empty();`.

* **Optional.of(T value)**: Se usa para crear un `Optional` a partir de un valor no nulo. Lanza una excepción `NullPointerException` si el valor es nulo. Lo usamos anteriormente en `this.address = Optional.of(address);`.

* **Optional.ofNullable(T value)**: Este método estático de factoría funciona tanto para valores nulos como no nulos. Para valores nulos creará un `Optional` vacío y para valores no nulos creará un `Optional` con el valor.

Abajo mostramos en ejemplo sencillo de como puedes escribir un API usando `Optional`.

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

## Usando valores Optional

Se puede pensar en `Optional` como un flujo de un único elemento. Tiene métodos similares al API `Stream` como map, filter, flatMap que se pueden usar para trabajar con los valores contenidos en el `Optional`.

### Obtener el título de una tarea

Para leer el valor del título de una tarea escribiríamos el siguiente código. La función `map()` se usa para transformar de `Optional<Task>` a `Optional<String>`. El método `orElseThrow()` se usa para lanzar una excepción personalizada de negocio cuando no se encuentra la tarea.

```java
public String taskTitle(String taskId) {
    return taskRepository.
        find(taskId).
        map(Task::getTitle).
        orElseThrow(() -> new TaskNotFoundException(String.format("No task exist for id '%s'",taskId)));
}
```

Existen tres variantes del método `orElse()`:

1. **orElse(T t)**: Se usa para devolver un valor cuando exista o el valor que se le pasa como parámetro. P. ej. `Optional.ofNullable(null).orElse("NoValue")` devolverá `NoValue` ya que no existe el valor.

2. **orElseGet**: Devolverá el valor si está presente, y si no generará un nuevo valor resultado de invocar el método `get()` de `Supplier`. Por ejemplo, `Optional.ofNullable(null).orElseGet(() -> UUID.randomUUID().toString()` se usaría para generar valores de forma perezosa cuando no exista un valor.

3. **orElseThrow**: Esto permite a los clientes lanzar sus propias excepciones personalizadas cuando no exista un valor.

El método `find()` mostrado en el ejemplo de antes devuelve un `Optional<Task>` que el cliente puede usar para obtener el valor. Supón que queremos obtener el título de una tarea a partir del `Optional<Task>`, podemos hacerlo usando la función `map()` como se muestra a continuación.

### Obtener el nombre del usuario asignado

Para obtener el nombre del usuario que está asignado a una tarea podemos usar el método `flatMap()` de la siguiente manera.

```java
public String taskAssignedTo(String taskId) {
  return taskRepository.
    find(taskId).
    flatMap(task -> task.getAssignedTo().map(user -> user.getUsername())).
    orElse("NotAssigned");
}
```

### Filtrar con Optional

La tercera operación, como la del API de `Stream`, soportada por `Optional` es `filter`, que te permite filtrar un `Optional` por una propiedad como se muestra en el siguiente ejemplo.

```java
public boolean isTaskDueToday(Optional<Task> task) {
    return task.flatMap(Task::getDueOn).filter(d -> d.isEqual(LocalDate.now())).isPresent();
}
```
[![Analytics](https://ga-beacon.appspot.com/UA-74043032-1/malobato/java8-the-missing-tutorial/05-optionals)](https://github.com/igrigorik/ga-beacon)
