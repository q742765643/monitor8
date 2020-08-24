package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AlarmConfigDto extends HtJobInfoDto {
    @ApiModelProperty(value = "监测类型")
    private Integer monitorType;

    @ApiModelProperty(value = "一般判断条件")
    private List<ConditionDto> generals;

    @ApiModelProperty(value = "危险判断条件")
    private List<ConditionDto> dangers;

    @ApiModelProperty(value = "故障判断条件")
    private List<ConditionDto> severitys;
}
