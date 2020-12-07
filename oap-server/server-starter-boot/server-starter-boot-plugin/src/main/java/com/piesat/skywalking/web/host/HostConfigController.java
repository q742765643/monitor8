package com.piesat.skywalking.web.host;

import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
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
@Api(value = "主机配置接口", tags = {"主机配置接口"})
@RequestMapping("/hostConfig")
public class HostConfigController {
    @Autowired
    private HostConfigService hostConfigService;

    @ApiOperation(value = "分页查询主机", notes = "分页查询主机")
    @GetMapping("/list")
    public ResultT<PageBean<HostConfigDto>> list(HostConfigDto hostConfigDto, int pageNum, int pageSize) {
        ResultT<PageBean<HostConfigDto>> resultT = new ResultT<>();
        PageForm<HostConfigDto> pageForm = new PageForm<>(pageNum, pageSize, hostConfigDto);
        PageBean pageBean = hostConfigService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询主机", notes = "根据ID查询主机")
    @GetMapping(value = "/{id:.+}")
    public ResultT<HostConfigDto> getInfo(@PathVariable String id) {
        ResultT<HostConfigDto> resultT = new ResultT<>();
        HostConfigDto hostConfigDto = hostConfigService.findById(id);
        resultT.setData(hostConfigDto);
        return resultT;
    }

    @ApiOperation(value = "添加主机或者修改主机", notes = "添加主机或者修改主机")
    @PostMapping
    @RequiresPermissions("hostConfig:hostConfig:add")
    @Log(title = "主机和链路管理", businessType = BusinessType.INSERT)
    public ResultT<String> add(@RequestBody HostConfigDto hostConfigDto) {
        ResultT<String> resultT = new ResultT<>();
        hostConfigService.save(hostConfigDto);
        return resultT;
    }

    @ApiOperation(value = "修改链路或者主机，启动停止", notes = "修改链路或者主机，启动停止")
    @PostMapping("/updateHost")
    @RequiresPermissions("hostConfig:hostConfig:updateHost")
    @Log(title = "主机和链路管理", businessType = BusinessType.UPDATE)
    public ResultT<String> updateHost(@RequestBody HostConfigDto hostConfigDto) {
        ResultT<String> resultT = new ResultT<>();
        hostConfigService.updateHost(hostConfigDto);
        return resultT;
    }

    @ApiOperation(value = "删除主机", notes = "删除主机")
    @DeleteMapping("/{ids:.+}")
    @RequiresPermissions("hostConfig:hostConfig:remove")
    @Log(title = "主机和链路管理", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        hostConfigService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "立即执行", notes = "立即执行")
    @GetMapping("/trigger/{id:.+}")
    @RequiresPermissions("hostConfig:hostConfig:trigger")
    public ResultT<String> trigger(@PathVariable("id") String id){
        ResultT<String> resultT = new ResultT<>();
        hostConfigService.trigger(id);
        return resultT;
    }
}
