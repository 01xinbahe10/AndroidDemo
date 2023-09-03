package com.example.aac.test.stack;

public class TestStackUtil {
    public static void printStackSize(String testName) {
        //获取堆内存信息
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        System.out.println(testName + " -Xmx(最大可用内存): " + maxMemory / 1024 / 1024 + "MB");
        System.out.println(testName + "StackSizeUtil -Xms(已获得内存): " + totalMemory / 1024 / 1024 + "MB");
        System.out.println(testName + "StackSizeUtil -空闲内存: " + freeMemory / 1024 / 1024 + "MB");

        //获取栈内存信息
        int processor = Runtime.getRuntime().availableProcessors();
        String stackSize = System.getProperty("sun.arch.data.model");
        int Kilo = 1024;
        int stackInKilo;
        if ("32".equals(stackSize)) {
            stackInKilo = Kilo * 4;
        } else {
            stackInKilo = Kilo * 8;
        }
        long totalStack = (long) stackInKilo * processor;
        System.out.println(testName + " 当前使用的CPU内核数:" + processor);
        System.out.println(testName + " 每个线程栈内存大小:" + stackInKilo / Kilo + "KB");
        System.out.println(testName + " 最大可用栈内存:" + totalStack / Kilo + "KB");
    }
}
