package com.piesat.ucenter.web.controller.system;

import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.entity.system.MenuEntity;
import com.piesat.ucenter.rpc.api.system.MenuService;
import com.piesat.ucenter.rpc.dto.system.MenuDto;
import com.piesat.ucenter.rpc.dto.system.RoleDto;
import com.piesat.ucenter.rpc.util.TreeSelect;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/29 14:46
 */
@RestController
@RequestMapping("/system/menu")
@Api(value="菜单操作接口",tags={"菜单操作接口"})
public class MenuController {
    @Autowired
    private MenuService menuService;
    @ApiOperation(value = "查询所有菜单", notes = "查询所有菜单")
    @RequiresPermissions("system:menu:list")
    @GetMapping("/list")
    public ResultT<List<MenuDto>> list(MenuDto menu)
    {
        ResultT<List<MenuDto>> resultT=new ResultT<>();
        List<MenuDto> menuDtos=menuService.selectMenuList(menu);
        resultT.setData(menuDtos);
        return resultT;
    }
    @ApiOperation(value = "查询菜单树", notes = "查询菜单树")
    @GetMapping("/treeselect")
    public ResultT<List<TreeSelect>> treeselect(MenuDto menu)
    {
        ResultT< List<TreeSelect>> resultT=new ResultT<>();
        List<TreeSelect> treeSelects = menuService.treeSelect(menu);
        resultT.setData(treeSelects);
        return resultT;
    }
    /**
     * 新增菜单
     */
    @ApiOperation(value = "新增菜单", notes = "新增菜单")
    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<MenuDto> add(@RequestBody MenuDto menu)
    {
        ResultT<MenuDto> resultT=new ResultT<>();
        MenuDto menuDto=menuService.insertMenu(menu);
        resultT.setData(menuDto);
        return resultT;
    }
    /**
     * 修改菜单
     */
    @ApiOperation(value = "修改菜单", notes = "修改菜单")
    @RequiresPermissions("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT edit(@RequestBody MenuDto menu)
    {
        ResultT<MenuDto> resultT=new ResultT<>();
        menuService.updateMenu(menu);
        return resultT;
    }
    /**
     * 根据菜单编号获取详细信息
     */
    @ApiOperation(value = "根据菜单编号获取详细信息", notes = "根据菜单编号获取详细信息")
    @RequiresPermissions("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public ResultT<MenuDto> getInfo(@PathVariable String menuId)
    {
        ResultT<MenuDto> resultT=new ResultT<>();
        MenuDto menuDto=menuService.selectMenuById(menuId);
        resultT.setData(menuDto);
        return resultT;
    }

    /**
     * 删除菜单
     */
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    @RequiresPermissions("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public ResultT<String> remove(@PathVariable("menuId") String menuId)
    {
       /* if (menuService.hasChildByMenuId(menuId))
        {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return AjaxResult.error("菜单已分配,不允许删除");
        }*/
        ResultT<String> resultT=new ResultT<>();
        menuService.deleteMenuById(menuId);
        return resultT;
    }
    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation(value = "加载对应角色菜单列表树", notes = "加载对应角色菜单列表树")
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public ResultT<List<String>> roleMenuTreeselect(@PathVariable("roleId") String roleId)
    {
        ResultT<List<String>> resultT=new ResultT<>();
        List<String> list=menuService.selectMenuListByRoleId(roleId);
        resultT.setData(list);
        return resultT;
    }
    @ApiOperation(value = "菜单信息导出", notes = "菜单信息导出")
    @RequiresPermissions("system:role:export")
    @GetMapping("/export")
    public void exportExcel(MenuDto menuDto){
        menuService.exportExcel(menuDto);
    }

}
