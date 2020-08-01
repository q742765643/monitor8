package com.piesat.ucenter.rpc.dto.monitor;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 14:14
 **/
@Data
public class LoginInfoDto extends BaseDto {
    private String id;
    /** 用户账号 */
    @ApiModelProperty(value = "用户账号")
    private String userName;

    /** 登录状态 0成功 1失败 */
    @ApiModelProperty(value = "登录状态 0成功 1失败")
    private String status;

    /** 登录IP地址 */
    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    /** 登录地点 */
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    /** 浏览器类型 */
    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    /** 操作系统 */
    @ApiModelProperty(value = "操作系统")
    private String os;

    /** 提示消息 */
    @ApiModelProperty(value = "提示消息")
    private String msg;

    /** 访问时间 */
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;
}

