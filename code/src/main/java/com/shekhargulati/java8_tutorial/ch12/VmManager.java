package com.shekhargulati.java8_tutorial.ch12;

import java.util.stream.Stream;

public class VmManager {

    @CreateVm(name = "vm1")
    @CreateVm(name = "vm2")
    public void manage() {
        System.out.println("Managing ....");
    }

    public static void main(String[] args) throws Exception {
        CreateVm[] createVms = VmManager.class.getMethod("manage").getAnnotationsByType(CreateVm.class);
        Stream.of(createVms).map(CreateVm::name).forEach(System.out::println);
    }
}
