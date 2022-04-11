package com.meeting.gateway.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {

    @Autowired
    private Router router;

    @Test
    public void testRouter() {
        for (Service service : router.getServices()) {
            System.out.println(service);
        }
    }

}
