package com.shekhargulati.java8_tutorial.ch06;

import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MapExampleTest {

    @Test
    public void shouldCreateAHashMapUsingSimpleEntries() throws Exception {
        Map<String, Integer> nameAndAge = MapExample.createMap(new SimpleEntry<>("shekhar", 32), new SimpleEntry<>("rahul", 33), new SimpleEntry<>("sameer", 33));
        assertThat(nameAndAge.size(), equalTo(3));
        
    }
}