package com.shekhargulati.java8_tutorial.ch01;

public abstract class CalculatorFactory {

    public static Calculator getInstance() {
        return new BasicCalculator();
    }
}
