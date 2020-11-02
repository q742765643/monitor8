package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.report.DeviceQReportService;
import com.piesat.skywalking.dto.HostConfigDto;
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
 * @ClassName : DeviceQReportController
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-19 10:36
 */
@RestController
@Api(value = "设备报表", tags = {"设备报表"})
@RequestMapping("/report")
public class DeviceQReportController {
    @Autowired
    private DeviceQReportService deviceQReportService;

    @ApiOperation(value = "分页查询设备报表", notes = "分页查询设备报表")
    @GetMapping("/list")
    public ResultT<PageBean<HostConfigDto>> list(HostConfigDto hostConfigDto, int pageNum, int pageSize) {
        ResultT<PageBean<HostConfigDto>> resultT = new ResultT<>();
        PageForm<HostConfigDto> pageForm = new PageForm<>(pageNum, pageSize, hostConfigDto);
        PageBean pageBean = deviceQReportService.getSystemReport(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
}

