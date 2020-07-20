package com.piesat.ucenter.entity.system;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/21 15:23
 */
@Entity
@Data
@Table(name="T_SOD_USER")
public class UserEntity extends BaseEntity {
    /**
     * 部门id
     */
    @Column(name="dept_id", length=32)
    private String deptId;

    /**
     * 用户账号
     */
    @Excel(name = "用户帐号")
    @Column(name="user_name", length=36,nullable=false)
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户昵称")
    @Column(name="nick_name", length=30,nullable=false)
    private String nickName;

    /**
     * 用户类型（00系统用户;11业务注册用户）
     */
    @Column(name="user_type",columnDefinition = "varchar(2) default '00'")
    private String userType;

    /**
     * 用户邮箱
     */
    @Excel(name = "用户邮箱")
    @Column(name="email",columnDefinition = "varchar(50) default ''")
    private String email;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码")
    @Column(name="phonenumber",columnDefinition = "varchar(11) default ''")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @Excel(name = "性别",readConverterExp = "0=男,1=女,2=未知")
    @Column(name="sex",columnDefinition = "varchar(1) default '0'")
    private String sex;

    /**
     * 头像地址
     */
    @Column(name="avatar",columnDefinition = "varchar(100) default ''")
    private String avatar;

    /**
     * appId
     */
    @Column(name="app_id",columnDefinition = "varchar(100) default ''")
    private String appId;
    /**
     * 密码
     */
    @Column(name="password",columnDefinition = "varchar(100) default ''")
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Column(name="status",columnDefinition = "varchar(1) default '0'")
    private String status;

    /**
     * 最后登陆ip
     */
    @Column(name="login_ip",columnDefinition = "varchar(50) default '0'")
    private String loginIp;

    /**
     * 最后登陆时间
     */
    @Excel(name = "最后登陆时间",dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @Column(name="login_date")
    private Date loginDate;


    /**
     * 备注
     */
    @Column(name="remark",length = 500)
    private String remark;

    @Column(name="pdf_path",length = 500)
    private String pdfPath;
    /** 角色组 */
    @Transient
    private String[] roleIds;
    @Transient
    private DeptEntity dept;

    /** 角色对象 */
    @Transient
    private List<RoleEntity> roles;




    /*                  业务注册用户新增属性                      */
    /**
     * web用户id
     */
    @Column(name="WEB_USERID", length = 36)
    private String webUserId;
    /**
     * 业务类型
     */
    @Column(name="BIZ_TYPE", length = 36)
    private String bizType;
    /**
     * 绑定ip
     */
    @Column(name="BIZ_IP", length = 200)
    private String bizIp;
    /**
     *有效时间
     */
    @Column(name="VALID_TIME")
    private Date validTime;
    /**
     * 责任人姓名
     */
    @Column(name="WEB_USERNAME", length = 30)
    private String webUsername;
    /**
     * 系统名称
     */
    @Column(name="APP_NAME", length = 50)
    private String appName;
    /**
     * 单位名称
     */
    @Column(name="LEGAL_UNITS", length = 100)
    private String legalUnits;
    /**
     * 部门名称
     */
    @Column(name="DEPT_NAME", length = 100)
    private String deptName;
    /**
     * 指导老师姓名
     */
    @Column(name="TUTOR_NAME", length = 30)
    private String tutorName;
    /**
     * 指导老师电话
     */
    @Column(name="TUTOR_PHONE", length = 20)
    private String tutorPhone;
    /**
     * 申请材料
     */
    @Column(name="APPLY_PAPER", length = 200)
    private String applyPaper;
    /**
     * 申请时间
     */
    @Column(name="APPLY_TIME")
    private Date applyTime;

    /**
     * 最后编辑时间
     */
    @Column(name="LAST_EDIT_TIME")
    private Date lastEditTime;

    /**
     * 审核状态
     * 0：待审核
     * 1：审核通过
     * 2：驳回
     * 3：激活
     * 4：注销
     */
    @Column(name="CHECKED", length = 1)
    private String checked;


    /**
     * 存储系统管理接口权限
     */
    @Column(name="SOD_API", length = 1)
    private String sodApi;

    /**
     * 是否申请资料
     */
    @Column(name="SOD_DATA", length = 1)
    private String sodData;

    /**
     * 存储系统数据库申请
     */
    @Column(name="DB_IDS", length = 200)
    private String dbIds;
    /**
     *是否创建专题库 “1” 是 “0” 否
     */
    @Column(name="DB_CREATE", length = 1)
    private String dbCreate;


    @Column(name="REASON", length = 500)
    private String reason;
}
