package com.piesat.skywalking.web.discovery;

import com.piesat.skywalking.api.discover.NetIpService;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.dto.NetIpDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName : NetIpController
 * @Description :
 * @Author : zzj
 * @Date: 2021-03-29 17:13
 */
@RestController
@Api(value = "全网ip接口", tags = {"全网ip接口"})
@RequestMapping("/netIp")
public class NetIpController {
    @Autowired
    private NetIpService netIpService;
    @ApiOperation(value = "分页查询ip", notes = "分页查询ip")
    @GetMapping("/list")
    public ResultT<PageBean<NetIpDto>> list(NetIpDto netIpDto, int pageNum, int pageSize) {
        ResultT<PageBean<NetIpDto>> resultT = new ResultT<>();
        PageForm<NetIpDto> pageForm = new PageForm<>(pageNum, pageSize, netIpDto);
        PageBean pageBean = netIpService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
}

