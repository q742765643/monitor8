package com.piesat.ucenter.web.controller.monitor;

import com.piesat.common.annotation.HtParam;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.monitor.LoginInfoService;
import com.piesat.ucenter.rpc.dto.monitor.LoginInfoDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 15:15
 **/
@RestController
@Api(value = "登录日志接口", tags = {"登录日志接口"})
@RequestMapping("/monitor/logininfor")
public class LoginInfoController {
    @Autowired
    private LoginInfoService loginInfoService;

    @ApiOperation(value = "分页查询登录日志接口", notes = "分页查询登录日志接口")
    @RequiresPermissions("monitor:logininfor:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(LoginInfoDto logininfor, @HtParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @HtParam(value = "pageSize", defaultValue = "10") int pageSize) {
        ResultT<PageBean> resultT = new ResultT<>();
        PageForm<LoginInfoDto> pageForm = new PageForm(pageNum, pageSize, logininfor);
        PageBean pageBean = loginInfoService.selectLogininforList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }


    @ApiOperation(value = "删除登录日志", notes = "删除登录日志")
    @RequiresPermissions("monitor:logininfor:remove")
    @Log(title = "登陆日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public ResultT<String> remove(@PathVariable String[] infoIds) {
        ResultT<String> resultT = new ResultT<>();
        loginInfoService.deleteLogininforByIds(infoIds);
        return resultT;
    }

    @ApiOperation(value = "清除登录日志", notes = "清除登录日志")
    @RequiresPermissions("monitor:logininfor:remove")
    @Log(title = "登陆日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public ResultT<String> clean() {
        ResultT<String> resultT = new ResultT<>();
        loginInfoService.cleanLogininfor();
        return resultT;
    }


    @ApiOperation(value = "登录日志导出", notes = "登录日志导出")
    @RequiresPermissions("monitor:logininfor:export")
    @GetMapping("/export")
    public void exportExcel(LoginInfoDto loginInfoDto) {
        loginInfoService.exportExcel(loginInfoDto);
    }
}

