package com.soyeon.sharedcalendar.common.id;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {
    private final SnowflakeService snowflakeService;

    public SnowflakeIdGenerator(SnowflakeService snowflakeService) {
        this.snowflakeService = snowflakeService;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return snowflakeService.nextId();
    }
}
