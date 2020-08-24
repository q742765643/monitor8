package com.piesat.skywalking.schedule.service.alarm;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.host.PacketLossLogService;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.schedule.service.es.CreateIndexNameService;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.sso.client.util.mq.MsgPublisher;
import com.piesat.util.CompareUtil;
import com.piesat.util.IndexNameUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AlarmLogService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private MsgPublisher msgPublisher;
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @GrpcHthtClient
    private ProcessConfigService processConfigService;
    @GrpcHthtClient
    private PacketLossLogService packetLossLogService;
    @Autowired
    private CreateIndexNameService createIndexNameService;
    @Autowired
    private RedisUtil redisUtil;
    private static final String  PREFIX="HTHT.HOSTSTATUS";

    public AlarmLogDto isAlarm(AlarmConfigDto alarmConfigDto,AlarmLogDto alarmLogDto, float usage) {
        alarmLogDto.setStatus(0);
        alarmLogDto.setTimestamp(new Date());
        alarmLogDto.setUsage(usage);
        boolean isAlarm = false;
        if(usage==-1){
            isAlarm=true;
            alarmLogDto.setLevel(2);
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }

        List<ConditionDto> generals = alarmConfigDto.getGenerals();
        isAlarm = CompareUtil.compare(generals, usage);
        if (isAlarm) {
            alarmLogDto.setLevel(2);
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }
        List<ConditionDto> dangers = alarmConfigDto.getDangers();
        isAlarm = CompareUtil.compare(dangers, usage);
        if (isAlarm) {
            alarmLogDto.setLevel(1);
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }

        List<ConditionDto> severitys = alarmConfigDto.getSeveritys();
        isAlarm = CompareUtil.compare(severitys, usage);
        if (isAlarm) {
            alarmLogDto.setLevel(0);
            alarmLogDto.setAlarm(isAlarm);
            return alarmLogDto;
        }
        return alarmLogDto;

    }


    public Integer checkAndInsert(AlarmConfigDto alarmConfigDto, AlarmLogDto alarmLogDto, float usage){
        this.isAlarm(alarmConfigDto, alarmLogDto,usage);
        Integer currentStatus=3;
        if(alarmLogDto.isAlarm()){
            currentStatus=alarmLogDto.getLevel();
            Map<String,Object> source=new HashMap<>();
            source.put("device_name",alarmLogDto.getDeviceName());
            source.put("device_type",alarmLogDto.getDeviceType());
            source.put("level",alarmLogDto.getLevel());
            source.put("ip",alarmLogDto.getIp());
            source.put("monitor_type",alarmLogDto.getMonitorType());
            source.put("media_type",alarmLogDto.getMediaType());
            source.put("usage",alarmLogDto.getUsage());
            source.put("message",alarmLogDto.getMessage());
            source.put("status",alarmLogDto.getStatus());
            source.put("host_id",alarmLogDto.getHostId());
            source.put("@timestamp",alarmLogDto.getTimestamp());
            String indexName= IndexNameUtil.getIndexName(IndexNameConstant.T_MT_ALARM_LOG,alarmLogDto.getTimestamp());;
            try {
                createIndexNameService.createAlarmLog(indexName);
                elasticSearch7Client.forceInsert(indexName,IdUtils.fastUUID(),source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            msgPublisher.sendMsg(JSON.toJSONString(alarmConfigDto));

        }
        /***======currentStatus=3 状态为正常========***/
        if(0==alarmLogDto.getDeviceType()||1==alarmLogDto.getDeviceType()){
            HostConfigDto hostConfigDto=new HostConfigDto();
            hostConfigDto.setId(alarmLogDto.getHostId());
            hostConfigDto.setCurrentStatus(currentStatus);
            if(alarmLogDto.getMediaType()==MonitorTypeEnum.PING.getValue()){
                hostConfigDto.setPacketLoss(alarmLogDto.getUsage());
            }
            if(currentStatus!=3){
                redisUtil.hset(PREFIX+":"+alarmLogDto.getIp(),String.valueOf(alarmLogDto.getMonitorType()),currentStatus,60);
            }else {
                redisUtil.hdel(PREFIX+":"+alarmLogDto.getIp(),String.valueOf(alarmLogDto.getMonitorType()));
                Map<Object, Object> map=redisUtil.hmget(PREFIX+":"+alarmLogDto.getIp());
                if(map!=null&&map.size()>0){
                    Collection<Object> c = map.values();
                    Object[] obj = c.toArray();
                    Arrays.sort(obj);
                    currentStatus= (Integer) obj[map.size()-1];
                }
            }
            hostConfigService.save(hostConfigDto);
        }
        if(2==alarmLogDto.getDeviceType()){
            ProcessConfigDto processConfigDto=new ProcessConfigDto();
            processConfigDto.setId(alarmLogDto.getProcessId());
            processConfigDto.setCurrentStatus(currentStatus);
            processConfigService.save(processConfigDto);
        }
        if(alarmLogDto.getMediaType()==MonitorTypeEnum.PING.getValue()){
            PacketLossLogDto packetLossLogDto=new PacketLossLogDto();
            packetLossLogDto.setIp(alarmLogDto.getIp());
            packetLossLogDto.setHostId(alarmLogDto.getHostId());
            packetLossLogDto.setPacketLoss(alarmLogDto.getUsage());
            packetLossLogService.save(packetLossLogDto);
        }
        return currentStatus;

    }
}
