package com.meeting.chatroom.entity;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class ChatChannelGroup {

    private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    private final Map<Long, Channel> ID_CHANNEL_REF = new HashMap<>();

    private final Map<Channel, Long> CHANNEL_ID_REF = new HashMap<>();

    public void addChannel(Long id, Channel channel) {
        LOCK.writeLock().lock();
        try {
            ID_CHANNEL_REF.put(id, channel);
            CHANNEL_ID_REF.put(channel, id);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public void removeChannel(Long id, Channel channel) {
        LOCK.writeLock().lock();
        try {
            ID_CHANNEL_REF.remove(id);
            CHANNEL_ID_REF.remove(channel);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public void removeChannel(Channel channel) {
        LOCK.writeLock().lock();
        try {
            Long uid = CHANNEL_ID_REF.get(channel);
            CHANNEL_ID_REF.remove(channel);
            if (uid != null) {
                ID_CHANNEL_REF.remove(uid);
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public void print() {
        LOCK.readLock().lock();
        try {
            ID_CHANNEL_REF.forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            System.out.println("print over");
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public Channel getChannelById(Long id) {
        LOCK.readLock().lock();
        try {
            return ID_CHANNEL_REF.get(id);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public Long getIdByChannel(Channel channel) {
        LOCK.readLock().lock();
        try {
            return CHANNEL_ID_REF.get(channel);
        } finally {
            LOCK.readLock().unlock();
        }
    }

}
