package com.piesat.ucenter.rpc.dto.system;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/21 17:02
 */
@Data
@ApiModel("user")
public class UserDto extends BaseDto {

    @ApiModelProperty("部门id")
    private String deptId;

    @ApiModelProperty("用户账号")
    private String userName;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("用户类型（00系统用户）")
    private String userType;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("手机号码")
    private String phonenumber;

    @ApiModelProperty("用户性别（0男 1女 2未知）")
    private String sex;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("帐号状态（0正常 1停用）")
    private String status;


    @ApiModelProperty("最后登陆ip")
    private String loginIp;

    @ApiModelProperty("最后登陆时间")
    private Date loginDate;

    @ApiModelProperty("创建者")
    private String createBy;


    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("备注")
    private String remark;

    private String pdfPath;

    private String appId;

    //@JsonIgnore
    private String params;

    private String[] roleIds;

    private DeptDto dept;

    private String loginLocation;

    private String browser;

    private String os;

    private String tokenId;

    private int operatorType;

    /**
     * 角色对象
     */
    private List<RoleDto> roles;


    /*注册用户属性*/

    private String webUserId;
    /**
     * 业务类型
     */
    @ApiModelProperty("业务类型")
    private String bizType;
    /**
     * 绑定ip
     */
    @ApiModelProperty("绑定ip")
    private String bizIp;
    /**
     * 有效时间
     */
    @ApiModelProperty("有效时间")
    private Date validTime;
    /**
     * 责任人姓名
     */
    @ApiModelProperty("责任人姓名")
    private String webUsername;
    /**
     * 系统名称
     */
    @ApiModelProperty("系统名称")
    private String appName;
    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    private String legalUnits;
    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String deptName;
    /**
     * 指导老师姓名
     */
    @ApiModelProperty("指导老师姓名")
    private String tutorName;
    /**
     * 指导老师电话
     */
    @ApiModelProperty("指导老师电话")
    private String tutorPhone;
    /**
     * 申请材料
     */
    @ApiModelProperty("申请材料")
    private String applyPaper;
    /**
     * 申请时间
     */
    @ApiModelProperty("申请时间")
    private Date applyTime;

    /**
     * 最后编辑时间
     */
    @ApiModelProperty("最后编辑时间")
    private Date lastEditTime;

    /**
     * 审核状态
     * 0：待审核
     * 1：审核通过
     * 2：驳回
     * 3：激活
     * 4：注销
     */
    @ApiModelProperty("审核状态")
    private String checked;


    /**
     * 存储系统管理接口权限
     */
    @ApiModelProperty("存储系统管理接口权限")
    private String sodApi;

    /**
     * 是否申请资料
     */
    private String sodData;
    /**
     * 存储系统数据库申请
     */
    @ApiModelProperty("存储系统数据库申请")
    private String dbIds;
    /**
     * 是否创建专题库 “1” 是 “0” 否
     */
    @ApiModelProperty("是否创建专题库")
    private String dbCreate;

    @ApiModelProperty("拒绝原因")
    private String reason;
}
