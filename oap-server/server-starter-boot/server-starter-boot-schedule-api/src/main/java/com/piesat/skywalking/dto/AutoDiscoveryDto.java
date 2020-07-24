package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import lombok.Data;

@Data
public class AutoDiscoveryDto extends HtJobInfoDto {
    private String ipRange;
    private String updateInterval;
}
