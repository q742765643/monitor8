package com.piesat.skywalking.vo;

import lombok.Data;

@Data
public class NetworkVo {
    private String timestamp;
    private double inSpeed;
    private double outSpeed;
}
