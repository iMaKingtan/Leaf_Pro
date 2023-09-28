package com.generate.core.snowflake.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndPointData {
    private String ip;
    private String port;
    private long timestamp;
    private long workId;
}
