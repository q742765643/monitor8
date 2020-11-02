package com.htht;

import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.ScheduleApplication;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.AlarmHandler;
import com.piesat.skywalking.handler.AutoDiscoveryHandler;
import com.piesat.util.ResultT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest(classes =ScheduleApplication.class )
public class FileMonitorHandlerTest {
    //@Autowired
    //private FileMonitorHandler fileMonitorHandler;
    @Autowired
    private AutoDiscoveryHandler autoDiscoveryHandler;
    @Autowired
    private AlarmHandler alarmHandler;
    @Test
    public void test() throws Exception {
        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        fileMonitorDto.setFilenameRegular("zc_mwf_height_10_${yyyyMMddHHmm}.\\d{3}");
        fileMonitorDto.setFolderRegular("F:/DQ1055-cp/T799/5.8height${yyyyMMdd}");
        fileMonitorDto.setJobCron("0 0 1 * * ?");
        fileMonitorDto.setTriggerLastTime(new Date().getTime());
        JobContext jobContext=new JobContext();
        jobContext.setHtJobInfoDto(fileMonitorDto);
       // fileMonitorHandler.execute(jobContext,new ResultT<>());
    }
    @Test
    public void test1() throws Exception {
        HostConfigDto hostConfigDto=new HostConfigDto();
        hostConfigDto.setIp("10.1.100.75");
        autoDiscoveryHandler.getHost(hostConfigDto);
    }
    @Test
    public void test2() throws Exception {
        List<ConditionDto> conditionDtos=new ArrayList<>();
        ConditionDto conditionDto=new ConditionDto();
        conditionDto.setParamname("gte");
        conditionDto.setParamvalue("50");
        ConditionDto conditionDto1=new ConditionDto();
        conditionDto1.setParamname("lte");
        conditionDto1.setOperate("and");
        conditionDto1.setParamvalue("60");
        conditionDtos.add(conditionDto);
        conditionDtos.add(conditionDto1);
        JobContext jobContext=new JobContext();
        List<HostConfigDto> ips=new ArrayList<>();
        HostConfigDto hostConfigDto=new HostConfigDto();
        hostConfigDto.setIp("10.1.100.96");
        hostConfigDto.setMediaType(0);
        ips.add(hostConfigDto);
        AlarmConfigDto alarmConfigDto=new AlarmConfigDto();
        alarmConfigDto.setGenerals(conditionDtos);
        alarmConfigDto.setDangers(conditionDtos);
        alarmConfigDto.setSeveritys(conditionDtos);
        alarmConfigDto.setMonitorType(MonitorTypeEnum.CPU_USAGE.getValue());
        jobContext.setHtJobInfoDto(alarmConfigDto);
        jobContext.setLists(ips);
        alarmHandler.execute(jobContext,new ResultT<>());
    }
    @Test
    public void test3() throws Exception {
        ProcessConfigDto processConfigDto=new ProcessConfigDto();
        processConfigDto.setIp("10.1.100.75");
        processConfigDto.setProcessName("elasticsearch");
        alarmHandler.selectProcess(processConfigDto);

    }
    @Test
    public void test4() throws Exception {
        List<ConditionDto> conditionDtos = new ArrayList<>();
        ConditionDto conditionDto = new ConditionDto();
        conditionDto.setParamname("gte");
        conditionDto.setParamvalue("50");
        ConditionDto conditionDto1 = new ConditionDto();
        conditionDto1.setParamname("lte");
        conditionDto1.setOperate("and");
        conditionDto1.setParamvalue("60");
        conditionDtos.add(conditionDto);
        conditionDtos.add(conditionDto1);
        JobContext jobContext = new JobContext();
        List<ProcessConfigDto> ips = new ArrayList<>();
        ProcessConfigDto processConfigDto=new ProcessConfigDto();
        processConfigDto.setProcessName("java");
        processConfigDto.setIp("10.1.100.96");
        ips.add(processConfigDto);
        AlarmConfigDto alarmConfigDto=new AlarmConfigDto();
        alarmConfigDto.setGenerals(conditionDtos);
        alarmConfigDto.setDangers(conditionDtos);
        alarmConfigDto.setSeveritys(conditionDtos);
        alarmConfigDto.setMonitorType(MonitorTypeEnum.PRCESS.getValue());
        jobContext.setHtJobInfoDto(alarmConfigDto);
        jobContext.setLists(ips);
        alarmHandler.execute(jobContext,new ResultT<>());
    }
}
