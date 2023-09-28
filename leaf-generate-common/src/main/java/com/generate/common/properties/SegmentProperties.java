package com.generate.common.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * @DESCRIPTION: 配置号段模式参数
 * @CREATED by: iMaKingtan
 * @DATE: 2023/09/17 20:15
 */
@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "id.generate.segment")
public class SegmentProperties {

    private BigDecimal nextSegmentFetchPercent;
    private Integer segmentFetchSize;
    private Integer threadLocalFetchSize;
    private Boolean threadLocalCacheEnabled;

    private Integer corePoolSize = 5;
    private Integer maxPoolSize = 10;
    private Long keepAliveTime = 60L;
    private Integer blockQueueSize = 50;
}
