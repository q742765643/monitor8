package com.piesat.ucenter.entity.system;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:13
 */
@Entity
@Data
@Table(name = "T_SOD_DICT_DATA")
public class DictDataEntity extends BaseEntity {

    /**
     * 字典排序
     */
    @Excel(name = "字典排序")
    @Column(name = "dict_sort")
    private int dictSort;

    /**
     * 字典标签
     */
    @Excel(name = "字典标签")
    @Column(name = "dict_label", length = 100)
    private String dictLabel;

    /**
     * 字典键值
     */
    @Excel(name = "字典键值")
    @Column(name = "dict_value", length = 100)
    private String dictValue;

    /**
     * 字典类型
     */
    @Excel(name = "字典类型")
    @Column(name = "dict_type", length = 100)
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @Column(name = "css_class", length = 100)
    private String cssClass;

    /**
     * 表格字典样式
     */
    @Column(name = "list_class", length = 100)
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @Column(name = "is_default", length = 1)
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    @Column(name = "status", length = 1)
    private String status;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}
