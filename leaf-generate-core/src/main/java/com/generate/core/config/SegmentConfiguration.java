package com.generate.core.config;

import com.generate.common.properties.SegmentProperties;
import com.generate.common.properties.SnowflakeProperties;
import com.google.common.eventbus.AsyncEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @DESCRIPTION:
 * @CREATED by: iMaKingtan
 * @DATE: 2023/3/1 15:16
 */
@Configuration
@ConditionalOnProperty(prefix = "id.generate",name = "segment",havingValue = "true")
@ComponentScan("com.generate.core.segment")
public class SegmentConfiguration {
    @Autowired
    private SegmentProperties segmentProperties;

    @Bean
    public AsyncEventBus asyncEventBus(){
        AsyncEventBus eventBus = new AsyncEventBus("segment-event-bus",
                new ThreadPoolExecutor(segmentProperties.getCorePoolSize(),
                                       segmentProperties.getMaxPoolSize(),
                                       segmentProperties.getCorePoolSize(),
                                        TimeUnit.SECONDS,
                                        new LinkedBlockingQueue<>(segmentProperties.getBlockQueueSize())));
        return eventBus;
    }
}
