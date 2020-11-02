package com.piesat.ucenter.rpc.dto.dictionary;

import com.piesat.util.BaseDto;
import lombok.Data;

/**
 * @author yaya
 * @description
 * @date 2019/12/23
 */
@Data
public class LevelDto extends BaseDto {
    /**
     * grib格式（1，2）
     */
    private Integer gribVersion;

    /**
     * 层次类型
     */
    private Integer levelType;

    /**
     * 层次代码
     */
    private String levelCode;

    /**
     * 比例因子
     */
    private String scaleDivisor;

    /**
     * 层次的英文描述，代码缩写来源
     */
    private String levelProperity;

    /**
     * 中文描述
     */
    private String levelName;

    /**
     * 单位
     */
    private String unit;

}
