package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonitorViewVo {
    @ApiModelProperty(value = "分类")
    private String classify;
    @ApiModelProperty(value = "数量")
    private long num;
}
