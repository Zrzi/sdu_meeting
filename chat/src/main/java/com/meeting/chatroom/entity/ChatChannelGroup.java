package com.meeting.chatroom.entity;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatChannelGroup {

    public final static ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    public final static ConcurrentHashMap<Long, Channel> ID_CHANNEL_REF = new ConcurrentHashMap<>();

    public final static ConcurrentHashMap<Channel, Long> CHANNEL_ID_REF = new ConcurrentHashMap<>();

}
