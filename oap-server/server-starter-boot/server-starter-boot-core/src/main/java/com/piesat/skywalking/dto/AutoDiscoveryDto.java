package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AutoDiscoveryDto extends HtJobInfoDto {
    @ApiModelProperty(value = "ip 范围 10.1.100.1-8")
    private String ipRange;
    private String updateInterval;
}
