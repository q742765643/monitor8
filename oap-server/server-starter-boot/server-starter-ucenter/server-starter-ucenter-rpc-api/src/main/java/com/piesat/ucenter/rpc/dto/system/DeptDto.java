package com.piesat.ucenter.rpc.dto.system;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 9:33
 */
@Data
public class DeptDto extends BaseDto {
    /**
     * 父部门ID
     */
    @ApiModelProperty(value = "父部门ID")
    private String parentId;

    /**
     * 祖级列表
     */
    @ApiModelProperty(value = "祖级列表")
    private String ancestors;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private int orderNum;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    private String leader;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    @ApiModelProperty(value = "部门状态:0正常,1停用")
    private String status;

    /**
     * 父部门名称
     */
    @ApiModelProperty(value = "父部门名称")
    private String parentName;
    @ApiModelProperty(hidden = true)
    private List<DeptDto> children = new ArrayList<>();
}
