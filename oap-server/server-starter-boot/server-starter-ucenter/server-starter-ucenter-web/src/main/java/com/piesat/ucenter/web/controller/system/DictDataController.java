package com.piesat.ucenter.web.controller.system;

import com.piesat.common.annotation.HtParam;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.system.DictDataService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.ucenter.rpc.dto.system.DictTypeDto;
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
 * @创建时间 2019/12/3 18:17
 */
@RestController
@Api(value="字典数据操作接口",tags={"字典数据操作接口"})
@RequestMapping("/system/dict/data")
public class DictDataController {
    @Autowired
    private DictDataService dictDataService;
    @ApiOperation(value = "分页查询字典数据", notes = "分页查询字典数据")
    @RequiresPermissions("system:dict:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(DictDataDto dictData,
                                  @HtParam(value="pageNum",defaultValue="1") int pageNum,
                                  @HtParam(value="pageSize",defaultValue="10") int pageSize)
    {
        ResultT<PageBean> resultT=new ResultT();
        PageForm<DictDataDto> pageForm=new PageForm<>(pageNum,pageSize,dictData);
        PageBean pageBean=dictDataService.selectDictDataList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
    /**
     * 查询字典数据详细
     */
    @ApiOperation(value = "查询字典数据详细", notes = "查询字典数据详细")
    @RequiresPermissions("system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public ResultT<DictDataDto> getInfo(@PathVariable String dictCode)
    {
        ResultT<DictDataDto> resultT=new ResultT<>();
        DictDataDto dictDataDto=dictDataService.selectDictDataById(dictCode);
        resultT.setData(dictDataDto);
        return resultT;
    }
    /**
     * 根据字典类型查询字典数据信息
     */
    @ApiOperation(value = "根据字典类型查询字典数据信息", notes = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/dictType/{dictType}")
    @RequiresPermissions("system:dict:dictType")
    public ResultT<List<DictDataDto>> dictType(@PathVariable String dictType)
    {
        ResultT<List<DictDataDto>> resultT=new ResultT<>();
        List<DictDataDto> dictDataDtoList=dictDataService.selectDictDataByType(dictType);
        resultT.setData(dictDataDtoList);
        return resultT;
    }
    /**
     * 新增字典类型
     */
    @ApiOperation(value = "新增字典数据", notes = "新增字典数据")
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<DictDataDto> add(@RequestBody DictDataDto dict)
    {
        ResultT<DictDataDto> resultT=new ResultT<>();
        DictDataDto dictDataDto=dictDataService.insertDictData(dict);
        resultT.setData(dictDataDto);
        return resultT;
    }

    /**
     * 修改保存字典类型
     */
    @ApiOperation(value = "修改保存字典数据", notes = "修改保存字典数据")
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT<DictDataDto> edit(@RequestBody DictDataDto dict)
    {
        ResultT<DictDataDto> resultT=new ResultT<>();
        DictDataDto dictDataDto=dictDataService.updateDictData(dict);
        resultT.setData(dictDataDto);
        return resultT;
    }

    /**
     * 删除字典类型
     */
    @ApiOperation(value = "修删除字典数据", notes = "修删除字典数据")
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public  ResultT<String> remove(@PathVariable String[] dictCodes)
    {
        ResultT<String> resultT=new ResultT<>();
        List<String> list=new ArrayList();
        if(dictCodes.length>0){
            list= Arrays.asList(dictCodes);
            dictDataService.deleteDictDataByIds(list);
        }
        return resultT;
    }

    @ApiOperation(value = "字典数据导出", notes = "字典数据导出")
    @RequiresPermissions("system:dict:export")
    @GetMapping("/export")
    public void exportExcel(DictDataDto dictDataDto){
        dictDataService.exportExcel(dictDataDto);
    }
}
