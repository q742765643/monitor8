package com.piesat.skywalking.web.discovery;

import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@Api(value = "自动发现接口", tags = {"自动发现接口"})
@RequestMapping("/autoDiscovery")
public class AutoDiscoveryController {
    @Autowired
    private AutoDiscoveryService autoDiscoveryService;

    @ApiOperation(value = "分页查询自动发现任务", notes = "分页查询自动发现任务")
    @GetMapping("/list")
    public ResultT<PageBean<AutoDiscoveryDto>> list(AutoDiscoveryDto autoDiscoveryDto, int pageNum, int pageSize) {
        ResultT<PageBean<AutoDiscoveryDto>> resultT = new ResultT<>();
        PageForm<AutoDiscoveryDto> pageForm = new PageForm<>(pageNum, pageSize, autoDiscoveryDto);
        PageBean pageBean = autoDiscoveryService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询发现任务", notes = "根据ID查询发现任务")
    @GetMapping(value = "/{id}")
    public ResultT<AutoDiscoveryDto> getInfo(@PathVariable String id) {
        ResultT<AutoDiscoveryDto> resultT = new ResultT<>();
        AutoDiscoveryDto discoveryDto = autoDiscoveryService.findById(id);
        resultT.setData(discoveryDto);
        return resultT;
    }

    @ApiOperation(value = "添加或者修改发现任务", notes = "添加或者修改发现任务")
    @PostMapping
    public ResultT<String> add(@RequestBody AutoDiscoveryDto autoDiscoveryDto) {
        ResultT<String> resultT = new ResultT<>();
        autoDiscoveryService.save(autoDiscoveryDto);
        return resultT;
    }

    @ApiOperation(value = "删除发现任务", notes = "删除发现任务")
    @DeleteMapping("/{ids}")
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        autoDiscoveryService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }
}
