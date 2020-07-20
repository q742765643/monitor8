package com.piesat.ucenter.web.controller.system;

import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.system.RoleService;
import com.piesat.ucenter.rpc.dto.system.DictTypeDto;
import com.piesat.ucenter.rpc.dto.system.RoleDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/4 15:03
 */
@RestController
@Api(value="角色操作接口",tags={"角色操作接口"})
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @RequiresPermissions("system:role:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(RoleDto role, int pageNum, int pageSize)
    {
        ResultT<PageBean> resultT=new ResultT<>();
        PageForm<RoleDto> pageForm=new PageForm<>(pageNum,pageSize,role);
        PageBean pageBean=roleService.selectRoleList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    /**
     * 根据角色编号获取详细信息
     */
    @RequiresPermissions("system:role:query")
    @GetMapping(value = "/{roleId}")
    public ResultT<RoleDto> getInfo(@PathVariable String roleId)
    {
        ResultT<RoleDto> resultT=new ResultT<>();
        RoleDto roleDto=roleService.selectRoleById(roleId);
        resultT.setData(roleDto);
        return resultT;
    }
    /**
     * 新增角色
     */
    @RequiresPermissions("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<String> add(@RequestBody RoleDto role)
    {
      /*  if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
        {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(SecurityUtils.getUsername());*/
        ResultT<String> resultT=new ResultT<>();
        roleService.insertRole(role);
        return resultT;

    }

    /**
     * 修改保存角色
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT<String> edit(@RequestBody RoleDto role)
    {
       /* roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
        {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(SecurityUtils.getUsername());*/
        ResultT<String> resultT=new ResultT<>();
        roleService.authDataScope(role);
        return resultT;
    }
    /**
     * 删除角色
     */
    @RequiresPermissions("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public ResultT<String> remove(@PathVariable String[] roleIds)
    {
        ResultT<String> resultT=new ResultT<>();
        List<String> list=new ArrayList();
        if(roleIds.length>0){
            list= Arrays.asList(roleIds);
            roleService.deleteRoleByIds(list);
        }
        return resultT;
    }
    /**
     * 状态修改
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public ResultT<RoleDto> changeStatus(@RequestBody RoleDto role)
    {
        //roleService.checkRoleAllowed(role);
        ResultT<RoleDto> resultT=new ResultT<>();
        RoleDto roleDto=roleService.updateRoleStatus(role);
        resultT.setData(roleDto);
        return resultT;
    }
    /**
     * 获取角色选择框列表
     */
    @GetMapping("/optionselect")
    @RequiresPermissions("system:role:optionselect")
    public ResultT<List<RoleDto>> optionselect()
    {
        ResultT<List<RoleDto>> resultT=new ResultT<>();
        List<RoleDto> roleDtos=roleService.selectRoleAll();
        resultT.setData(roleDtos);
        return resultT;
    }
    @ApiOperation(value = "角色信息导出", notes = "角色信息导出")
    @RequiresPermissions("system:role:export")
    @GetMapping("/export")
    public void exportExcel(RoleDto roleDto){
        roleService.exportExcel(roleDto);
    }

}
