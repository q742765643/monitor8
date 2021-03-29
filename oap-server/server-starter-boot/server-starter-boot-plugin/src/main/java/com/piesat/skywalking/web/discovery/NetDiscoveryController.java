package com.piesat.skywalking.web.discovery;

import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.api.discover.NetDiscoveryService;
import com.piesat.skywalking.dto.NetDiscoveryDto;
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
@Api(value = "全网自动发现接口", tags = {"全网自动发现接口"})
@RequestMapping("/netDiscovery")
public class NetDiscoveryController {
    @Autowired
    private NetDiscoveryService netDiscoveryService;

    @ApiOperation(value = "分页查询自动发现任务", notes = "分页查询自动发现任务")
    @GetMapping("/list")
    public ResultT<PageBean<NetDiscoveryDto>> list(NetDiscoveryDto netDiscoveryDto, int pageNum, int pageSize) {
        ResultT<PageBean<NetDiscoveryDto>> resultT = new ResultT<>();
        PageForm<NetDiscoveryDto> pageForm = new PageForm<>(pageNum, pageSize, netDiscoveryDto);
        PageBean pageBean = netDiscoveryService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询发现任务", notes = "根据ID查询发现任务")
    @GetMapping(value = "/{id}")
    public ResultT<NetDiscoveryDto> getInfo(@PathVariable String id) {
        ResultT<NetDiscoveryDto> resultT = new ResultT<>();
        NetDiscoveryDto discoveryDto = netDiscoveryService.findById(id);
        resultT.setData(discoveryDto);
        return resultT;
    }

    @ApiOperation(value = "添加或者修改发现任务", notes = "添加或者修改发现任务")
    @RequiresPermissions("discovery:netDiscovery:add")
    @Log(title = "自动发现", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<String> add(@RequestBody NetDiscoveryDto netDiscoveryDto) {
        ResultT<String> resultT = new ResultT<>();
        netDiscoveryService.save(netDiscoveryDto);
        return resultT;
    }

    @ApiOperation(value = "删除发现任务", notes = "删除发现任务")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("discovery:netDiscovery:remove")
    @Log(title = "自动发现", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        netDiscoveryService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "启动停止接口", notes = "启动停止接口")
    @PostMapping("/updateAutoDiscovery")
    @RequiresPermissions("discovery:netDiscovery:updateAutoDiscovery")
    @Log(title = "自动发现", businessType = BusinessType.UPDATE)
    public ResultT<String> updateAutoDiscovery(@RequestBody NetDiscoveryDto netDiscoveryDto) {
        ResultT<String> resultT = new ResultT<>();
        netDiscoveryService.updateAutoDiscovery(netDiscoveryDto);
        return resultT;
    }

    @ApiOperation(value = "立即执行", notes = "立即执行")
    @GetMapping("/trigger/{id:.+}")
    @RequiresPermissions("discovery:netDiscovery:trigger")
    public ResultT<String> trigger(@PathVariable("id") String id){
        ResultT<String> resultT = new ResultT<>();
        netDiscoveryService.trigger(id);
        return resultT;
    }
}
