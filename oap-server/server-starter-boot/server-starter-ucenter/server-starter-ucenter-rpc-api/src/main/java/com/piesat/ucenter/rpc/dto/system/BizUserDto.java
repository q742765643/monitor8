package com.piesat.ucenter.rpc.dto.system;

import lombok.Data;

import java.util.Date;

/**
 * @author cwh
 * @date 2020年 04月17日 16:30:21
 */
@Data
public class BizUserDto {

    private String bizUserId;

    private String webUserId;

    private String password;

    private String bizType;

    private String bizIp;

    private Date validTime;

    private String remark;

    private String webUsername;

    private String appName;

    private String legalUnits;

    private String deptName;

    private String phone;

    private String tutorName;

    private String tutorPhone;

    private String applyPaper;

    private Date applyTime;

    private Date lastEditTime;

    private String checked;
}
