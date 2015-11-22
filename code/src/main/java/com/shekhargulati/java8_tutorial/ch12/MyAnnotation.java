package com.shekhargulati.java8_tutorial.ch12;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
public @interface MyAnnotation {
}


class MyAnnotationUsage<@MyAnnotation T> {

    public static void main(String[] args) {

    }

    public static void doSth() {
        List<@MyAnnotation String> names = Arrays.asList("shekhar");
    }

}