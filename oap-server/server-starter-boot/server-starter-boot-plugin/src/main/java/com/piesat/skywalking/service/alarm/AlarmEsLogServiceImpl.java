
package com.piesat.skywalking.service.alarm;

import com.alibaba.fastjson.JSON;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.vo.NetworkVo;
import com.piesat.util.HtDateUtil;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.StringUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.ParsedSimpleValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AlarmEsLogServiceImpl implements AlarmEsLogService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public PageBean selectPageList(PageForm<AlarmLogDto> pageForm) {
        AlarmLogDto query = pageForm.getT();
        PageBean<AlarmLogDto> pageBean=new PageBean<>();
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
        search.query(boolBuilder).sort("@timestamp", SortOrder.DESC);
        search.trackTotalHits(true);
        try {
            int startIndex=(pageForm.getCurrentPage()-1)*pageForm.getPageSize();
            search.from(startIndex);
            search.size(pageForm.getPageSize());
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            long count=hits.getTotalHits().value;
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
                alarmLog.setDuration(HtDateUtil.millisToDayHrMinSec(System.currentTimeMillis()-alarmLog.getTimestamp().getTime()));
                alarmLogDtoLis.add(alarmLog);
            }
            pageBean.setPageData(alarmLogDtoLis);
            pageBean.setTotalCount(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageBean;
    }

    public Map<String,Object> selectAlarmTrend(AlarmLogDto query){
         Map<String,Object> result=new HashMap<>();
         RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         long endTime = calendar.getTime().getTime();
         long startTime = endTime - 86400 * 1000;
         List<String> hoursList=new ArrayList<>();
         List<String> date=new ArrayList<>();
         List<Long> data=new ArrayList<>();
         Map<String,String> mapValues=new HashMap<>();
         rangeQueryBuilder.gte(format.format(startTime));
         rangeQueryBuilder.lte(format.format(endTime));
         SearchSourceBuilder search = new SearchSourceBuilder();
         search.size(0);
         BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
         rangeQueryBuilder.timeZone("+08:00");
         rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
         boolBuilder.filter(rangeQueryBuilder);

        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.hours(1));
        dateHis.subAggregation(AggregationBuilders.count("count").field("@timestamp"));
        search.aggregation(dateHis);
        search.query(boolBuilder);
        SimpleDateFormat qh=new SimpleDateFormat("d/H");

        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM_LOG, search);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    Histogram.Bucket bucket = buckets.get(i);
                    ZonedDateTime time = (ZonedDateTime) bucket.getKey();
                    ParsedValueCount parsedValueCount=bucket.getAggregations().get("count");
                    long timesLong = Timestamp.from(time.toInstant()).getTime();
                    mapValues.put(qh.format(timesLong),parsedValueCount.getValueAsString());
                }
            }
            Calendar calendarTemp = Calendar.getInstance();
            calendarTemp.setTimeInMillis(startTime);
            for(int i=0;i<24;i++){
                calendarTemp.setTimeInMillis(calendarTemp.getTimeInMillis()+3600*1000);
                int hour=calendarTemp.get(Calendar.HOUR_OF_DAY);
                if(i==0||hour==0){
                    date.add(qh.format(calendarTemp.getTimeInMillis()));
                }else {
                    date.add(String.valueOf(hour));
                }
                String key=qh.format(calendarTemp.getTimeInMillis());
                if(null!=mapValues.get(key)){
                    data.add(new BigDecimal(mapValues.get(key)).longValue());
                }else {
                    data.add(0l);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("date",date);
        result.put("data",data);
        result.put("hourList",hoursList);
        return result;

    }

    public List<Map<String,Object>> selectAlarmLevel(AlarmLogDto query){
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long endTime = calendar.getTime().getTime();
        long startTime = endTime - 86400 * 1000;
        rangeQueryBuilder.gte(format.format(startTime));
        rangeQueryBuilder.lte(format.format(endTime));
        SearchSourceBuilder search = new SearchSourceBuilder();
        search.size(0);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);

        ValueCountAggregationBuilder countByLevel = AggregationBuilders.count("count").field("level");
        TermsAggregationBuilder gourpByLevel= AggregationBuilders.terms("level").field("level")
                .subAggregation(countByLevel);
        search.aggregation(gourpByLevel);
        search.query(boolBuilder);
        search.size(0);
        String[] names=new String[]{"一般","危险","故障"};
        Map<String,String> maps=new HashMap<>();
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedLongTerms parsedLongTerms = aggregations.get("level");
            if (parsedLongTerms == null) {
                return null;
            }
            List<? extends Terms.Bucket> buckets = parsedLongTerms.getBuckets();
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    Terms.Bucket bucket = buckets.get(i);
                    String key =String.valueOf(bucket.getKey());
                    ParsedValueCount parsedValueCount = bucket.getAggregations().get("count");
                    Long count = parsedValueCount.getValue();
                    maps.put(key,String.valueOf(count));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<names.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("name",names[i]);
            if(null!=maps.get(String.valueOf(i))){
                map.put("value",Long.parseLong(maps.get(String.valueOf(i))));
            }else {
                map.put("value",0);
            }
            list.add(map);
        }
        return list;
    }
    public List<Map<String,Object>> selectAlarmList(AlarmLogDto query){
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        search.query(boolBuilder);
        search.size(100);
        search.sort("@timestamp",SortOrder.DESC);
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                list.add(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public Map<String,Long> selectAlarmNum(AlarmLogDto query){
        Map<String,Long> map=new HashMap<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        search.query(boolBuilder);
        search.size(10000);
        MatchQueryBuilder status = QueryBuilders.matchQuery("status", 0);
        boolBuilder.must(status);
        Set set = new HashSet<Long>();
        set.add(0l);
        set.add(1l);
        TermsQueryBuilder termsHost = QueryBuilders.termsQuery("device_type", set);
        boolBuilder.must(termsHost);
        boolBuilder.must(status);
        search.query(boolBuilder);
        search.trackTotalHits(true);
        Set<String>  deviceNum=new HashSet<>();
        long faultNum=0;
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            faultNum=hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                deviceNum.add((String) source.get("related_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("deviceNum", (long) deviceNum.size());
        map.put("faultNum", faultNum);
        return map;
    }
}

