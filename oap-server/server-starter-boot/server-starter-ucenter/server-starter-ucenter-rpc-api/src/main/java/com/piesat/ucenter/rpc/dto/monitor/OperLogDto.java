package com.piesat.ucenter.rpc.dto.monitor;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-16 14:00
 **/
@Data
public class OperLogDto extends BaseDto {
    /**
     * 日志主键
     */
    @ApiModelProperty(value = "日志主键")
    private String id;

    /**
     * 操作模块
     */
    @ApiModelProperty(value = "操作模块")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @ApiModelProperty(value = "业务类型（0其它 1新增 2修改 3删除）")
    private Integer businessType;

    /**
     * 业务类型数组
     */
    @ApiModelProperty(value = "业务类型数组")
    private Integer[] businessTypes;

    /**
     * 请求方法
     */
    @ApiModelProperty(value = "请求方法")
    private String method;

    /**
     * 请求方式
     */
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @ApiModelProperty(value = "操作类别（0其它 1后台用户 2手机端用户） ")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @ApiModelProperty(value = "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 请求url
     */
    @ApiModelProperty(value = "请求url")
    private String operUrl;

    /**
     * 操作地址
     */
    @ApiModelProperty(value = "操作地址")
    private String operIp;

    /**
     * 操作地点
     */
    @ApiModelProperty(value = "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @ApiModelProperty(value = "返回参数")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    private Integer status;

    /**
     * 错误消息
     */
    @ApiModelProperty(value = "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    private Date operTime;
}

