package com.shekhargulati.java8_tutorial.ch01;

public class App3 implements E, F {
    @Override
    public void doSth() {
        F.super.doSth();
    }

    public static void main(String[] args) {
        new App3().doSth();
    }
}

interface E {
    default void doSth() {
        System.out.println("inside E");
    }
}

interface F {
    default void doSth() {
        System.out.println("inside F");
    }
}