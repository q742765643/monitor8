package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.ConditionDto;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.CompareUtil;
import com.piesat.util.IndexNameUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlarmLogService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public AlarmLogDto isAlarm(AlarmConfigDto alarmConfigDto,AlarmLogDto alarmLogDto, float usage) {
        alarmLogDto.setStatus("0");
        alarmLogDto.setTimestamp(new Date());
        alarmLogDto.setUsage(usage);
        boolean isAlarm = false;
        if(usage==-1){
            isAlarm=true;
            alarmLogDto.setLevel("2");
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }

        List<ConditionDto> generals = alarmConfigDto.getGenerals();
        isAlarm = CompareUtil.compare(generals, usage);
        if (isAlarm) {
            alarmLogDto.setLevel("2");
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }
        List<ConditionDto> dangers = alarmConfigDto.getDangers();
        isAlarm = CompareUtil.compare(dangers, usage);
        if (isAlarm) {
            alarmLogDto.setLevel("1");
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }

        List<ConditionDto> severitys = alarmConfigDto.getSeveritys();
        isAlarm = CompareUtil.compare(severitys, usage);
        if (isAlarm) {
            alarmLogDto.setLevel("0");
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }
        return alarmLogDto;

    }


    public void checkAndInsert(AlarmConfigDto alarmConfigDto, AlarmLogDto alarmLogDto, float usage){
        this.isAlarm(alarmConfigDto, alarmLogDto,usage);
        if(alarmLogDto.isAlarm()){
            Map<String,Object> source=new HashMap<>();
            source.put("device_name",alarmLogDto.getDeviceName());
            source.put("device_type",alarmLogDto.getDeviceType());
            source.put("level",alarmLogDto.getLevel());
            source.put("ip",alarmLogDto.getIp());
            source.put("type",alarmLogDto.getType());
            source.put("usage",alarmLogDto.getUsage());
            source.put("message",alarmLogDto.getMessage());
            source.put("status",alarmLogDto.getStatus());
            source.put("@timestamp",alarmLogDto.getTimestamp());
            String indexName= IndexNameUtil.getIndexName(IndexNameConstant.T_MT_ALARM_LOG,alarmLogDto.getTimestamp());;
            try {
                elasticSearch7Client.forceInsert(indexName,IdUtils.fastUUID(),source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
