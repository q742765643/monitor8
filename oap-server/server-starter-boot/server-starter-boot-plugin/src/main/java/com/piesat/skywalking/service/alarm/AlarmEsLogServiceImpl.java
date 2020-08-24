package com.piesat.skywalking.service.alarm;

import com.alibaba.fastjson.JSON;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.StringUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlarmEsLogServiceImpl implements AlarmEsLogService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public PageBean selectPageList(PageForm<AlarmLogDto> pageForm) {
        AlarmLogDto alarmLogDto = pageForm.getT();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        if (!StringUtil.isEmpty(alarmLogDto.getDeviceName())) {
            WildcardQueryBuilder deviceName = QueryBuilders.wildcardQuery("device_name", "*" + alarmLogDto.getDeviceName() + "*");
            boolBuilder.must(deviceName);
        }
        if (null!=alarmLogDto.getMonitorType()&&alarmLogDto.getMonitorType()>-1) {
            MatchQueryBuilder type = QueryBuilders.matchQuery("monitor_type", alarmLogDto.getMonitorType());
            boolBuilder.must(type);
        }
        if (null!=alarmLogDto.getDeviceType()&&alarmLogDto.getDeviceType()>-1) {
            MatchQueryBuilder deviceType = QueryBuilders.matchQuery("device_type", alarmLogDto.getDeviceType());
            boolBuilder.must(deviceType);
        }
        if (!StringUtil.isEmpty(alarmLogDto.getIp())) {
            WildcardQueryBuilder ip = QueryBuilders.wildcardQuery("ip", "*" + alarmLogDto.getIp() + "*");
            boolBuilder.must(ip);
        }
        if (null!=alarmLogDto.getStatus()&&alarmLogDto.getStatus()>-1) {
            MatchQueryBuilder status = QueryBuilders.matchQuery("status", alarmLogDto.getStatus());
            boolBuilder.must(status);
        }
        if (null!=alarmLogDto.getLevel()&&alarmLogDto.getLevel()>-1) {
            MatchQueryBuilder level = QueryBuilders.matchQuery("level", alarmLogDto.getLevel());
            boolBuilder.must(level);
        }
        if (StringUtil.isNotEmpty(alarmLogDto.getHostId())) {
            MatchQueryBuilder hostId = QueryBuilders.matchQuery("host_id", alarmLogDto.getHostId());
            boolBuilder.must(hostId);
        }
        Map<String, Object> paramt = new HashMap<>();
        if (!StringUtil.isEmpty(alarmLogDto.getParams())) {
            paramt = JSON.parseObject(alarmLogDto.getParams(), Map.class);
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        if (StringUtils.isNotNullString((String) paramt.get("beginTime"))) {
            rangeQueryBuilder.gte((String) paramt.get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) paramt.get("endTime"))) {
            rangeQueryBuilder.lte((String) paramt.get("endTime"));
        }

        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.trackTotalHits(true);
        PageBean<AlarmLogDto> pageBean=new PageBean<>();
        try {
            int startIndex=(pageForm.getCurrentPage()-1)*pageForm.getPageSize();
            searchSourceBuilder.from(startIndex);
            searchSourceBuilder.size(pageForm.getPageSize());
            List<AlarmLogDto> alarmLogDtoLis=new ArrayList<>();
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM_LOG+"-*", searchSourceBuilder);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            long count=hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                AlarmLogDto alarmLog=new AlarmLogDto();
                Map<String, Object> map = hit.getSourceAsMap();
                alarmLog.setId(hit.getId());
                alarmLog.setDeviceType(Integer.parseInt(String.valueOf(map.get("device_type"))));
                alarmLog.setDeviceName(String.valueOf(map.get("device_name")));
                alarmLog.setIp(String.valueOf(map.get("ip")));
                alarmLog.setMessage(String.valueOf(map.get("message")));
                alarmLog.setStatus(Integer.parseInt(String.valueOf(map.get("status"))));
                alarmLog.setLevel(Integer.parseInt(String.valueOf(map.get("level"))));
                alarmLog.setUsage(new BigDecimal(String.valueOf(map.get("usage"))).floatValue());
                alarmLog.setTimestamp(JsonParseUtil.formateToDate(String.valueOf(map.get("@timestamp"))));
                alarmLogDtoLis.add(alarmLog);
            }
            pageBean.setPageData(alarmLogDtoLis);
            pageBean.setTotalCount(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageBean;
    }
}
