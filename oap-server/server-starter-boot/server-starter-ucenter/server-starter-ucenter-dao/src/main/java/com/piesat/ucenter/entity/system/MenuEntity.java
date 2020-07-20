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
 * @创建时间 2019/11/28 15:23
 */
@Entity
@Data
@Table(name="T_SOD_MENU")
public class MenuEntity extends BaseEntity {

    /** 菜单名称 */
    @Excel(name="菜单名称")
    @Column(name="MENU_NAME", length=50)
    private String menuName;

    /** 父菜单名称 */
    @Excel(name="父菜单名称")
    @Transient
    private String parentName;

    /** 父菜单ID */
    @Excel(name="父菜单ID")
    @Column(name="PARENT_ID", length=32)
    private String parentId;

    /** 显示顺序 */
    @Excel(name="显示顺序")
    @Column(name="ORDER_NUM", length=4)
    private int orderNum;

    /** 路由地址 */
    @Excel(name="路由地址")
    @Column(name="PATH", length=200)
    private String path;

    /** 组件路径 */
    @Excel(name="组件路径")
    @Column(name="COMPONENT", length=255)
    private String component;

    /** 是否为外链（0是 1否） */
    @Column(name="IS_FRAME")
    private int isFrame=1;

    /** 类型（M目录 C菜单 F按钮） */
    @Excel(name="类型",readConverterExp = "M=目录,C=菜单,F=按钮")
    @Column(name="MENU_TYPE",length = 1)
    private String menuType;

    /** 菜单状态:0显示,1隐藏 */
    @Excel(name="菜单状态",readConverterExp = "0=显示,1=隐藏")
    @Column(name="VISIBLE",length = 1)
    private String visible="0";

    /** 权限字符串 */
    @Column(name="PERMS",length = 100)
    private String perms;

    /** 菜单图标 */
    @Column(name="ICON",length = 100)
    private String icon;


    /**
     * 备注
     */
    @Column(name="REMARK",length = 500)
    private String remark;
}
