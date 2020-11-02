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
 * @创建时间 2019/12/3 15:27
 */
@Entity
@Data
@Table(name = "T_SOD_DICT_TYPE")
public class DictTypeEntity extends BaseEntity {


    /**
     * 字典名称
     */
    @Excel(name = "字典名称")
    @Column(name = "dict_name", length = 100)
    private String dictName;

    /**
     * 字典类型
     */
    @Excel(name = "字典类型")
    @Column(name = "dict_type", length = 100)
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "字典类型", readConverterExp = "0=正常,1=停用")
    @Column(name = "status", length = 1)
    private String status;
    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}
