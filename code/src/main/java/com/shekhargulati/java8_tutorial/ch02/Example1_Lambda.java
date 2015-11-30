package com.shekhargulati.java8_tutorial.ch02;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Example1_Lambda {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("shekhar", "rahul", "sameer");
        // sort alphabetically
        Collections.sort(names);
        System.out.println("names sorted alphabetically  >>");
        System.out.println(names);
        System.out.println();

        // using anonymous classes
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });
        System.out.println("names sorted by length  >>");
        System.out.println(names);
        System.out.println();

        /**
         * Using lambda
         * Things to show >>
         * 1. return statement
         * 2. Without return statement
         * 3. Multiple lines
         * 4. Type inference
         */

        Collections.sort(names, (String first, String second) -> second.length() - first.length());
        System.out.println("names sorted by length(reversed)  >>");
        System.out.println(names);
        System.out.println();
    }


}
