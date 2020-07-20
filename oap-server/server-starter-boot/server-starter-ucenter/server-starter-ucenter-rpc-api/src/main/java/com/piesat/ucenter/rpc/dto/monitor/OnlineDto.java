package com.piesat.ucenter.rpc.dto.monitor;

import lombok.Data;

import java.util.Date;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/9 10:41
 */
@Data
public class OnlineDto implements Comparable<OnlineDto>{

    /** 会话编号 */
    private String tokenId;

    /** 部门名称 */
    private String deptName;

    /** 用户名称 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录地址 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录时间 */
    private Date loginTime;

    @Override
    public int compareTo(OnlineDto o) {
        return this.getLoginTime().compareTo(o.getLoginTime());
    }
}
