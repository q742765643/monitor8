package com.piesat.ucenter.entity.system;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 15:15
 */
@Entity
@Data
@Table(name="T_SOD_ROLE")
public class RoleEntity extends BaseEntity {
    /** 角色名称 */
    @Excel(name="角色名称")
    @Column(name="role_name",length = 30)
    private String roleName;

    /** 角色权限 */
    @Excel(name="角色权限")
    @Column(name="role_key",length = 100)
    private String roleKey;

    /** 角色排序 */
    @Excel(name="角色排序")
    @Column(name="role_sort",length = 4)
    private int roleSort;

    /** 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限） */
    @Column(name="data_scope",columnDefinition = "varchar(1) default '1'")
    private String dataScope;

    /** 角色状态（0正常 1停用） */
    @Excel(name="角色状态",readConverterExp = "0=正常,1=停用")
    @Column(name="status",columnDefinition = "varchar(1) default '0'")
    private String status;

    /**
     * 备注
     */
    @Excel(name="备注")
    @Column(name="remark",length = 500)
    private String remark;

    /** 菜单组 */
    @Transient
    private String[] menuIds;

    /** 部门组（数据权限） */
    @Transient
    private String[] deptIds;
}
