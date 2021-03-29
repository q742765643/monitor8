package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : NetIpDto
 * @Description :
 * @Author : zzj
 * @Date: 2021-03-29 10:27
 */
@Data
public class NetIpDto {
    @ApiModelProperty(value = "ip")
    private String ip;
    @ApiModelProperty(value = "0 不在线 1在线")
    private Integer currentStatus = 0;
    private String discoveryId;
}

