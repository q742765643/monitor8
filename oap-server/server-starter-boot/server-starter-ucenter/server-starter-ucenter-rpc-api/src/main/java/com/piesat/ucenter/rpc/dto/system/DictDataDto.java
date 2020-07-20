package com.piesat.ucenter.rpc.dto.system;

import com.piesat.util.BaseDto;
import lombok.Data;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 17:46
 */
@Data
public class DictDataDto extends BaseDto {
    /** 字典排序 */
    private int dictSort;

    /** 字典标签 */
    private String dictLabel;

    /** 字典键值 */
    private String dictValue;

    /** 字典类型 */
    private String dictType;

    /** 样式属性（其他样式扩展） */
    private String cssClass;

    /** 表格字典样式 */
    private String listClass;

    /** 是否默认（Y是 N否） */
    private String isDefault;

    /** 状态（0正常 1停用） */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
