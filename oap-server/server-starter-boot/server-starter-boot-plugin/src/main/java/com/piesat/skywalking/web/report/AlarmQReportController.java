package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.report.AlarmQReportService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.excel.AlarmReportVo;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName : AlarmQReportController
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-04 14:19
 */
@RestController
@Api(value = "告警报表", tags = {"告警报表"})
@RequestMapping("/alarmQReport")
public class AlarmQReportController {
    @Autowired
    private AlarmQReportService alarmQReportService;

    @ApiOperation(value = "查询告警报表", notes = "查询告警报表")
    @GetMapping("/getAlarmReport")
    public ResultT<List<AlarmReportVo>> getAlarmReport(AlarmConfigDto alarmConfigDto){
        ResultT<List<AlarmReportVo>> resultT=new ResultT<>();
        List<AlarmReportVo> alarmReportVos= (List<AlarmReportVo>) alarmQReportService.getAlarmReport(alarmConfigDto);
        resultT.setData(alarmReportVos);
        return resultT;
    }
    @ApiOperation(value = "告警信息导出", notes = "告警信息导出")
    @PostMapping("/exportExcel")
    public void exportExcel(AlarmConfigDto alarmConfigDto){
        alarmQReportService.exportExcel(alarmConfigDto);
    }
}

