package com.meeting.gateway.balance;

/**
 * 负载均衡
 */
public interface LoadBalancer {

    int getNextService(int length);

}
