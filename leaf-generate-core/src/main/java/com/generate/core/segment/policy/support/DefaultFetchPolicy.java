package com.generate.core.segment.policy.support;

import com.generate.common.properties.SegmentProperties;
import com.generate.core.segment.policy.FetchPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DefaultFetchPolicy implements FetchPolicy {

    @Autowired
    SegmentProperties segmentProperties;

    /**
     * 默认不开启ThreadLocal
     */
    @Override
    public boolean threadLocalCacheEnabled() {
        Boolean enabled = segmentProperties.getThreadLocalCacheEnabled();
        // 不为空并且为true
        return (enabled != null && enabled) ? enabled : false;
    }
    /**
     * 默认从threadLocal拿出来10个
     */
    @Override
    public int threadLocalFetchSize(String key) {
        Integer fetchSize = segmentProperties.getThreadLocalFetchSize();
        return fetchSize != null ? fetchSize : 10;
    }
    /**
     * 默认拿一百个key
     */
    @Override
    public int segmentFetchSize(String key) {
        Integer fetchSize = segmentProperties.getSegmentFetchSize();
        return fetchSize != null ? fetchSize : 100;
    }

    /**
     * 预热阈值
     * @param key
     * @return
     */
    @Override
    public BigDecimal nextSegmentFetchPercent(String key) {
        BigDecimal percent = segmentProperties.getNextSegmentFetchPercent();
        return percent != null ? percent : new BigDecimal("0.9");
    }
}
