package com.piesat.ucenter.entity.monitor;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.util.BaseDto;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-16 15:00
 **/
@Entity
@Data
@Table(name="T_SOD_OPER_LOG")
public class OperLogEntity extends BaseEntity{
    /** 操作模块 */
    @Excel(name="操作模块")
    @Column(name="title", length=50)
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @Excel(name = "业务类型",readConverterExp = "0=其它,1=新增,2=修改,3=删除")
    @Column(name="business_type", length=2)
    private Integer businessType;

    /** 业务类型数组 */
    @Transient
    private Integer[] businessTypes;

    /** 请求方法 */
    @Excel(name = "请求方法")
    @Column(name="method", length=100)
    private String method;

    /** 请求方式 */
    @Excel(name = "请求方式")
    @Column(name="request_method", length=10)
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @Column(name="operator_type", length=1)
    private Integer operatorType;

    /** 操作人员 */
    @Excel(name = "操作人员")
    @Column(name="oper_name", length=50)
    private String operName;

    /** 部门名称 */
    @Excel(name = "部门名称")
    @Column(name="dept_name", length=50)
    private String deptName;

    /** 请求url */
    @Excel(name = "请求url")
    @Column(name="oper_url", length=255)
    private String operUrl;

    /** 操作地址 */
    @Excel(name = "操作地址")
    @Column(name="oper_ip", length=50)
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点")
    @Column(name="oper_location", length=255)
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "请求参数")
    @Column(name="oper_param", length=2000)
    private String operParam;

    /** 返回参数 */
    @Excel(name = "返回参数")
    @Column(name="json_result", length=2000)
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @Column(name="status", length=1)
    private Integer status;

    /** 错误消息 */
    @Column(name="error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    /** 操作时间 */
    @Excel(name = "操作时间",dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @Column(name="oper_time")
    private Date operTime;
}

