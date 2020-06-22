package com.piesat.skywalking.web.discovery;

import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.service.discovery.AutoDiscoveryService;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@Api(value="自动发现接口",tags = {"自动发现接口"})
@RequestMapping("/autoDiscovery")
public class AutoDiscoveryController {
    @Autowired
    private AutoDiscoveryService autoDiscoveryService;
    @ApiOperation(value = "分页查询自动发现任务", notes = "分页查询自动发现任务")
    @GetMapping("/list")
    public ResultT<PageBean<AutoDiscoveryEntity>> list(AutoDiscoveryEntity autoDiscoveryEntity, int pageNum, int pageSize)
    {
        ResultT<PageBean<AutoDiscoveryEntity>> resultT=new ResultT<>();
        PageForm<AutoDiscoveryEntity> pageForm=new PageForm<>(pageNum,pageSize,autoDiscoveryEntity);
        PageBean pageBean=autoDiscoveryService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
    @ApiOperation(value = "根据ID查询发现任务", notes = "根据ID查询发现任务")
    @GetMapping(value = "/{discoveryId}")
    public ResultT<AutoDiscoveryEntity> getInfo(@PathVariable String discoveryId)
    {
        ResultT<AutoDiscoveryEntity> resultT=new ResultT<>();
        AutoDiscoveryEntity discoveryEntity= autoDiscoveryService.getById(discoveryId);
        resultT.setData(discoveryEntity);
        return resultT;
    }
    @ApiOperation(value = "添加或者修改发现任务", notes = "添加或者修改发现任务")
    @PostMapping
    public ResultT<String> add(@RequestBody AutoDiscoveryEntity discoveryEntity)
    {
        ResultT<String> resultT=new ResultT<>();
        autoDiscoveryService.save(discoveryEntity);
        return resultT;
    }

    @ApiOperation(value = "删除发现任务", notes = "删除发现任务")
    @DeleteMapping("/{discoveryIds}")
    public  ResultT<String> remove(@PathVariable String[] discoveryIds)
    {
        ResultT<String> resultT=new ResultT<>();
        autoDiscoveryService.deleteByIds(Arrays.asList(discoveryIds));
        return resultT;
    }
}
