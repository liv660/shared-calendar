package com.soyeon.sharedcalendar.common.id;

import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeService {
    private static final long EPOCH = 1735689600000L; // 2025-01-01
    private static final long NODE_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_NODE = ~(-1L << NODE_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private final Long nodeId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeService(@Value("${id.snowflake.node-id}") Long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE) {
            throw new IllegalArgumentException("nodeId out of range");
        }
        this.nodeId = nodeId;
    }

    @Synchronized
    public Long nextId() {
        long now = System.currentTimeMillis();
        if (now < lastTimestamp) {
            throw new IllegalStateException("Clock moved back to the future");
        }
        if (now == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                now = waitNextMillis(now);
            } else {
                sequence = 0L;
            }
            lastTimestamp = now;
        }
        return ((now - EPOCH) << (NODE_BITS + SEQUENCE_BITS))
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }

    private Long waitNextMillis(Long current) {
        long now = System.currentTimeMillis();
        while (current <= now) {
            now = System.currentTimeMillis();
        }
        return now;
    }
}
