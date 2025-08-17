package com.soyeon.sharedcalendar.common.id;

import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator implements IdGenerator {
    private static final long EPOCH = 1735689600000L; // 2025-01-01
    private static final long NODE_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = 1L << SEQUENCE_BITS - 1;

    private final long nodeId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(@Value("${id.snowflake.node-id}") long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    @Synchronized
    public long nextId() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < lastTimestamp) {
            throw new IllegalStateException("Clock moved back to the future");
        }
        if (currentTimeMillis == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                while ((currentTimeMillis = System.currentTimeMillis()) <= lastTimestamp) {}
            } else {
                sequence = 0L;
            }
            lastTimestamp = currentTimeMillis;
        }
        return ((currentTimeMillis - EPOCH) << (NODE_BITS +  SEQUENCE_BITS) )
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }
}
