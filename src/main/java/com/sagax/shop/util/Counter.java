package com.sagax.shop.util;

public class Counter {
    private volatile int count = 0;

    public void increment() {
        count++;
    }

    public int get() { return count; }
}