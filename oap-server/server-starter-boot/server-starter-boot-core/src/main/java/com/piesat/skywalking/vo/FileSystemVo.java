package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FileSystemVo {
    @ApiModelProperty(value = "时间")
    private String timestamp;
    @ApiModelProperty(value = "磁盘名称")
    private String diskName;
    @ApiModelProperty(value = "磁盘使用率")
    private BigDecimal usage;
    @ApiModelProperty(value = "磁盘空闲 GB")
    private BigDecimal free;
    @ApiModelProperty(value = "磁盘使用率")
    private BigDecimal useByte;
}
