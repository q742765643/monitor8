package com.piesat.ucenter.entity.system;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 业务注册用户
 *
 * @author cwh
 * @date 2020年 04月17日 16:12:07
 */
@Entity
@Data
@Table(name="T_SOD_BIZ_USER")
public class BizUserEntity {

    /**
     * 业务用户id
     */
    @Id
    @Column(name="BIZ_USERID", length = 36)
    private String bizUserId;

    /**
     * web用户id
     */
    @Column(name="WEB_USERID", length = 36)
    private String webUserId;

    /**
     * 密码
     */
    @Column(name="PASSWORD", length = 312)
    private String password;

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
     * 备注
     */
    @Column(name="REMARK", length = 200)
    private String remark;

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
     * 联系电话
     */
    @Column(name="PHONE", length = 20)
    private String phone;

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

}
