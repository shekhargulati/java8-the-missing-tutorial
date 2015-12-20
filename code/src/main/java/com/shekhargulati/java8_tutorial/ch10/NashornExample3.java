package com.shekhargulati.java8_tutorial.ch10;

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
