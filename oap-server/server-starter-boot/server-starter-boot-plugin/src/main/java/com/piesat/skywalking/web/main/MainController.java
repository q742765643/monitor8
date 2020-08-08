package com.piesat.skywalking.web.main;

import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.service.main.MainService;
import com.piesat.skywalking.vo.AlarmDistributionVo;
import com.piesat.skywalking.vo.MonitorViewVo;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "首页", tags = {"首页"})
@RequestMapping("/main")
public class MainController {
    @Autowired
    private MainService mainService;
    @ApiOperation(value = "查询监控总览", notes = "查询监控总览")
    @GetMapping("/getMonitorViewVo")
    public ResultT<List<MonitorViewVo>> getMonitorViewVo(){
        ResultT< List<MonitorViewVo>> resultT=new ResultT<>();
        List<MonitorViewVo> list=mainService.getMonitorViewVo();
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询设备状态", notes = "查询设备状态")
    @GetMapping("/getDeviceStatus")
    public ResultT<List<HostConfigDto>> getDeviceStatus(HostConfigDto hostConfigdto){
        ResultT< List<HostConfigDto>> resultT=new ResultT<>();
        List<HostConfigDto> list=mainService.getDeviceStatus(hostConfigdto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询未处理告警", notes = "查询未处理告警")
    @GetMapping("/getAlarm")
    public ResultT<PageBean> getAlarm(AlarmLogDto alarmLogDto, int pageNum, int pageSize){
        ResultT< PageBean> resultT=new ResultT<>();
        PageForm<AlarmLogDto> pageForm=new PageForm<AlarmLogDto>(pageNum,pageSize,alarmLogDto);
        PageBean pageBean=mainService.getAlarm(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }


    @ApiOperation(value = "查询告警分布", notes = "查询告警分布")
    @GetMapping("/getAlarmDistribution")
    public ResultT<List<AlarmDistributionVo>> getAlarmDistribution(AlarmLogDto alarmLogDto){
        ResultT<List<AlarmDistributionVo>> resultT=new ResultT<>();
        List<AlarmDistributionVo> list=mainService.getAlarmDistribution(alarmLogDto);
        resultT.setData(list);
        return resultT;
    }

}
