package com.shekhargulati.java8_tutorial.ch10;

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
