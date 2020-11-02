package com.piesat.skywalking.web.host;

import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.ProcessDetailsDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@Api(value = "进程配置接口", tags = {"进程配置接口"})
@RequestMapping("/processConfig")
public class ProcessConfigController {
    @Autowired
    private ProcessConfigService processConfigService;

    @ApiOperation(value = "分页查询进程", notes = "分页查询进程")
    @GetMapping("/list")
    public ResultT<PageBean<ProcessConfigDto>> list(ProcessConfigDto processConfigDto, int pageNum, int pageSize) {
        ResultT<PageBean<ProcessConfigDto>> resultT = new ResultT<>();
        PageForm<ProcessConfigDto> pageForm = new PageForm<>(pageNum, pageSize, processConfigDto);
        PageBean pageBean = processConfigService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询进程", notes = "根据ID查询进程")
    @GetMapping(value = "/{id}")
    public ResultT<ProcessConfigDto> getInfo(@PathVariable String id) {
        ResultT<ProcessConfigDto> resultT = new ResultT<>();
        ProcessConfigDto processConfigDto = processConfigService.findById(id);
        resultT.setData(processConfigDto);
        return resultT;
    }

    @ApiOperation(value = "添加进程或者修改进程", notes = "添加进程或者修改进程")
    @PostMapping
    public ResultT<String> add(@RequestBody ProcessConfigDto processConfigDto) {
        ResultT<String> resultT = new ResultT<>();
        processConfigService.save(processConfigDto);
        return resultT;
    }

    @ApiOperation(value = "删除进程", notes = "删除进程")
    @DeleteMapping("/{ids}")
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        processConfigService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "查询进程详细信息", notes = "查询进程详细信息")
    @GetMapping("/getDetail")
    public ResultT<ProcessDetailsDto> getDetail(ProcessConfigDto processConfigDto) {
        ResultT<ProcessDetailsDto> resultT = new ResultT<>();
        resultT.setData(processConfigService.getDetail(processConfigDto));
        return resultT;
    }
}
