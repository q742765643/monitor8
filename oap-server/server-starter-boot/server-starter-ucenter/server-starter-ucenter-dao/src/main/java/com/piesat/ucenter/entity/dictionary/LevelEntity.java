package com.piesat.ucenter.entity.dictionary;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yaya
 * @description 层次属性实体类
 * @date 2019/12/23
 */
@Data
@Entity
@Table(name = "T_SOD_GRID_LAYER_LEVEL")
public class LevelEntity extends BaseEntity {
    /**
     * grib格式（1,2）
     */
    @Column(name="grib_version")
    private Integer gribVersion;

    /**
     * 层次类型
     */
    @Column(name="level_type")
    private Integer levelType;

    /**
     * 层次代码
     */
    @Column(name="level_code", length=10)
    private String levelCode;

    /**
     * 比例因子
     */
    @Column(name="scale_divisor")
    private String scaleDivisor;

    /**
     * 层次的英文描述，代码缩写来源
     */
    @Column(name="level_properity", length=100)
    private String levelProperity;

    /**
     * 中文描述
     */
    @Column(name="level_name", length=100)
    private String levelName;

    /**
     * 单位
     */
    @Column(name="unit", length=50)
    private String unit;

}
