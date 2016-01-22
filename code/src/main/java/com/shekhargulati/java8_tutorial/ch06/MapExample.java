package com.shekhargulati.java8_tutorial.ch06;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MapExample {

    public static <T, U> Map<T, U> createMap(SimpleEntry<T, U>... entries) {
        return Stream.of(entries).collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

}
