package com.piesat.skywalking.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProcessDetailsDto {

    private String pid;
    private String workingDirectory;
    private String cmdline;
    private String startTime;
    private float cpuUsage;
    private long cpuTime;
    private long memoryBytes;
    private float memoryUsage;
    private String userName;

}
