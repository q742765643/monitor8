package com.piesat.ucenter.entity.monitor;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 14:10
 **/
@Entity
@Data
@Table(name="T_SOD_LOGIN_INFO")
public class LoginInfoEntity extends BaseEntity{
    /** 用户账号 */
    @Excel(name="用户账号")
    @Column(name="user_name", length=50)
    private String userName;

    /** 登录状态 0成功 1失败 */
    @Excel(name="登录状态",readConverterExp = "0=成功,1=失败")
    @Column(name="status", length=1)
    private String status;

    /** 登录IP地址 */
    @Excel(name="登录IP地址")
    @Column(name="ipaddr", length=50)
    private String ipaddr;

    /** 登录地点 */
    @Excel(name="登录地点")
    @Column(name="login_location", length=255)
    private String loginLocation;

    /** 浏览器类型 */
    @Excel(name="浏览器类型")
    @Column(name="browser", length=50)
    private String browser;

    /** 操作系统 */
    @Excel(name="操作系统")
    @Column(name="os", length=50)
    private String os;

    /** 提示消息 */
    @Excel(name="提示消息")
    @Column(name="msg", length=255)
    private String msg;

    /** 访问时间 */
    @Excel(name = "访问时间",dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @Column(name="login_time")
    private Date loginTime;
}

