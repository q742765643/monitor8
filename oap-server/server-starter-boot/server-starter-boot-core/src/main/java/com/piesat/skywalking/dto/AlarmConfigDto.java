package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class AlarmConfigDto extends HtJobInfoDto {
    private String monitorType;

    private List<ConditionDto> generals;

    private List<ConditionDto> dangers;

    private List<ConditionDto> severitys;
}
