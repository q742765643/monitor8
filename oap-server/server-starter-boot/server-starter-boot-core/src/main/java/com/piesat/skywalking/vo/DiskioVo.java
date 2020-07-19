package com.piesat.skywalking.vo;

import lombok.Data;

@Data
public class DiskioVo {
    private String timestamp;
    private double write;
    private double read;
}
