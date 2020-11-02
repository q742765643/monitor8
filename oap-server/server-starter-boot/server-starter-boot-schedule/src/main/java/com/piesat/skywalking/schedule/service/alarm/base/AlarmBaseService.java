package com.piesat.skywalking.schedule.service.alarm.base;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.ConditionDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.sso.client.util.mq.MsgPublisher;
import com.piesat.util.CompareUtil;
import com.piesat.util.ResultT;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : AlarmBaseService
 * @Description : 文件监控告警
 * @Author : zzj
 * @Date: 2020-11-02 11:05
 */
@Service
public abstract class AlarmBaseService {
    @Autowired
    protected ElasticSearch7Client elasticSearch7Client;
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @Autowired
    private MsgPublisher msgPublisher;

    public abstract void execute(JobContext jobContext, ResultT<String> resultT);

    public List<HostConfigDto> selectAvailable() {
        return hostConfigService.selectAvailable();
    }

    public SearchSourceBuilder buildWhere(String type) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime = format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime = format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system." + type);
        //MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", ip);
        boolBuilder.must(matchEvent);
        //boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }

    public void fitAlarmLog(AlarmConfigDto alarmConfigDto, AlarmLogDto alarmLogDto) {
        alarmLogDto.setMonitorType(alarmConfigDto.getMonitorType());
        alarmLogDto.setAlarmKpi(alarmConfigDto.getMonitorType());
        alarmLogDto.setTimestamp(new Date());
        alarmLogDto.setGenerals(alarmConfigDto.getGenerals());
        alarmLogDto.setDangers(alarmConfigDto.getDangers());
        alarmLogDto.setSeveritys(alarmConfigDto.getSeveritys());
        alarmLogDto.setStatus(3);
    }

    public void judgeAlarm(AlarmLogDto alarmLogDto) {
        boolean isAlarm = false;
        List<ConditionDto> severitys = alarmLogDto.getSeveritys();
        isAlarm = CompareUtil.compare(severitys, alarmLogDto.getUsage());
        if (isAlarm) {
            alarmLogDto.setLevel(2);
            alarmLogDto.setAlarm(isAlarm);
            return;
        }
        List<ConditionDto> dangers = alarmLogDto.getDangers();
        isAlarm = CompareUtil.compare(dangers, alarmLogDto.getUsage());
        if (isAlarm) {
            alarmLogDto.setLevel(1);
            alarmLogDto.setAlarm(isAlarm);
            return;
        }
        List<ConditionDto> generals = alarmLogDto.getGenerals();
        isAlarm = CompareUtil.compare(generals, alarmLogDto.getUsage());
        if (isAlarm) {
            alarmLogDto.setLevel(0);
            alarmLogDto.setAlarm(isAlarm);
            return;
        }
    }

    public Float getMap(String key, Map<String, Float> map) {
        if (null != map.get(key)) {
            return map.get(key);
        }
        return -1f;
    }

    public void insertEs(AlarmLogDto alarmLogDto) {

        Map<String, Object> source = new HashMap<>();
        source.put("device_type", alarmLogDto.getDeviceType());
        source.put("alarm_kpi", alarmLogDto.getAlarmKpi());
        source.put("level", alarmLogDto.getLevel());
        source.put("ip", alarmLogDto.getIp());
        source.put("monitor_type", alarmLogDto.getMonitorType());
        source.put("media_type", alarmLogDto.getMediaType());
        source.put("usage", alarmLogDto.getUsage());
        source.put("message", alarmLogDto.getMessage());
        source.put("status", alarmLogDto.getStatus());
        source.put("related_id", alarmLogDto.getRelatedId());
        source.put("@timestamp", alarmLogDto.getTimestamp());
        String indexName = IndexNameConstant.T_MT_ALARM_LOG;
        try {
            elasticSearch7Client.forceInsert(indexName, IdUtils.fastUUID(), source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        msgPublisher.sendMsg(JSON.toJSONString(alarmLogDto));
    }


}


