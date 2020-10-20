package com.piesat.skywalking.dto.model;

import lombok.Data;

/**
 * @ClassName : OverviewNodeDto
 * @Description : 预览节点
 * @Author : zzj
 * @Date: 2020-10-20 14:10
 */
@Data
public class OverviewNodeDto {
    private long ready;
    private long down;
    private long total;
    private float use;
}

