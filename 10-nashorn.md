Nashorn: Run JavaScript on the JVM [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/10-nashorn.md)](http://ttr.myapis.xyz/)
-----

![Nashorn](https://upload.wikimedia.org/wikipedia/commons/7/7a/Dortmunder_Nashorn_-_Hell_wieherte_der_Hippogryph.jpg)

Nashorn is a high-performance JavaScript runtime written in Java for the JVM. It
allows developers to embed JavaScript code inside their Java applications and
even use Java classes and methods from their JavaScript code. You can think it
as an alternative to Google's V8 JavaScript engine. It is a successor to Rhino
JavaScript runtime which came bundled with earlier JDK versions. Nashorn is
written from scratch using new language features like JSR 292(Supporting
Dynamically Typed Languages) and `invokedynamic`.

From the Nashorn documentation:

> Nashorn uses invokedynamic to implement all of its invocations. If an
> invocation has a Java object receiver, Nashorn attempts to bind the call to an
> appropriate Java method instead of a JavaScript function. Nashorn has full
> discretion about how it resolves methods. As an example, if it can't find a
> field in the receiver, it looks for an equivalent Java Bean method. The result
> is completely transparent for calls from JavaScript to Java.

Currently, Nashorn supports [ECMAScript 5.1
specification](http://www.ecma-international.org/ecma-262/5.1/) and work is in
progress to support [ECMAScript
6](http://www.ecma-international.org/ecma-262/6.0/) as well. Few ECMAScript 6
features like `let` and `const` are available in latest JDK 8 updates(40 and
above) and we will cover them later in this chapter.

In this chapter, we will cover the following:

* Working with Nashorn command-line
* Accessing Java classes and methods
* Using external JavaScript libraries
* Writing scripts
* Using Nashorn from Java code
* Using Java 8 features like Streams and Lambdas inside JavaScript code
* Turning off Java language access


## Working with Nashorn command-line

JDK 8 comes bundled with two command-line tools that can be used to work with
Nashorn engine. These two command-line tools are `jrunscript` and `jjs`. `jjs`
is recommended to be used when working with Nashorn so we will only discuss it.
To use `jjs`, you have to add `jjs` to the path. On *nix machines, you can do
that adding a symbolic link as shown below.

```bash
$ cd /usr/bin
$ ln -s $JAVA_HOME/bin/jjs jjs
```

Windows users can add `$JAVA_HOME/bin` to the path for easy access.

Once you have set the symbolic link you can access `jjs` from your terminal. To
check version of `jjs`, run the following command.

```bash
$ jjs -v
nashorn 1.8.0_60
jjs>
```

It will render the version and then show `jjs>` prompt. You can view the full
version of `jjs` by using `jjs -fv` command.

To quit the `jjs` shell, you can use `Ctrl-C`.

Once you are inside `jjs`, you can execute any JavaScript code as shown below.

```bash
jjs> print("learning about Nashorn")
learning about Nashorn
```

You can define functions as shown below.

```
jjs> function add(a,b) {return a + b;}
```

You can call the add function as shown below.

```
jjs> add(5,10)
15
```

## Accessing Java classes and methods

It is very easy to access Java classes from within Nashorn. Assuming you are
inside the `jjs` shell, you can create an instance of HashMap as shown below.

```bash
jjs> var HashMap = Java.type("java.util.HashMap")
jjs> var userAndAge = new HashMap()
jjs> userAndAge.put("shekhar",32)
null
jjs> userAndAge.put("rahul",33)
null
jjs> userAndAge.get("shekhar")
32
```

In the code shown above we have used `Java` global object to create HashMap
object. `Java` global object has `type` method that takes a string with the
fully qualified Java class name, and returns the corresponding `JavaClass`
function object.

```bash
jjs> HashMap
[JavaClass java.util.HashMap]
```

The `var userAndAge = new HashMap()` is used to instantiate `java.util.HashMap`
class using the `new` keyword.

You can access values by either calling the `get` method or using the `[]`
notation as shown below.

```bash
jjs> userAndAge["shekhar"]
32
```

Similarly, you can work with other Java collections. To use an `ArrayList` you
will write code as shown below.

```bash
jjs> var List = Java.type("java.util.ArrayList")
jjs> var names = new List()
jjs> names.add("shekhar")
true
jjs> names.add("rahul")
true
jjs> names.add("sameer")
true
jjs> names.get(0)
shekhar
jjs> names[1]
rahul
```

### Accessing static methods

To access static methods you have to first get the Java type using `Java.type`
method and then calling method on `JavaClass` function object.

```bash
jjs> var UUID = Java.type("java.util.UUID")
jjs>
jjs> UUID.randomUUID().toString()
e4242b89-0e94-458e-b501-2fc4344d5498
```

You can sort list using `Collections.sort` method as shown below.

```bash
jjs> var Collections = Java.type("java.util.Collections")
jjs>
jjs> Collections.sort(names)
jjs> names
[rahul, sameer, shekhar]
jjs>
```

## Using external JavaScript libraries

Let's suppose we want to use an external JavaScript library in our JavaScript
code. Nashorn comes up with a built-in function -- `load` that loads and
evaluates a script from a path, URL, or script object. To use `lodash` library
we can write code as shown below.

```
jjs> load("https://raw.github.com/lodash/lodash/3.10.1/lodash.js")

jjs> _.map([1, 2, 3], function(n) { return n * 3; });
3,6,9
```

## Writing scripts

You can use Nashorn extensions that enable users to write scripts that can use
Unix shell scripting features. To enable shell scripting features, you have to
start `jjs` with `-scripting` option as shown below.

```bash
jjs -scripting
jjs>
```

Now you have access to Nashorn shell scripting global objects.

**$ARG:** This global object can be used to access the arguments passed to the
script

```
$ jjs -scripting -- hello hey
jjs>
jjs> $ARG
hello,hey
```

**$ENV:** A map containing all the current environment variables

```bash
jjs> $ENV["HOME"]
/Users/shekhargulati

jjs> $ENV.JAVA_HOME
/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home
```

**$EXEC:** launches processes to run commands

```bash
jjs> $EXEC("pwd")
/Users/shekhargulati/java8-the-missing-tutorial
```

### Writing executable scripts

You can use shebang(#!) at the beginning of the script to make a script file run
as shell executable. Let's write a simple script that reads content of a file.
We will use Java's `Files` and `Paths` API.

```javascript
#!/usr/bin/jjs

var Paths = Java.type("java.nio.file.Paths");
var Files = Java.type("java.nio.file.Files");

Files.lines(Paths.get($ARG[0])).forEach(function(line){print(line);})
```

We will invoke it as

```bash
$ jjs ch10/lines.js -- README.md
```

## Using Nashorn from Java code

To use Nashorn from inside Java code, you have to create an instance of
ScriptEngine from `ScriptEngineManager` as shown below. Once you have
`ScriptEngine` you can evaluate expressions.

```java
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class NashornExample1 {

    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine nashorn = manager.getEngineByName("nashorn");
        Integer eval = (Integer) nashorn.eval("10 + 20");
        System.out.println(eval);
    }
}
```

Using bindings

```java
import javax.script.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class NashornExample2 {

    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine nashorn = manager.getEngineByName("nashorn");

        Bindings bindings = new SimpleBindings(Stream.of(
                new SimpleEntry<>("a", 10),
                new SimpleEntry<>("b", 20))
                .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
        Double eval = (Double) nashorn.eval("a + b", bindings);
        System.out.println(eval);
    }
}
```

## Using Java 8 features like Streams and Lambdas inside JavaScript code

Java 8 supports lambdas and many API in JDK make use of them. Every collection
in Java has `forEach` method that accepts a consumer. Consumer is an interface
with one method. In Java, you can write following:

```java
Arrays.asList("shekhar","rahul","sameer").forEach(name -> System.out.println(name));

// shekhar
// rahul
// sameer
```

In Nashorn, you can use them same API but you will pass JavaScript function
instead as shown below.

```javascript
jjs> var Arrays = Java.type("java.util.Arrays")
jjs> Arrays.asList("shekhar","rahul","sameer")
[shekhar, rahul, sameer]
jjs> var names = Arrays.asList("shekhar","rahul","sameer")
jjs> names.forEach(function(name){print(name);})
shekhar
rahul
sameer
```

You can also use Stream API with Nashorn as shown below.

```
jjs> names
    .stream().filter(function(name){return name.startsWith("s");})
    .forEach(function(name){print(name);})

shekhar
sameer
```

## Turning off Java language access

In case you need to disallow Java usage, you can very easily turn off by passing
`--no-java` option to `jjs` as shown below.

```
→ jjs --no-java
jjs>
jjs> var HashMap = Java.type("java.util.HashMap")
<shell>:1 TypeError: null has no such function "type"
```

Now when you will try to use `java.util.HashMap` you will get `TypeError`.

You can do the same with Java code as well.

```java

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class NashornExample3 {

    public static void main(String[] args) throws ScriptException {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine nashorn = factory.getScriptEngine(new NoJavaFilter());
        Integer eval = (Integer) nashorn.eval("var HashMap = Java.type('java.util.HashMap')");
        System.out.println(eval);
    }

    private static class NoJavaFilter implements ClassFilter{

        @Override
        public boolean exposeToScripts(String s) {
            return false;
        }
    }
}
```

You will get following exception when you run this program.

```
Caused by: java.lang.ClassNotFoundException: java.util.HashMap
	at jdk.nashorn.internal.runtime.Context.findClass(Context.java:1032)
	at jdk.nashorn.internal.objects.NativeJava.simpleType(NativeJava.java:493)
	at jdk.nashorn.internal.objects.NativeJava.type(NativeJava.java:322)
	at jdk.nashorn.internal.objects.NativeJava.type(NativeJava.java:314)
	at jdk.nashorn.internal.objects.NativeJava.type(NativeJava.java:310)
	at jdk.nashorn.internal.scripts.Script$\^eval\_.:program(<eval>:1)
	at jdk.nashorn.internal.runtime.ScriptFunctionData.invoke(ScriptFunctionData.java:640)
	at jdk.nashorn.internal.runtime.ScriptFunction.invoke(ScriptFunction.java:228)
	at jdk.nashorn.internal.runtime.ScriptRuntime.apply(ScriptRuntime.java:393)
```


[![Analytics](https://ga-beacon.appspot.com/UA-59411913-3/shekhargulati/java8-the-missing-tutorial/10-nashorn)](https://github.com/igrigorik/ga-beacon)
