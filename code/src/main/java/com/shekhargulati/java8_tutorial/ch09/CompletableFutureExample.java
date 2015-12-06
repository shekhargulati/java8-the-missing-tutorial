package com.shekhargulati.java8_tutorial.ch09;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class CompletableFutureExample {

    public static void main(String[] args) {
        CompletableFuture.completedFuture("hello");
        CompletableFuture.runAsync(() -> System.out.println("hello"));
        CompletableFuture.runAsync(() -> System.out.println("hello"), Executors.newSingleThreadExecutor());
        CompletableFuture.supplyAsync(() -> UUID.randomUUID().toString());
        CompletableFuture.supplyAsync(() -> UUID.randomUUID().toString(), Executors.newSingleThreadExecutor());
    }
}
