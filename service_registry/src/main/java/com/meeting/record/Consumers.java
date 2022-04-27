package com.meeting.record;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.Service;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 服务消费者
 */
@Component
public class Consumers {

    /**
     * 读写共享锁
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 根据微服务的id将微服务存储
     */
    private final Map<Integer, Service> serviceMap = new HashMap<>();

    /**
     * 根据微服务的名称将微服务存储
     */
    private final Map<String, List<Service>> servicesMap = new HashMap<>();

    /**
     * 当有微服务需要注册时，加入map中
     * @param service 微服务
     * @return 返回值表示添加成功与否
     */
    public boolean addRecord(Service service) {
        if (service == null) {
            return false;
        }
        if (checkIfExist(service)) {
            return false;
        }
        lock.writeLock().lock();
        try {
            serviceMap.put(service.getServiceId(), service);
            List<Service> serviceList = servicesMap.getOrDefault(service.getServiceName(), null);
            if (serviceList == null) {
                serviceList = new ArrayList<>();
            }
            serviceList.add(service);
            servicesMap.put(service.getServiceName(), serviceList);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeService(Integer id) {
        if (id == null) {
            return false;
        }
        if (!checkIfExist(id)) {
            return false;
        }
        lock.writeLock().lock();
        try {
            Service remove = serviceMap.remove(id);
            List<Service> services = servicesMap.get(remove.getServiceName());
            services.remove(remove);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Service get(int id) {
        return serviceMap.get(id);
    }

    public String getAllCopy() {
        lock.readLock().lock();
        try {
            return JSON.toJSONString(servicesMap);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int[] keys() {
        lock.readLock().lock();
        try {
            return serviceMap
                    .keySet()
                    .stream()
                    .mapToInt(Integer::valueOf)
                    .toArray();
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean checkIfExist(Service service) {
        lock.readLock().lock();
        try {
            Service record = null;
            String ip = null;
            Integer port = null;
            for (Integer key : serviceMap.keySet()) {
                record = serviceMap.get(key);
                ip = record.getIp();
                port = record.getPort();
                if (service.getServiceId().equals(key) ||
                        (service.getIp().equals(ip) && service.getPort().equals(port))) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean checkIfExist(Integer id) {
        lock.readLock().lock();
        try {
            return serviceMap.containsKey(id);
        } finally {
            lock.readLock().unlock();
        }
    }

}
