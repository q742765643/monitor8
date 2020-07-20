package com.piesat.ucenter.web.controller.system;

import com.piesat.common.annotation.HtParam;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.system.DictTypeService;
import com.piesat.ucenter.rpc.dto.system.DictTypeDto;
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
 * @创建时间 2019/12/3 16:10
 */
@RestController
@Api(value="字段类型操作接口",tags={"字段类型操作接口"})
@RequestMapping("/system/dict/type")
public class DictTypeController {
    @Autowired
    private DictTypeService dictTypeService;
    @RequiresPermissions("system:dict:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(DictTypeDto dictType ,
                                  @HtParam(value="pageNum",defaultValue="1") int pageNum,
                                  @HtParam(value="pageSize",defaultValue="10") int pageSize)
    {
        ResultT<PageBean> resultT=new ResultT<>();
        if(dictType==null){
            dictType =  new DictTypeDto();
        }
        PageForm<DictTypeDto> pageForm=new PageForm<>(pageNum,pageSize,dictType);
        PageBean pageBean=dictTypeService.selectDictTypeList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
    /**
     * 查询字典类型详细
     */
    @RequiresPermissions("system:dict:query")
    @GetMapping(value = "/{dictId}")
    public ResultT<DictTypeDto> getInfo(@PathVariable String dictId)
    {
        ResultT<DictTypeDto> resultT=new ResultT<>();
        DictTypeDto dictTypeDto=dictTypeService.selectDictTypeById(dictId);
        resultT.setData(dictTypeDto);
        return resultT;
    }
    /**
     * 新增字典类型
     */
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<DictTypeDto> add(@RequestBody DictTypeDto dict)
    {
  /*      if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict)))
        {
            return AjaxResult.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());*/
        ResultT<DictTypeDto> resultT=new ResultT<>();
        DictTypeDto dictTypeDto=dictTypeService.insertDictType(dict);
        resultT.setData(dictTypeDto);
        return resultT;
    }
    /**
     * 修改字典类型
     */
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT<DictTypeDto> edit(@RequestBody DictTypeDto dict)
    {
     /*   if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict)))
        {
            return AjaxResult.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());*/
        ResultT<DictTypeDto> resultT=new ResultT<>();
        DictTypeDto dictTypeDto=dictTypeService.updateDictType(dict);
        resultT.setData(dictTypeDto);
        return resultT;
    }

    /**
     * 删除字典类型
     */
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public ResultT<String> remove(@PathVariable String[] dictIds)
    {
        ResultT<String> resultT=new ResultT<>();
        List<String> list=new ArrayList();
        if(dictIds.length>0){
            list= Arrays.asList(dictIds);
            dictTypeService.deleteDictTypeByIds(list);
        }
        return resultT;
    }

    @ApiOperation(value = "字典类型导出", notes = "字典类型导出")
    @RequiresPermissions("system:dict:export")
    @GetMapping("/export")
    public void exportExcel(DictTypeDto dictTypeDto){
        dictTypeService.exportExcel(dictTypeDto);
    }
}
