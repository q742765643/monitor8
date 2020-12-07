package com.piesat.skywalking.web.folder;

import com.piesat.skywalking.api.folder.FileMonitorLogService;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @ClassName : FileMonitorLogController
 * @Description : 文件日志
 * @Author : zzj
 * @Date: 2020-11-01 15:33
 */
@RestController
@Api(value = "文件监控日志接口", tags = {"文件监控日志接口"})
@RequestMapping("/fileMonitorLog")
public class FileMonitorLogController {
    @Autowired
    private FileMonitorLogService fileMonitorLogService;

    @ApiOperation(value = "分页查询文件监控日志", notes = "分页查询文件监控日志")
    @GetMapping("/list")
    public ResultT<PageBean<FileMonitorLogDto>> list(FileMonitorLogDto fileMonitorLogDto, int pageNum, int pageSize) {
        ResultT<PageBean<FileMonitorLogDto>> resultT = new ResultT<>();
        PageForm<FileMonitorLogDto> pageForm = new PageForm<>(pageNum, pageSize, fileMonitorLogDto);
        PageBean pageBean = fileMonitorLogService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询文件监控日志", notes = "根据ID查询文件监控日志")
    @GetMapping(value = "/{id}")
    public ResultT<FileMonitorLogDto> getInfo(@PathVariable String id) {
        ResultT<FileMonitorLogDto> resultT = new ResultT<>();
        FileMonitorLogDto fileMonitorLogDto = fileMonitorLogService.findById(id);
        resultT.setData(fileMonitorLogDto);
        return resultT;
    }

    @ApiOperation(value = "删除文件共享目录", notes = "删除文件共享目录")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("fileMonitorLog:fileMonitorLog:remove")
    @Log(title = "文件监控日志管理", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        fileMonitorLogService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }
}

