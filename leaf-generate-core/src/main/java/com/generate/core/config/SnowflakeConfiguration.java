package com.generate.core.config;

import com.generate.common.properties.SnowflakeProperties;
import com.generate.core.snowflake.notify.DefaultNotifyService;
import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @DESCRIPTION: config leaf_pro
 * @CREATED by: iMaKingtan
 * @DATE: 2023/2/28 14:25
 */
@Configuration
@ConditionalOnProperty(prefix = "id.generate",name = "snowflake",havingValue = "true")
@ComponentScan("com.generate.core.snowflake")
public class SnowflakeConfiguration {

    @Autowired
    private SnowflakeProperties snowflakeProperties;

    @Bean
    public Client client(){
        String[] endpoints = snowflakeProperties.getEtcdPoints();
        Client etcdClient = Client.builder().endpoints(endpoints).build();
        return etcdClient;
    }
    @Bean
    public DefaultNotifyService defaultNotifyService(){
        return new DefaultNotifyService();
    }
}
