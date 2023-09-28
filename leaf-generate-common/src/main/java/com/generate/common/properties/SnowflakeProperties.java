package com.generate.common.properties;


import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @DESCRIPTION: 配置雪花模式参数
 * @CREATED by: iMaKingtan
 * @DATE: 2023/09/17 20:15
 */
@Data
@ToString
@Component
@PropertySource(value = {"classpath:id-generate.properties"})
@ConfigurationProperties(prefix = "id.generate.snowflake")
public class SnowflakeProperties {
    private String workIp;
    private String workPort;
    private String generatorName;
    private Long blockBackThreshold;
    private String[] etcdPoints;
    /**
     * 周期线程报告间隔
     */
    private Long keepAliveInterval = 1000L;
    /**
     * etcd的租约时间,默认800ms
     */
    private Long leaseTtl = 800L;

    private Long systemReportInterval = 3000L;

    /**
     * ETCD 节点根节点
     */
    private String rootEtcdPath;
    /**
     * 保存所有服务器节点数据的持久节点路径
     */
    private String pathForever;
    /**
     * 保持服务器节点信息的临时节点路径
     */
    private String pathTemp;
    /**
     * 指定获取启动服务IP的的网卡
     */
    private String netInterName;
}
