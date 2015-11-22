package com.shekhargulati.java8_tutorial.ch12;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CreateVms.class)
public @interface CreateVm {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface CreateVms {
    CreateVm[] value();
}