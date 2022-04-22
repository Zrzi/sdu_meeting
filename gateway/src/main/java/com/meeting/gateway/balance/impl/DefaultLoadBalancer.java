package com.meeting.gateway.balance.impl;

import com.meeting.gateway.balance.LoadBalancer;
import com.meeting.gateway.entity.AtomicCounter;

/**
 * 默认的负载均衡处理器
 * 采用轮询方法
 */
public class DefaultLoadBalancer implements LoadBalancer {

    private final AtomicCounter counter;

    public DefaultLoadBalancer() {
        counter = new AtomicCounter();
    }

    @Override
    public int getNextService(int length) {
        return counter.getAndIncrement() % length;
    }

}
