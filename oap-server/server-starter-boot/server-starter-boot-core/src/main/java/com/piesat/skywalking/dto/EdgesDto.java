package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import lombok.Data;

/**
 * @ClassName : EdgesDto
 * @Description :
 * @Author : zzj
 * @Date: 2021-02-23 10:26
 */
@Data
public class EdgesDto extends BaseDto {
    private String source;
    private String target;
    private Integer currentStatus;
}

