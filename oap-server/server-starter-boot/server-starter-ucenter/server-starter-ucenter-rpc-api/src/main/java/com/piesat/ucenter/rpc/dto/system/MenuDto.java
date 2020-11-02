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
 * @创建时间 2019/11/28 15:37
 */
@Data
public class MenuDto extends BaseDto {
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    /**
     * 父菜单名称
     */
    @ApiModelProperty(value = "父菜单名称")
    private String parentName;

    /**
     * 父菜单ID
     */
    @ApiModelProperty(value = "父菜单ID")
    private String parentId;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private int orderNum;

    /**
     * 路由地址
     */
    @ApiModelProperty(value = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @ApiModelProperty(value = "组件路径")
    private String component;

    /**
     * 是否为外链（0是 1否）
     */
    @ApiModelProperty(value = "是否为外链（0是 1否）")
    private int isFrame = 1;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    @ApiModelProperty(value = "类型（M目录 C菜单 F按钮）")
    private String menuType;

    /**
     * 菜单状态:0显示,1隐藏
     */
    @ApiModelProperty(value = "菜单状态:0显示,1隐藏")
    private String visible = "0";

    /**
     * 权限字符串
     */
    @ApiModelProperty(value = "权限字符串")
    private String perms;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String icon;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(hidden = true)
    private List<MenuDto> children = new ArrayList<>();
}
