package com.generate.core.segment.database.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "leaf_alloc")
public class LeafAlloc {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 系统id
     */
    private String systemId;
    /**
     * 组id
     */
    private Integer groupId;
    /**
     * 业务标签
     */
    private String bizTag;
    /**
     * 最大值
     */
    private Long maxId;
    /**
     * 填充零值
     */
    private Integer fillZero;
    /**
     * 步长
     */
    private Integer step;
    /**
     * 是否有效
     */
    private Boolean enableFlag;

    private String description;
    /**
     * 更新时间
     */
    private Date updateTime;
}