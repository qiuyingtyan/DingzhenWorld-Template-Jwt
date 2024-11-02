package com.example.utils;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {
    // 开始时间戳
    private static final long START_TIMESTAMP = 1691087910202L;

    // 数据中心ID所占的位数
    private static final long DATA_CENTER_ID_BITS = 5L;
    // 工作机器ID所占的位数
    private static final long WORKER_ID_BITS = 5L;
    // 序列在ID中占的位数
    private static final long SEQUENCE_BITS = 12L;

    // 最大数据中心ID
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    // 最大工作机器ID
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    // 最大序列号
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 工作机器ID向左移12位
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 数据中心ID向左移17位
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    // 时间戳向左移22位
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    // 数据中心ID
    private final long dataCenterId;
    // 工作机器ID
    private final long workerId;
    // 上一次时间戳
    private long lastTimestamp = -1L;
    // 序列号
    private long sequence = 0L;

    // 默认构造函数，数据中心ID和工作机器ID都为1
    public SnowflakeIdGenerator(){
        this(1, 1);
    }

    // 构造函数，传入数据中心ID和工作机器ID
    private SnowflakeIdGenerator(long dataCenterId, long workerId) {
        // 检查数据中心ID是否合法
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        }
        // 检查工作机器ID是否合法
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    // 生成下一个ID
    public synchronized long nextId() {
        // 获取当前时间戳
        long timestamp = getCurrentTimestamp();
        // 如果当前时间戳小于上一次时间戳，说明发生了时钟回拨
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
        }
        // 如果当前时间戳等于上一次时间戳，则序列号加1
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号达到最大值，则等待下一个时间戳
            if (sequence == 0) {
                timestamp = getNextTimestamp(lastTimestamp);
            }
        } else {
            // 如果当前时间戳大于上一次时间戳，则序列号重置为0
            sequence = 0L;
        }
        // 更新上一次时间戳
        lastTimestamp = timestamp;
        // 生成ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) |
                (dataCenterId << DATA_CENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    // 获取当前时间戳
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    // 获取下一个时间戳
    private long getNextTimestamp(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        // 如果当前时间戳小于等于上一次时间戳，则继续获取当前时间戳
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
}
