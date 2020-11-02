package com.piesat.skywalking.web.folder;

import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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
    public ResultT<String> add(@RequestBody FileMonitorDto fileMonitorDto) {
        ResultT<String> resultT = new ResultT<>();
        fileMonitorService.save(fileMonitorDto);
        return resultT;
    }

    @ApiOperation(value = "删除文件监控", notes = "删除文件监控")
    @DeleteMapping("/{ids}")
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        fileMonitorService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "文件正则校验", notes = "文件正则校验")
    @PostMapping("/regularCheck")
    public ResultT<String> regularCheck(@RequestBody FileMonitorDto fileMonitorDto) {
        ResultT<String> resultT = new ResultT<>();
        boolean flag = fileMonitorService.regularCheck(fileMonitorDto);
        if (!flag) {
            resultT.setErrorMessage("正则校验不通过请重新配置");
        }
        return resultT;
    }
}
