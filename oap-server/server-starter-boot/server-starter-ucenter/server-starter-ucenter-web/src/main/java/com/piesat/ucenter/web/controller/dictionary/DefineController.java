package com.piesat.ucenter.web.controller.dictionary;

import com.piesat.ucenter.rpc.api.dictionary.DefineService;
import com.piesat.ucenter.rpc.dto.dictionary.DefineDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yaya
 * @description 区域类别管理
 * @date 2019/12/26 16:37
 */
@RestController
@RequestMapping("/dictionary/define")
@Api(value = "区域类别管理controller", tags = {"区域类别管理接口"})
public class DefineController {
    @Autowired
    private DefineService defineService;

    /**
     * 获取分页数据接口
     *
     * @param defineDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "条件分页查询", notes = "条件分页查询")
    public ResultT<PageBean> list(DefineDto defineDto, int pageNum, int pageSize) {
        ResultT<PageBean> resultT = new ResultT<>();
        PageForm<DefineDto> pageForm = new PageForm<>(pageNum, pageSize, defineDto);
        PageBean pageBean = defineService.selectDefineList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    /**
     * 添加
     *
     * @param defineDto
     * @return
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "添加区域类别接口", notes = "添加区域类别接口")
    public ResultT save(@RequestBody DefineDto defineDto) {
        try {
            DefineDto save = this.defineService.saveDto(defineDto);
            return ResultT.success(save);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultT.failed(e.getMessage());
        }
    }

    /**
     * 批量删除
     *
     * @param defineIds
     * @return
     */
    @DeleteMapping("/{defineIds}")
    public ResultT<String> remove(@PathVariable String[] defineIds) {
        ResultT<String> resultT = new ResultT<>();
        List<String> list = new ArrayList();
        if (defineIds.length > 0) {
            list = Arrays.asList(defineIds);
            defineService.deleteDefineByIds(list);
        }
        return resultT;
    }

    @PutMapping("/edit")
    public ResultT<DefineDto> edit(@RequestBody DefineDto defineDto) {
        ResultT<DefineDto> resultT = new ResultT<>();
        defineDto = defineService.updateDefine(defineDto);
        resultT.setData(defineDto);
        return resultT;
    }

    @GetMapping(value = "/{defineId}")
    public ResultT<DefineDto> getDefineById(@PathVariable String defineId) {
        ResultT<DefineDto> resultT = new ResultT<>();
        DefineDto defineDto = defineService.getDotById(defineId);
        resultT.setData(defineDto);
        return resultT;
    }

    @ApiOperation(value = "查询所有")
    @RequiresPermissions("dictionary:define:all")
    @GetMapping(value = "/all")
    public ResultT all() {
        try {
            List<DefineDto> all = this.defineService.all();
            return ResultT.success(all);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultT.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "区域类别管理导出")
    @RequiresPermissions("dictionary:define:exportTable")
    @GetMapping("/exportTable")
    public void exportExcel(DefineDto defineDto) {
        defineService.exportExcel(defineDto);
    }
}

