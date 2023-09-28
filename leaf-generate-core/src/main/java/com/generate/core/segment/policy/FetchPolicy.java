package com.generate.core.segment.policy;

import java.math.BigDecimal;
/**
 * 自适应策略模式 
 */
public interface FetchPolicy {

    /**
     *  是否开启 threadLocal 缓存
     *
     */
    boolean threadLocalCacheEnabled();

    /**
     * 缓存拉取的数量
     * @param key
     *
     */
    int threadLocalFetchSize(String key);

    /**
     * 号段拉取的数量
     * @param key
     * @return
     */
    int segmentFetchSize(String key);

    BigDecimal nextSegmentFetchPercent(String key);
}
