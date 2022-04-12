package com.meeting.gateway.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestAtomicCounter {

    @Test
    public void testAtomicInteger() {
        AtomicInteger atomic = new AtomicInteger(0);
        int prev = atomic.get();
        int current = prev + 1;
        System.out.println(atomic.compareAndSet(prev, current));
    }

}
