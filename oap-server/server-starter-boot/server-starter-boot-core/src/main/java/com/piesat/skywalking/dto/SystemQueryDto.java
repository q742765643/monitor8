package com.piesat.skywalking.dto;

import lombok.Data;

@Data
public class SystemQueryDto {
    private String ip;
    private String startTime;
    private String endTime;
}
