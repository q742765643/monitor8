package com.piesat.skywalking.dto.model;

import lombok.Data;

import java.util.List;

@Data
public class JobContext<T> {
    private T htJobInfoDto;
    private List<?> lists;
    private String handler;
}
