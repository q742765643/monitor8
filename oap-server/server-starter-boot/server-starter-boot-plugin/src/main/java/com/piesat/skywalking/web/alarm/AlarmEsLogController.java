package com.piesat.skywalking.web.alarm;

import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "告警日志查询接口", tags = {"告警日志查询接口"})
@RequestMapping("/alarmEsLog")
public class AlarmEsLogController {
    @Autowired
    private AlarmEsLogService alarmEsLogService;

    @ApiOperation(value = "分页查询告警日志", notes = "分页查询告警日志")
    @GetMapping("/list")
    public ResultT<PageBean<AlarmLogDto>> list(AlarmLogDto alarmLogDto, int pageNum, int pageSize) {
        ResultT<PageBean<AlarmLogDto>> resultT = new ResultT<>();
        PageForm<AlarmLogDto> pageForm = new PageForm<>(pageNum, pageSize, alarmLogDto);
        PageBean pageBean = alarmEsLogService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
    @ApiOperation(value = "查询告警趋势", notes = "查询告警趋势")
    @GetMapping("/selectAlarmTrend")
    public ResultT<Map<String,Object>> selectAlarmTrend(AlarmLogDto query){
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        Map<String,Object> map=alarmEsLogService.selectAlarmTrend(query);
        resultT.setData(map);
        return resultT;
    }
    @ApiOperation(value = "查询告警级别统计", notes = "查询告警级别统计")
    @GetMapping("/selectAlarmLevel")
    public ResultT<List<Map<String,Object>>> selectAlarmLevel(AlarmLogDto query){
        ResultT<List<Map<String,Object>>> resultT=new ResultT<>();
        List<Map<String,Object>> list=alarmEsLogService.selectAlarmLevel(query);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询告警时间轴", notes = "查询告警时间轴")
    @GetMapping("/selectAlarmList")
    public ResultT<List<Map<String,Object>>> selectAlarmList(AlarmLogDto query){
        ResultT<List<Map<String,Object>>> resultT=new ResultT<>();
        List<Map<String,Object>> list=alarmEsLogService.selectAlarmList(query);
        resultT.setData(list);
        return resultT;
    }
    @ApiOperation(value = "查询告警设备数量", notes = "查询告警设备数量")
    @GetMapping("/selectAlarmNum")
    public ResultT<Map<String,Long>> selectAlarmNum(AlarmLogDto query){
        ResultT<Map<String,Long>> resultT=new ResultT<>();
        Map<String,Long> map=alarmEsLogService.selectAlarmNum(query);
        resultT.setData(map);
        return resultT;
    }
}
