package com.piesat.skywalking.web.host;

import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    public ResultT<String> add(@RequestBody HostConfigDto hostConfigDto) {
        ResultT<String> resultT = new ResultT<>();
        hostConfigService.save(hostConfigDto);
        return resultT;
    }

    @ApiOperation(value = "删除主机", notes = "删除主机")
    @DeleteMapping("/{ids:.+}")
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        hostConfigService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }
}
