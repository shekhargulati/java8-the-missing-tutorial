package com.shekhargulati.java8_tutorial.ch10;

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
