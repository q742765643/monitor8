package com.piesat.ucenter.web.controller.monitor;

import com.piesat.common.annotation.HtParam;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.monitor.OperLogService;
import com.piesat.ucenter.rpc.dto.monitor.OperLogDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-16 15:44
 **/
@RestController
@Api(value="操作日志接口",tags={"操作日志接口"})
@RequestMapping("/monitor/operlog")
public class OperLogController {
    @Autowired
    private OperLogService operLogService;

    @ApiOperation(value = "分页查询操作日志", notes = "分页查询操作日志")
    @RequiresPermissions("monitor:operlog:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(OperLogDto operLog, @HtParam(value="pageNum",defaultValue="1") int pageNum,
                                  @HtParam(value="pageSize",defaultValue="10") int pageSize)
    {
        ResultT<PageBean> resultT=new ResultT<>();
        PageForm<OperLogDto> pageForm=new PageForm(pageNum,pageSize,operLog);
        PageBean pageBean=operLogService.selectOperLogList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "删除操作日志", notes = "删除操作日志")
    @RequiresPermissions("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/{operIds}")
    public ResultT<String> remove(@PathVariable String[] operIds)
    {
        ResultT<String> resultT=new ResultT<>();
        operLogService.deleteOperLogByIds(operIds);
        return resultT;
    }

    @ApiOperation(value = "清除操作日志", notes = "清除操作日志")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @RequiresPermissions("monitor:operlog:remove")
    @DeleteMapping("/clean")
    public ResultT<String> clean()
    {
        ResultT<String> resultT=new ResultT<>();
        operLogService.cleanOperLog();
        return resultT;
    }
    @ApiOperation(value = "日志信息导出", notes = "日志信息导出")
    @RequiresPermissions("monitor:operlog:export")
    @GetMapping("/export")
    public void exportExcel(OperLogDto operLogDto){
        operLogService.exportExcel(operLogDto);
    }

    @ApiOperation(value = "根据用户名模块名称查询日志信息", notes = "根据用户名模块名称查询日志信息")
    @GetMapping("/getLogInfoByOperNameAndTitle")
    public ResultT getLogInfoByOperNameAndTitle(String operName,String title){
        List<OperLogDto> operLogDtos = operLogService.findByOperNameAndTitle(operName, title);
        return ResultT.success(operLogDtos);
    }
}

