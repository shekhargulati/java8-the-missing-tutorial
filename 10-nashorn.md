Nashorn: Run JavaScript on the JVM
-----

![Nashorn](https://upload.wikimedia.org/wikipedia/commons/7/7a/Dortmunder_Nashorn_-_Hell_wieherte_der_Hippogryph.jpg)

Nashorn is a high-performance JavaScript runtime written in Java for the JVM. It allows developers to embed JavaScript code inside their Java applications and even use Java classes and methods from their JavaScript code. You can think it as an alternative to Google's V8 JavaScript engine. It is a successor to Rhino JavaScript runtime which came bundled with earlier JDK versions. Nashorn is written from scratch using new language features like JSR 292(Supporting Dynamically Typed Languages) and `invokedynamic`.

From the Nashorn documentation:
> Nashorn uses invokedynamic to implement all of its invocations. If an invocation has a Java object receiver, Nashorn attempts to bind the call to an appropriate Java method instead of a JavaScript function. Nashorn has full discretion about how it resolves methods. As an example, if it can't find a field in the receiver, it looks for an equivalent Java Bean method. The result is completely transparent for calls from JavaScript to Java.

Currently, Nashorn supports [ECMAScript 5.1 specification](http://www.ecma-international.org/ecma-262/5.1/) and work is in progress to support [ECMAScript 6](http://www.ecma-international.org/ecma-262/6.0/) as well. Few ECMAScript 6 features like `let` and `const` are available in latest JDK 8 updates(40 and above) and we will cover them later in this chapter.

In this chapter, we will cover the following:

* Working with Nashorn command-line
* Accessing Java classes and methods
* Working with Java collections
* Using external JavaScript libraries
* Writing scripts
* Using Nashorn from Java code
* Using Java 8 features like Streams and Lambdas inside JavaScript code


## Working with Nashorn command-line

JDK 8 comes bundled with two command-line tools that can be used to work with Nashorn engine. These two command-line tools are `jrunscript` and `jjs`. `jjs` is recommended to be used when working with Nashorn so we will only discuss it. To use `jjs`, you have to add `jjs` to the path. On *nix machines, you can do that adding a symbolic link as shown below.

```bash
$ cd /usr/bin
$ ln -s $JAVA_HOME/bin/jjs jjs
```
Windows users can add `$JAVA_HOME/bin` to the path for easy access.

Once you have set the symbolic link you can access `jjs` from your terminal. To check version of `jjs`, run the following command.

```bash
$ jjs -v
nashorn 1.8.0_60
jjs>
```

It will render the version and then show `jjs>` prompt. You can view the full version of `jjs` by using `jjs -fv` command.

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

It is very easy to access Java classes from within Nashorn. Assuming you are inside the `jjs` shell, you can create an instance of HashMap as shown below.

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

In the code shown above we have used `Java` global object to create HashMap object. `Java` global object has `type` method that takes a string with the fully qualified Java class name, and returns the corresponding `JavaClass` function object.

```bash
jjs> HashMap
[JavaClass java.util.HashMap]
```

The `var userAndAge = new HashMap()` is used to instantiate `java.util.HashMap` class using the `new` keyword.

You can access values by either calling the `get` method or using the `[]` notation as shown below.

```bash
jjs> userAndAge["shekhar"]
32
```

### Accessing static methods

To access static methods you have to first get the Java type using `Java.type` method and then calling method on `JavaClass` function object.

```bash
jjs> var UUID = Java.type("java.util.UUID")
jjs>
jjs> UUID.randomUUID().toString()
e4242b89-0e94-458e-b501-2fc4344d5498
```
