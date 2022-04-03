package com.meeting.counter;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ServiceIdCounter {

    private final AtomicInteger atomic = new AtomicInteger(0);

    public int getNext() {
        for (;;) {
            int current = atomic.get();
            int next = current + 1;
            if (atomic.compareAndSet(current, next)) {
                return next;
            }
        }
    }

}
