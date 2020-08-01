package com.piesat.skywalking.dto;

import lombok.Data;

@Data
public class ConditionDto {
    private String relation;
    private String paramname;
    private String operate;
    private String paramvalue;
}
