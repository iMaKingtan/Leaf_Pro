package com.generate.server.segment.bean;

import com.generate.common.base.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class SegmentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "系统code")
    @NotBlank(message = "systemId不能为空")
    private String systemId;


    @ApiModelProperty(value = "业务键")
    @NotBlank(message = "bizTag不能为空")
    private String bizTag;

    @ApiModelProperty(value = "id长度")
    private Integer size;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(systemId);
        sb.append(Constants.SPLIT_CHAR);
        sb.append(bizTag);
        return sb.toString();
    }
}
