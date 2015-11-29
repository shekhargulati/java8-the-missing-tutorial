package com.shekhargulati.java8_tutorial.ch03;


import com.shekhargulati.java8_tutorial.domain.Task;
import com.shekhargulati.java8_tutorial.domain.TaskType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.shekhargulati.java8_tutorial.utils.DataUtils.getTasks;


public class Example1_Java7 {

    public static void main(String[] args) {
        List<Task> tasks = getTasks();
        List<Task> readingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getType() == TaskType.READING) {
                readingTasks.add(task);
            }
        }
        Collections.sort(readingTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getTitle().length() - t2.getTitle().length();
            }
        });
        for (Task readingTask : readingTasks) {
            System.out.println(readingTask.getTitle());
        }
    }
}
