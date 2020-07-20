package com.piesat.ucenter.web.controller.dictionary;

import com.piesat.ucenter.rpc.api.dictionary.LevelService;
import com.piesat.ucenter.rpc.dto.dictionary.LevelDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 层次属性管理
 *
 * @author wangyajuan
 * @date 2019年 12月23日 17:35:27
 */
@RestController
@RequestMapping("/dictionary/level")
@Api(value = "层次属性管理controller", tags = {"层次属性管理接口"})
public class LevelController {
    @Autowired
    private LevelService levelService;

    /**
     * 获取分页数据接口
     * @param levelDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "条件分页查询", notes = "条件分页查询")
    public ResultT<PageBean> list(LevelDto levelDto,int pageNum,int pageSize) {
        ResultT<PageBean> resultT=new ResultT<>();
        PageForm<LevelDto> pageForm=new PageForm<>(pageNum,pageSize,levelDto);
        PageBean pageBean=levelService.selectLevelList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    /**
     * 添加
     * @param levelDto
     * @return
     */
    @PostMapping(value = "/save")
    @ApiOperation(value = "添加层次属性接口", notes = "添加层次属性接口")
    public ResultT save(@RequestBody LevelDto levelDto) {
        try {
            LevelDto save = this.levelService.saveDto(levelDto);
            if(save.getId() == null){
               return  ResultT.failed("相同grid格式的层次类型已存在");
            }
            return ResultT.success(save);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultT.failed(e.getMessage());
        }
    }

    /**
     * 批量删除
     * @param levelIds
     * @return
     */
    @DeleteMapping("/{levelIds}")
    public ResultT<String> remove(@PathVariable String[] levelIds)
    {
        ResultT<String> resultT=new ResultT<>();
        List<String> list=new ArrayList();
        if(levelIds.length>0){
            list= Arrays.asList(levelIds);
            levelService.deleteLevelByIds(list);
        }
        return resultT;
    }

    @PutMapping("/edit")
    public ResultT<LevelDto> edit(@RequestBody LevelDto levelDto)
    {
        ResultT<LevelDto> resultT=new ResultT<>();
        levelDto=levelService.updateLevel(levelDto);
        resultT.setData(levelDto);
        return resultT;
    }

    @GetMapping(value = "/{levelId}")
    public ResultT<LevelDto> getLevelById(@PathVariable String levelId)
    {
        ResultT<LevelDto> resultT=new ResultT<>();
        LevelDto levelDto=levelService.getDotById(levelId);
        resultT.setData(levelDto);
        return resultT;
    }

    @ApiOperation(value = "查询所有层次")
    @RequiresPermissions("dm:dictionary:getAllLevel")
    @GetMapping(value = "/getAllLevel")
    public ResultT getAllLevel(){
        List<LevelDto> allLevel = this.levelService.getAllLevel();
        return ResultT.success(allLevel);
    }
}
