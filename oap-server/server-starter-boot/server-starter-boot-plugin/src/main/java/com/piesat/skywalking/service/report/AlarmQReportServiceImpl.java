package com.piesat.skywalking.service.report;

import com.alibaba.fastjson.JSON;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.report.AlarmQReportService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.entity.AlarmConfigEntity;
import com.piesat.skywalking.excel.AlarmReportVo;
import com.piesat.skywalking.excel.ProcessReportVo;
import com.piesat.skywalking.mapstruct.AlarmConfigMapstruct;
import com.piesat.util.DateExpressionEngine;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : AlarmQReportServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-04 11:10
 */
@Service
public class AlarmQReportServiceImpl implements AlarmQReportService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private AlarmConfigMapstruct alarmConfigMapstruct;

    public Object getAlarmReport(AlarmConfigDto alarmConfigDto){

        AlarmConfigEntity alarmConfigEntity=alarmConfigMapstruct.toEntity(alarmConfigDto);
        List<AlarmReportVo> alarmReportVos=new ArrayList<>();

        String[] deviceTypeTiltle=new String[]{"服务器","网络设备","进程","文件"};
        String[] levelTiltle=new String[]{"general","danger","fault"};
        Map<String,Map<String,Object>> mapList=new HashMap<>();
        for(int i=0;i<deviceTypeTiltle.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("general",0l);
            map.put("danger",0l);
            map.put("fault",0l);
            map.put("processed",0l);
            map.put("unprocessed",0l);
            mapList.put(deviceTypeTiltle[i],map);
        }
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime((String) alarmConfigEntity.getParamt().get("beginTime"));
        systemQueryDto.setEndTime((String) alarmConfigEntity.getParamt().get("endTime"));
        SearchSourceBuilder search=this.buildWhere(systemQueryDto);
        TermsAggregationBuilder groupByDeviceType = AggregationBuilders.terms("groupByDeviceType").field("device_type").size(10000);
        TermsAggregationBuilder groupByStatus = AggregationBuilders.terms("groupByStatus").field("status").size(10000);
        TermsAggregationBuilder groupByLevel = AggregationBuilders.terms("groupByLevel").field("level").size(10000);
        ValueCountAggregationBuilder countStatus=AggregationBuilders.count("countStatus").field("status");
        ValueCountAggregationBuilder countLevel=AggregationBuilders.count("countLevel").field("level");
        groupByStatus.subAggregation(countStatus);
        groupByLevel.subAggregation(countLevel);
        groupByDeviceType.subAggregation(groupByStatus);
        groupByDeviceType.subAggregation(groupByLevel);
        search.aggregation(groupByDeviceType);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            Aggregations agg=searchResponse.getAggregations();
            if(null!=agg){
                ParsedLongTerms termsDeviceType = agg.get("groupByDeviceType");
                List<? extends Terms.Bucket> bucketsDeviceType = termsDeviceType.getBuckets();
                for (int i = 0; i < bucketsDeviceType.size(); i++) {
                    Terms.Bucket bucketDeviceType = bucketsDeviceType.get(i);
                    ParsedLongTerms termsStatus = bucketDeviceType.getAggregations().get("groupByStatus");
                    List<? extends Terms.Bucket> bucketsStatus = termsStatus.getBuckets();
                    for(int j=0;j<bucketsStatus.size();j++){
                        Terms.Bucket bucketStatus = bucketsStatus.get(j);
                        ParsedValueCount pStatus = bucketStatus.getAggregations().get("countStatus");
                        if("0".equals(bucketStatus.getKeyAsString())){
                            mapList.get(deviceTypeTiltle[Integer.parseInt(bucketDeviceType.getKeyAsString())]).put("unprocessed",new BigDecimal(pStatus.getValue()).longValue());
                        }
                        if("1".equals(bucketStatus.getKeyAsString())){
                            mapList.get(deviceTypeTiltle[Integer.parseInt(bucketDeviceType.getKeyAsString())]).put("processed",new BigDecimal(pStatus.getValue()).longValue());
                        }
                    }
                    ParsedLongTerms termsLevel = bucketDeviceType.getAggregations().get("groupByLevel");
                    List<? extends Terms.Bucket> bucketsLevel = termsLevel.getBuckets();
                    for(int j=0;j<bucketsLevel.size();j++){
                        Terms.Bucket bucketLevel = bucketsLevel.get(j);
                        ParsedValueCount pLevel = bucketLevel.getAggregations().get("countLevel");
                        mapList.get(deviceTypeTiltle[Integer.parseInt(bucketDeviceType.getKeyAsString())]).put(levelTiltle[Integer.parseInt(bucketLevel.getKeyAsString())],new BigDecimal(pLevel.getValue()).longValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Map<String,Object>> entry : mapList.entrySet()) {
            AlarmReportVo alarmReportVo=new AlarmReportVo();
            alarmReportVo.setDeviceType(entry.getKey());
            alarmReportVo.setGeneral((Long) entry.getValue().get("general"));
            alarmReportVo.setDanger((Long) entry.getValue().get("danger"));
            alarmReportVo.setFault((Long) entry.getValue().get("fault"));
            alarmReportVo.setProcessed((Long) entry.getValue().get("processed"));
            alarmReportVo.setUnprocessed((Long) entry.getValue().get("unprocessed"));
            alarmReportVos.add(alarmReportVo);
        }
        return alarmReportVos;

    }
    public void exportExcel(AlarmConfigDto alarmConfigDto){
        ExcelUtil<AlarmReportVo> util = new ExcelUtil(AlarmReportVo.class);
        List<AlarmReportVo> alarmReportVos= (List<AlarmReportVo>) this.getAlarmReport(alarmConfigDto);
        util.exportExcel(alarmReportVos, "进程运行情况");
    }
    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        try {
            BoolQueryBuilder boolEnd = QueryBuilders.boolQuery();
            RangeQueryBuilder rangeEnd = QueryBuilders.rangeQuery("end_time").lte(simpleDateFormat.parse(systemQueryDto.getEndTime()).getTime());
            boolEnd.must(rangeEnd);
            RangeQueryBuilder rangeStart = QueryBuilders.rangeQuery("end_time").gte(simpleDateFormat.parse(systemQueryDto.getStartTime()).getTime());
            boolEnd.must(rangeStart);

            BoolQueryBuilder boolStart = QueryBuilders.boolQuery();
            RangeQueryBuilder rangeEnd1 = QueryBuilders.rangeQuery("start_time").lte(simpleDateFormat.parse(systemQueryDto.getEndTime()).getTime());
            boolStart.must(rangeEnd1);
            RangeQueryBuilder rangeStart1 = QueryBuilders.rangeQuery("start_time").gte(simpleDateFormat.parse(systemQueryDto.getStartTime()).getTime());
            boolStart.must(rangeStart1);

            boolBuilder.should(boolStart);
            boolBuilder.should(boolEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }
}

