package com.meeting.gateway.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {

    private final AtomicInteger atomic;

    public AtomicCounter() {
        atomic = new AtomicInteger(0);
    }

    public int autoIncrement() {
        int prev, current;
        do {
            prev = atomic.get();
            current = (prev == Integer.MAX_VALUE ? 0 : prev + 1);
        } while (!atomic.compareAndSet(prev, current));
        return current;
    }

}
