package com.piesat.skywalking.dto.model;

import lombok.Data;

/**
 * @ClassName : NodeStatusDto
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-20 14:50
 */
@Data
public class NodeStatusDto {
    private float cpuUse;
    private float cpuPct;
    private float cpuCores;
    private float memoryUse;
    private float memoryPct;
    private float memoryTotal;
}

