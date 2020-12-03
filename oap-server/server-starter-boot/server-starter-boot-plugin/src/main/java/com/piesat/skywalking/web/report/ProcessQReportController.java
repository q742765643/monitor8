package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.report.ProcessQReportService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.ProcessReportDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : ProcessQReportController
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-03 10:23
 */
@RestController
@Api(value = "进程报表", tags = {"进程报表"})
@RequestMapping("/processQReport")
public class ProcessQReportController {
    @Autowired
    private ProcessQReportService processQReportService;
    @ApiOperation(value = "进程报表", notes = "进程报表")
    @GetMapping("/findProcessReport")
    public ResultT<List<ProcessReportDto>> findProcessReport(ProcessConfigDto processConfigDto) {
        ResultT<List<ProcessReportDto>> resultT = new ResultT<>();
        List<ProcessReportDto> list = processQReportService.getProcessReport(processConfigDto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "进程信息导出", notes = "进程信息导出")
    @PostMapping("/exportExcel")
    public void exportExcel(ProcessConfigDto processConfigDto) {
        processQReportService.exportExcel(processConfigDto);
    }

    @ApiOperation(value = "进程信息pdf导出", notes = "进程信息pdf导出")
    @PostMapping("/exportPdf")
    public void exportPdf(ProcessConfigDto processConfigDto) {
        processQReportService.exportPdf(processConfigDto);
    }
}

