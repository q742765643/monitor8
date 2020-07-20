package com.piesat.ucenter.entity.system;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/27 16:24
 */
@Entity
@Data
@Table(name="T_SOD_DEPT")
public class DeptEntity extends BaseEntity {
    /** 父部门ID */
    @Column(name="parent_id", length=32)
    private String parentId;

    /** 祖级列表 */
    @Column(name="ancestors", columnDefinition = "TEXT")
    private String ancestors;

    /** 部门名称 */
    @Column(name="dept_name", length=30)
    private String deptName;

    /** 显示顺序 */
    @Column(name="order_num", length=4)
    private int orderNum;

    /** 负责人 */
    @Column(name="leader", length=20)
    private String leader;

    /** 联系电话 */
    @Column(name="phone", length=11)
    private String phone;

    /** 邮箱 */
    @Column(name="email", length=50)
    private String email;

    /** 部门状态:0正常,1停用 */
    @Column(name="status", length=1)
    private String status;

    /** 父部门名称 */
    @Transient
    private String parentName;

}
