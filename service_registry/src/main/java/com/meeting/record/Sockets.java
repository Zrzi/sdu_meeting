package com.meeting.record;

import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class Sockets {

    /**
     * 读写共享锁
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final List<Socket> sockets = new ArrayList<>();

    public boolean addSocket(Socket socket) {
        lock.writeLock().lock();
        try {
            return sockets.add(socket);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeSocket(Socket socket) {
        lock.writeLock().lock();
        try {
            return sockets.remove(socket);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return sockets.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Socket> copy() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(sockets);
        } finally {
            lock.readLock().unlock();
        }
    }

}
