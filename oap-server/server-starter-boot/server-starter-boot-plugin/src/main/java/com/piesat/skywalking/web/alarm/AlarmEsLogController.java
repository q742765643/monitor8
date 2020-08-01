package com.piesat.skywalking.web.alarm;

import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.dto.AlarmConfigDto;
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

@RestController
@Api(value="告警日志查询接口",tags = {"告警日志查询接口"})
@RequestMapping("/alarmEsLog")
public class AlarmEsLogController {
    @Autowired
    private AlarmEsLogService alarmEsLogService;

    @ApiOperation(value = "分页查询告警日志", notes = "分页查询告警日志")
    @GetMapping("/list")
    public ResultT<PageBean<AlarmLogDto>> list(AlarmLogDto alarmLogDto, int pageNum, int pageSize)
    {
        ResultT<PageBean<AlarmLogDto>> resultT=new ResultT<>();
        PageForm<AlarmLogDto> pageForm=new PageForm<>(pageNum,pageSize,alarmLogDto);
        PageBean pageBean=alarmEsLogService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }
}
