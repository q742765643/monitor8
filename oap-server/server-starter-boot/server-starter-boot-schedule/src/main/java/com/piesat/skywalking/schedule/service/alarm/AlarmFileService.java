package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.alarm.AlarmConfigService;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : AlarmFileService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-02 11:09
 */
@Service
public class AlarmFileService extends AlarmBaseService {
    @GrpcHthtClient
    private AlarmConfigService alarmConfigService;
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {

    }
    public void execute(FileMonitorLogDto fileMonitorLogDto) {
        AlarmConfigDto alarmConfigQDto=new AlarmConfigDto();
        NullUtil.changeToNull(alarmConfigQDto);
        alarmConfigQDto.setMonitorType(5);
        List<AlarmConfigDto> alarmConfigDtos=alarmConfigService.selectBySpecification(alarmConfigQDto);
        if(null!=alarmConfigDtos&&alarmConfigDtos.size()>0){
            for(AlarmConfigDto alarmConfigDto:alarmConfigDtos){
                this.toAlarm(alarmConfigDto,fileMonitorLogDto);
            }
        }
    }

    public void toAlarm(AlarmConfigDto alarmConfigDto, FileMonitorLogDto fileMonitorLogDto) {
        AlarmLogDto alarmLogDto=new AlarmLogDto();
        alarmLogDto.setRelatedId(fileMonitorLogDto.getTaskId());
        alarmLogDto.setDeviceType(3);
        float usage=new BigDecimal(fileMonitorLogDto.getRealFileNum() + fileMonitorLogDto.getLateNum()).divide(new BigDecimal(fileMonitorLogDto.getFileNum()), 4, BigDecimal.ROUND_HALF_UP).floatValue();
        alarmLogDto.setUsage(usage*100);
        this.fitAlarmLog(alarmConfigDto, alarmLogDto);
        this.judgeAlarm(alarmLogDto);
        fileMonitorLogDto.setStatus(3);
        if (alarmLogDto.isAlarm()&&0==fileMonitorLogDto.getIsCompensation()) {
            String message = fileMonitorLogDto.getTaskName()+":"+fileMonitorLogDto.getRemotePath()+":未采集到文件,请检查环境";
            if (alarmLogDto.getUsage() > 0) {
                message = fileMonitorLogDto.getTaskName()+":"+fileMonitorLogDto.getRemotePath()+":文件到达率为" + new BigDecimal(alarmLogDto.getUsage()).setScale(2,BigDecimal.ROUND_HALF_UP) + "%";
            }
            alarmLogDto.setMessage(message);
            this.insertEs(alarmLogDto);
            fileMonitorLogDto.setStatus(alarmLogDto.getLevel());
        }
        this.insertUnprocessed(alarmLogDto);
    }
}

