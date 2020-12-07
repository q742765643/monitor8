package com.piesat.skywalking.web.discovery;

import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
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
    @RequiresPermissions("discovery:autoDiscovery:add")
    @Log(title = "自动发现", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<String> add(@RequestBody AutoDiscoveryDto autoDiscoveryDto) {
        ResultT<String> resultT = new ResultT<>();
        autoDiscoveryService.save(autoDiscoveryDto);
        return resultT;
    }

    @ApiOperation(value = "删除发现任务", notes = "删除发现任务")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("discovery:autoDiscovery:remove")
    @Log(title = "自动发现", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        autoDiscoveryService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "启动停止接口", notes = "启动停止接口")
    @PostMapping("/updateAutoDiscovery")
    @RequiresPermissions("discovery:autoDiscovery:updateAutoDiscovery")
    @Log(title = "自动发现", businessType = BusinessType.UPDATE)
    public ResultT<String> updateAutoDiscovery(@RequestBody AutoDiscoveryDto autoDiscoveryDto) {
        ResultT<String> resultT = new ResultT<>();
        autoDiscoveryService.updateAutoDiscovery(autoDiscoveryDto);
        return resultT;
    }

    @ApiOperation(value = "立即执行", notes = "立即执行")
    @GetMapping("/trigger/{id:.+}")
    @RequiresPermissions("discovery:autoDiscovery:trigger")
    public ResultT<String> trigger(@PathVariable("id") String id){
        ResultT<String> resultT = new ResultT<>();
        autoDiscoveryService.trigger(id);
        return resultT;
    }
}
