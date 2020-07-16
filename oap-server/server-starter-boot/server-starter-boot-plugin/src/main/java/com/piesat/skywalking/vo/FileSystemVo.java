package com.piesat.skywalking.vo;

import lombok.Data;

@Data
public class FileSystemVo {
    private String timestamp;
    private String diskName;
    private double usage;
    private double free;
}
