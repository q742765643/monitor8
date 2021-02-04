package com.piesat.skywalking.web.folder;

import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Api(value = "文件监控接口", tags = {"文件监控接口"})
@RequestMapping("/fileMonitor")
public class FileMonitorController {
    @Autowired
    private FileMonitorService fileMonitorService;

    @ApiOperation(value = "分页查询文件监控", notes = "分页查询文件监控")
    @GetMapping("/list")
    public ResultT<PageBean<FileMonitorDto>> list(FileMonitorDto fileMonitorDto, int pageNum, int pageSize) {
        ResultT<PageBean<FileMonitorDto>> resultT = new ResultT<>();
        PageForm<FileMonitorDto> pageForm = new PageForm<>(pageNum, pageSize, fileMonitorDto);
        PageBean pageBean = fileMonitorService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询文件监控", notes = "根据ID查询文件监控")
    @GetMapping(value = "/{id}")
    public ResultT<FileMonitorDto> getInfo(@PathVariable String id) {
        ResultT<FileMonitorDto> resultT = new ResultT<>();
        FileMonitorDto fileMonitorDto = fileMonitorService.findById(id);
        resultT.setData(fileMonitorDto);
        return resultT;
    }

    @ApiOperation(value = "添加或者修改文件监控", notes = "添加或者修改文件监控")
    @PostMapping
    @RequiresPermissions("fileMonitor:fileMonitor:add")
    @Log(title = "文件监控管理", businessType = BusinessType.INSERT)
    public ResultT<String> add(@RequestBody FileMonitorDto fileMonitorDto) {
        ResultT<String> resultT = new ResultT<>();
        fileMonitorService.save(fileMonitorDto);
        return resultT;
    }

    @ApiOperation(value = "删除文件监控", notes = "删除文件监控")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("fileMonitor:fileMonitor:remove")
    @Log(title = "文件监控管理", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        fileMonitorService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "文件正则校验", notes = "文件正则校验")
    @PostMapping("/regularCheck")
    public ResultT<String> regularCheck(@RequestBody FileMonitorDto fileMonitorDto) {
        ResultT<String> resultT = new ResultT<>();
        boolean flag = fileMonitorService.regularCheck(fileMonitorDto,resultT);
        if (!flag) {
            resultT.setErrorMessage("正则校验不通过请重新配置");
        }
        return resultT;
    }

    @ApiOperation(value = "启动停止", notes = "启动停止")
    @PostMapping("/updateFileMonitor")
    @RequiresPermissions("fileMonitor:fileMonitor:updateFileMonitor")
    @Log(title = "文件监控管理", businessType = BusinessType.UPDATE)
    public ResultT<String> updateFileMonitor(@RequestBody FileMonitorDto fileMonitorDto) {
        ResultT<String> resultT = new ResultT<>();
        fileMonitorService.updateFileMonitor(fileMonitorDto);
        return resultT;
    }

    @ApiOperation(value = "立即执行", notes = "立即执行")
    @GetMapping("/trigger/{id:.+}")
    @RequiresPermissions("fileMonitor:fileMonitor:trigger")
    public ResultT<String> trigger(@PathVariable("id") String id){
        ResultT<String> resultT = new ResultT<>();
        fileMonitorService.trigger(id);
        return resultT;
    }
}
