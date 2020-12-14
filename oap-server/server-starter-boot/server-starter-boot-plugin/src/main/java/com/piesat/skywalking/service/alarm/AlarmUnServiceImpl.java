package com.piesat.skywalking.service.alarm;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmUnService;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.util.HtDateUtil;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.StringUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : AlarmUnServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-08 13:29
 */
@Service
public class AlarmUnServiceImpl implements AlarmUnService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public List<AlarmLogDto> selectList(AlarmLogDto query){
        List<AlarmLogDto> alarmLogDtoLis=new ArrayList<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        if (null!=query.getMonitorType()) {
            MatchQueryBuilder type = QueryBuilders.matchQuery("monitor_type", query.getMonitorType());
            boolBuilder.must(type);
        }
        if (null!=query.getDeviceType()) {
            MatchQueryBuilder deviceType = QueryBuilders.matchQuery("device_type", query.getDeviceType());
            boolBuilder.must(deviceType);
        }
        if (!StringUtil.isEmpty(query.getIp())) {
            WildcardQueryBuilder ip = QueryBuilders.wildcardQuery("ip", "*" + query.getIp() + "*");
            boolBuilder.must(ip);
        }
        if (!StringUtil.isEmpty(query.getMessage())) {
            WildcardQueryBuilder message = QueryBuilders.wildcardQuery("message", "*" + query.getMessage() + "*");
            boolBuilder.must(message);
        }
        if (null!=query.getLevel()) {
            MatchQueryBuilder level = QueryBuilders.matchQuery("level", query.getLevel());
            boolBuilder.must(level);
        }
        if (null!=query.getStatus()) {
            MatchQueryBuilder status = QueryBuilders.matchQuery("status", query.getStatus());
            boolBuilder.must(status);
        }
        search.query(boolBuilder).sort("end_time", SortOrder.DESC);
        search.size(100);
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                AlarmLogDto alarmLog=new AlarmLogDto();
                Map<String, Object> map = hit.getSourceAsMap();
                alarmLog.setId(hit.getId());
                alarmLog.setDeviceType(Integer.parseInt(String.valueOf(map.get("device_type"))));
                alarmLog.setIp(String.valueOf(map.get("ip")));
                alarmLog.setMonitorType(Integer.parseInt(String.valueOf(map.get("monitor_type"))));
                if(null!=map.get("media_type")){
                    alarmLog.setMediaType(Integer.parseInt(String.valueOf(map.get("media_type"))));
                }
                alarmLog.setMessage(String.valueOf(map.get("message")));
                alarmLog.setRelatedId(String.valueOf(map.get("related_id")));
                alarmLog.setLevel(Integer.parseInt(String.valueOf(map.get("level"))));
                alarmLog.setUsage(new BigDecimal(String.valueOf(map.get("usage"))).floatValue());
                alarmLog.setTimestamp(JsonParseUtil.formateToDate(String.valueOf(map.get("@timestamp"))));
                if(null!=map.get("alarm_num")){
                    alarmLog.setAlarmNum(new BigDecimal(String.valueOf(map.get("alarm_num"))).longValue());
                }else {
                    alarmLog.setAlarmNum(1);
                }
                alarmLog.setEndTime(new BigDecimal(String.valueOf(map.get("end_time"))).longValue());
                alarmLog.setDuration(HtDateUtil.millisToDayHrMinSec(System.currentTimeMillis()-alarmLog.getTimestamp().getTime()));
                alarmLogDtoLis.add(alarmLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alarmLogDtoLis;
    }

    public PageBean selectPageList(PageForm<AlarmLogDto> pageForm){
        PageBean<AlarmLogDto> pageBean=new PageBean<>();
        AlarmLogDto query=pageForm.getT();
        query.setStatus(0);
        List<AlarmLogDto> alarmLogDtoLis=new ArrayList<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        if (null!=query.getMonitorType()) {
            MatchQueryBuilder type = QueryBuilders.matchQuery("monitor_type", query.getMonitorType());
            boolBuilder.must(type);
        }
        if (null!=query.getDeviceType()) {
            MatchQueryBuilder deviceType = QueryBuilders.matchQuery("device_type", query.getDeviceType());
            boolBuilder.must(deviceType);
        }
        if (!StringUtil.isEmpty(query.getIp())) {
            WildcardQueryBuilder ip = QueryBuilders.wildcardQuery("ip", "*" + query.getIp() + "*");
            boolBuilder.must(ip);
        }
        if (!StringUtil.isEmpty(query.getMessage())) {
            WildcardQueryBuilder message = QueryBuilders.wildcardQuery("message", "*" + query.getMessage() + "*");
            boolBuilder.must(message);
        }
        if (null!=query.getLevel()) {
            MatchQueryBuilder level = QueryBuilders.matchQuery("level", query.getLevel());
            boolBuilder.must(level);
        }
        if (null!=query.getStatus()) {
            MatchQueryBuilder status = QueryBuilders.matchQuery("status", query.getStatus());
            boolBuilder.must(status);
        }
        search.query(boolBuilder).sort("end_time", SortOrder.DESC);
        search.trackTotalHits(true);
        try {
            int startIndex = (pageForm.getCurrentPage() - 1) * pageForm.getPageSize();
            search.from(startIndex);
            search.size(pageForm.getPageSize());
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            long count = hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                AlarmLogDto alarmLog=new AlarmLogDto();
                Map<String, Object> map = hit.getSourceAsMap();
                alarmLog.setId(hit.getId());
                alarmLog.setDeviceType(Integer.parseInt(String.valueOf(map.get("device_type"))));
                alarmLog.setIp(String.valueOf(map.get("ip")));
                alarmLog.setMonitorType(Integer.parseInt(String.valueOf(map.get("monitor_type"))));
                if(null!=map.get("media_type")){
                    alarmLog.setMediaType(Integer.parseInt(String.valueOf(map.get("media_type"))));
                }
                alarmLog.setMessage(String.valueOf(map.get("message")));
                alarmLog.setRelatedId(String.valueOf(map.get("related_id")));
                alarmLog.setLevel(Integer.parseInt(String.valueOf(map.get("level"))));
                alarmLog.setUsage(new BigDecimal(String.valueOf(map.get("usage"))).floatValue());
                alarmLog.setTimestamp(JsonParseUtil.formateToDate(String.valueOf(map.get("@timestamp"))));
                if(null!=map.get("alarm_num")){
                    alarmLog.setAlarmNum(new BigDecimal(String.valueOf(map.get("alarm_num"))).longValue());
                }else {
                    alarmLog.setAlarmNum(1);
                }
                alarmLog.setEndTime(new BigDecimal(String.valueOf(map.get("end_time"))).longValue());
                alarmLog.setDuration(HtDateUtil.millisToDayHrMinSec(System.currentTimeMillis()-alarmLog.getTimestamp().getTime()));
                alarmLogDtoLis.add(alarmLog);
            }
            pageBean.setTotalCount(count);
            pageBean.setPageData(alarmLogDtoLis);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return pageBean;
    }
}

