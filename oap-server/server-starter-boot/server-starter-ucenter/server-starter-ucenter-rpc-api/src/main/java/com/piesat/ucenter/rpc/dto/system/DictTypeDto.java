package com.piesat.ucenter.rpc.dto.system;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:34
 */
@Data
public class DictTypeDto extends BaseDto {

    /** 字典名称 */
    @ApiModelProperty(value = "字典名称")
    private String dictName;

    /** 字典类型 */
    @ApiModelProperty(value = "字典类型")
    private String dictType;

    /** 状态（0正常 1停用） */
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;

    private String params;
    @ApiModelProperty(value = "备注")
    private String remark;
}
