package com.piesat.skywalking.web.main;

import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.service.main.MainService;
import com.piesat.skywalking.vo.AlarmDistributionVo;
import com.piesat.skywalking.vo.MonitorViewVo;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(value = "首页", tags = {"首页"})
@RequestMapping("/main")
public class MainController {
    @Autowired
    private MainService mainService;

    @ApiOperation(value = "查询监控总览", notes = "查询监控总览")
    @GetMapping("/getMonitorViewVo")
    public ResultT<List<MonitorViewVo>> getMonitorViewVo() {
        ResultT<List<MonitorViewVo>> resultT = new ResultT<>();
        List<MonitorViewVo> list = mainService.getMonitorViewVo();
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询设备状态", notes = "查询设备状态")
    @GetMapping("/getDeviceStatus")
    public ResultT<List<Map<String, Object>> > getDeviceStatus(HostConfigDto hostConfigdto) {
        ResultT<List<Map<String, Object>> > resultT = new ResultT<>();
        List<Map<String, Object>>  list = mainService.getDeviceStatus(hostConfigdto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询未处理告警", notes = "查询未处理告警")
    @GetMapping("/getAlarm")
    public ResultT<List<AlarmLogDto>> getAlarm(AlarmLogDto query) {
        ResultT<List<AlarmLogDto>> resultT = new ResultT<>();
        List<AlarmLogDto> alarmLogDtos=mainService.getAlarm(query);
        resultT.setData(alarmLogDtos);
        return resultT;
    }


    @ApiOperation(value = "查询告警分布", notes = "查询告警分布")
    @GetMapping("/getAlarmDistribution")
    public ResultT<List<AlarmDistributionVo>> getAlarmDistribution(AlarmLogDto alarmLogDto) {
        ResultT<List<AlarmDistributionVo>> resultT = new ResultT<>();
        List<AlarmDistributionVo> list = mainService.getAlarmDistribution(alarmLogDto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询数据状态", notes = "查询数据状态")
    @GetMapping("/getFileStatus")
    public ResultT<Map<String,Object>> getFileStatus() {
        ResultT<Map<String,Object>> resultT = new ResultT<>();
        Map<String,Object> list = mainService.getFileStatus();
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询进程热力图", notes = "查询进程热力图")
    @GetMapping("/getProcess")
    public ResultT<Map<String,Object>> getProcess() {
        ResultT<Map<String,Object>> resultT = new ResultT<>();
        Map<String,Object> map = mainService.getProcess();
        resultT.setData(map);
        return resultT;
    }
}
