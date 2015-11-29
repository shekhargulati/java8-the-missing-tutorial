package com.shekhargulati.java8_tutorial.utils;


import com.shekhargulati.java8_tutorial.domain.Task;
import com.shekhargulati.java8_tutorial.domain.TaskType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public class DataUtils {

    public static Stream<String> lines() {
        return filePathToStream("src/main/resources/book.txt");
    }

    public static Stream<String> negativeWords() {
        return filePathToStream("src/main/resources/negative-words.txt");
    }

    public static Stream<String> filePathToStream(String path) {
        try {
            return Files.lines(Paths.get("training", path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static IntStream range(int start, int end) {
        return IntStream.rangeClosed(start, end);
    }

    public static List<Task> getTasks() {
        Task task1 = new Task("Read Java 8 in action", TaskType.READING, LocalDate.of(2015, Month.SEPTEMBER, 20)).addTag("java").addTag("java8").addTag("books");
        Task task2 = new Task("Write factorial program in Haskell", TaskType.CODING, LocalDate.of(2015, Month.SEPTEMBER, 20)).addTag("program").addTag("haskell").addTag("functional");
        Task task3 = new Task("Read Effective Java", TaskType.READING, LocalDate.of(2015, Month.SEPTEMBER, 21)).addTag("java").addTag("books");
        Task task4 = new Task("Write a blog on Stream API", TaskType.BLOGGING, LocalDate.of(2015, Month.SEPTEMBER, 21)).addTag("writing").addTag("stream").addTag("java8");
        Task task5 = new Task("Write prime number program in Scala", TaskType.CODING, LocalDate.of(2015, Month.SEPTEMBER, 22)).addTag("scala").addTag("functional").addTag("program");
        return Stream.of(task1, task2, task3, task4, task5).collect(toList());
    }
}
