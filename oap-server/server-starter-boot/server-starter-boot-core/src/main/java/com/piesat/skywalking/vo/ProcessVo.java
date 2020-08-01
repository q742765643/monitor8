package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessVo {
    @ApiModelProperty(value = "进程名")
    private String processName;
    @ApiModelProperty(value = "进程命令行")
    private String cmdline;

}
