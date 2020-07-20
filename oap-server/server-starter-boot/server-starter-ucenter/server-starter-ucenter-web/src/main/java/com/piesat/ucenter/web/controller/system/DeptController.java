package com.piesat.ucenter.web.controller.system;

import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.system.DeptService;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import com.piesat.ucenter.rpc.util.TreeSelect;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 9:46
 */
@RestController
@Api(value = "部门管理接口", tags = {"部门管理接口"})
@RequestMapping("/system/dept")
public class DeptController {
    @Autowired
    private DeptService deptService;
    /**
     * 获取部门列表
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list")
    public ResultT<List<DeptDto>> list(DeptDto dept)
    {
        ResultT<List<DeptDto>> resultT=new ResultT<>();
        List<DeptDto> deptDtos=deptService.selectDeptList(dept);
        resultT.setData(deptDtos);
        return resultT;
    }
    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public ResultT<List<TreeSelect>> treeselect(DeptDto dept)
    {
        ResultT<List<TreeSelect>> resultT=new ResultT<>();
        List<TreeSelect> treeSelects = deptService.getTreeSelectDept(dept);
        resultT.setData(treeSelects);
        return resultT;
    }
    /**
     * 根据部门编号获取详细信息
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping(value = "/{deptId}")
    public ResultT<DeptDto> getInfo(@PathVariable String deptId)
    {
        ResultT<DeptDto> resultT=new ResultT<>();
        DeptDto deptDto=deptService.selectDeptById(deptId);
        resultT.setData(deptDto);
        return resultT;
    }

    /**
     * 新增部门
     */
    @RequiresPermissions("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<DeptDto> add(@RequestBody DeptDto dept)
    {
       /* if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return AjaxResult.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());*/
        ResultT<DeptDto> resultT=new ResultT<>();
        DeptDto deptDto=deptService.insertDept(dept);
        resultT.setData(deptDto);
        return resultT;
    }
    /**
     * 修改部门
     */
    @RequiresPermissions("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT<DeptDto> edit(@RequestBody DeptDto dept)
    {
    /*    if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(dept.getDeptId()))
        {
            return AjaxResult.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }*/
        ResultT<DeptDto> resultT=new ResultT<>();
        DeptDto deptDto=deptService.updateDept(dept);
        resultT.setData(deptDto);
        return resultT;
    }

    /**
     * 删除部门
     */
    @RequiresPermissions("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public ResultT<String> remove(@PathVariable String deptId)
    {
      /*  if (deptService.hasChildByDeptId(deptId))
        {
            return AjaxResult.error("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return AjaxResult.error("部门存在用户,不允许删除");
        }*/
        ResultT<String> resultT=new ResultT<>();
        deptService.deleteDeptById(deptId);
        return resultT;
    }
}
