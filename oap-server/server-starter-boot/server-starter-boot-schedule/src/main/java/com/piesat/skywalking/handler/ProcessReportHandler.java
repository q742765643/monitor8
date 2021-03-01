package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.util.DateExpressionEngine;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.core.management.ui.template.mt.ProcessReportTemplate;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : ProcessReportHandler
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 16:01
 */
@Slf4j
@Service("processReportHandler")
public class ProcessReportHandler implements BaseHandler {
    @GrpcHthtClient
    private ProcessConfigService processConfigService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        ProcessConfigDto query=new ProcessConfigDto();
        NullUtil.changeToNull(query);
        List<ProcessConfigDto> processConfigDtos=processConfigService.selectBySpecification(query);
        long time = System.currentTimeMillis();
        String starTime = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,-0d}", time);
        String endTime = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,1d}", time);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        BulkRequest request = new BulkRequest();
        for(ProcessConfigDto processConfigDto:processConfigDtos){
              this.toReport(processConfigDto,systemQueryDto,request);
        }
        if (request.requests().size() > 0) {
            elasticSearch7Client.synchronousBulk(request);
        }

    }
    public void toReport(ProcessConfigDto processConfigDto,SystemQueryDto systemQueryDto,BulkRequest request ){
          ProcessReportTemplate processReportTemplate=new ProcessReportTemplate();
          processReportTemplate.setRelatedId(processConfigDto.getId());
          processReportTemplate.setIp(processConfigDto.getIp());
          processReportTemplate.setProcessName(processConfigDto.getProcessName());
          processReportTemplate.setTimestamp(new Date());
          this.getSystem(processConfigDto,systemQueryDto,processReportTemplate);
          this.getAlarm(processConfigDto,systemQueryDto,processReportTemplate);
          this.getDown(processConfigDto,systemQueryDto,processReportTemplate);
          this.getExeption(processConfigDto,systemQueryDto,processReportTemplate);
          processReportTemplate.setMaxUptimePct(System.currentTimeMillis()-processReportTemplate.getDownTime()-processReportTemplate.getExeptionTime()-processConfigDto.getCreateTime().getTime());
          IndexRequest indexRequest = new ElasticSearch7InsertRequest(IndexNameConstant.T_MT_PROCESS_REPORT, processConfigDto.getId() + "_" + systemQueryDto.getStartTime()).source(new ProcessReportTemplate.Builder().data2Map(processReportTemplate));
          request.add(indexRequest);
    }
    public void getSystem(ProcessConfigDto processConfigDto,SystemQueryDto systemQueryDto,ProcessReportTemplate processReportTemplate){
        SearchSourceBuilder search = this.buildWhere(systemQueryDto,processConfigDto, "process");
        AvgAggregationBuilder avgMemory = AggregationBuilders.avg("avg_memory_pct").field("system.process.memory.rss.pct").format("0.0000");
        MaxAggregationBuilder maxMemory = AggregationBuilders.max("max_memory_pct").field("system.process.memory.rss.pct").format("0.0000");
        AvgAggregationBuilder avgCpu = AggregationBuilders.avg("avg_cpu_pct").field("system.process.cpu.total.norm.pct").format("0.0000");
        MaxAggregationBuilder maxCpu = AggregationBuilders.max("max_cpu_pct").field("system.process.cpu.total.norm.pct").format("0.0000");
        MaxAggregationBuilder maxMemorySize = AggregationBuilders.max("max_memory_size").field("system.process.memory.rss.bytes").format("0.0000");
        AvgAggregationBuilder avgMemorySize = AggregationBuilders.avg("avg_memory_size").field("system.process.memory.rss.bytes").format("0.0000");

        search.size(1);
        search.aggregation(avgMemory);
        search.aggregation(maxMemory);
        search.aggregation(avgCpu);
        search.aggregation(maxCpu);
        search.aggregation(maxMemorySize);
        search.aggregation(avgMemorySize);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            if(searchResponse.getHits().getHits().length==0){
                return;
            }
            Aggregations agg=searchResponse.getAggregations();
            if(null!=agg){
                Avg avgMemoryPct=agg.get("avg_memory_pct");
                Max maxMemoryPct=agg.get("max_memory_pct");
                Avg avgCpuPct=agg.get("avg_cpu_pct");
                Max maxCpuPct=agg.get("max_cpu_pct");
                Avg avgMemoryS=agg.get("avg_memory_size");
                Max maxMemoryS=agg.get("max_memory_size");
                processReportTemplate.setAvgMemoryPct(new BigDecimal(avgMemoryPct.getValueAsString()).floatValue());
                processReportTemplate.setMaxMemoryPct(new BigDecimal(maxMemoryPct.getValueAsString()).floatValue());
                processReportTemplate.setAvgCpuPct(new BigDecimal(avgCpuPct.getValueAsString()).floatValue());
                processReportTemplate.setMaxCpuPct(new BigDecimal(maxCpuPct.getValueAsString()).floatValue());
                processReportTemplate.setMaxMemorySize(new BigDecimal(maxMemoryS.getValueAsString()).floatValue());
                processReportTemplate.setAvgMemorySize(new BigDecimal(avgMemoryS.getValueAsString()).floatValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getAlarm(ProcessConfigDto processConfigDto,SystemQueryDto systemQueryDto,ProcessReportTemplate processReportTemplate){
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("related_id", processConfigDto.getId());
        MatchQueryBuilder matchDeviceType = QueryBuilders.matchQuery("device_type", 2);
        boolBuilder.must(matchId);
        boolBuilder.must(matchDeviceType);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        search.query(boolBuilder);
        ValueCountAggregationBuilder sumAlarmNum = AggregationBuilders.count("sumAlarmNum").field("level");
        search.aggregation(sumAlarmNum);
        search.size(1);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM_LOG, search);
            if(searchResponse.getHits().getHits().length==0){
                return;
            }
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            ParsedValueCount sum=aggregations.get("sumAlarmNum");
            processReportTemplate.setAlarmNum(new BigDecimal(sum.getValueAsString()).longValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getDown(ProcessConfigDto processConfigDto,SystemQueryDto systemQueryDto,ProcessReportTemplate processReportTemplate){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("id", processConfigDto.getId());
        boolBuilder.must(matchId);
        MatchQueryBuilder matchType = QueryBuilders.matchQuery("type",-1);
        boolBuilder.must(matchType);
        try {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("end_time");
            rangeQueryBuilder.gt(format.parse(systemQueryDto.getStartTime()).getTime());
            rangeQueryBuilder.lte(format.parse(systemQueryDto.getEndTime()).getTime());
            boolBuilder.filter(rangeQueryBuilder);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        search.query(boolBuilder);
        ValueCountAggregationBuilder sumDownNum = AggregationBuilders.count("sumDownNum").field("duration");
        SumAggregationBuilder sumDownTime=AggregationBuilders.sum("sumDownTime").field("duration");
        MinAggregationBuilder minStartTime=AggregationBuilders.min("minStartTime").field("start_time");
        search.aggregation(sumDownNum);
        search.aggregation(sumDownTime);
        search.aggregation(minStartTime);
        search.size(1);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_PROCESS_DOWN_LOG, search);
            if(searchResponse.getHits().getHits().length==0){
                return;
            }
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            ParsedValueCount sumNum=aggregations.get("sumDownNum");
            ParsedSum sumTime=aggregations.get("sumDownTime");
            ParsedMin parsedMin=aggregations.get("minStartTime");
            String date=parsedMin.getValueAsString();
            long startL=format.parse(systemQueryDto.getStartTime()).getTime();
            long dateL=new BigDecimal(date).longValue();
            if(dateL<startL){
                processReportTemplate.setDownTime(new BigDecimal(sumTime.getValueAsString()).longValue()-(startL-dateL));
            }else {
                processReportTemplate.setDownTime(new BigDecimal(sumTime.getValueAsString()).longValue());
            }
            processReportTemplate.setDownNum(new BigDecimal(sumNum.getValueAsString()).longValue());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getExeption(ProcessConfigDto processConfigDto,SystemQueryDto systemQueryDto,ProcessReportTemplate processReportTemplate){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("id", processConfigDto.getId());
        boolBuilder.must(matchId);
        MatchQueryBuilder matchType = QueryBuilders.matchQuery("type",1);
        boolBuilder.must(matchType);
        try {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("end_time");
            rangeQueryBuilder.gt(format.parse(systemQueryDto.getStartTime()).getTime());
            rangeQueryBuilder.lte(format.parse(systemQueryDto.getEndTime()).getTime());
            boolBuilder.filter(rangeQueryBuilder);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        search.query(boolBuilder);
        ValueCountAggregationBuilder sumDownNum = AggregationBuilders.count("sumDownNum").field("duration");
        SumAggregationBuilder sumDownTime=AggregationBuilders.sum("sumDownTime").field("duration");
        MinAggregationBuilder minStartTime=AggregationBuilders.min("minStartTime").field("start_time");
        search.aggregation(sumDownNum);
        search.aggregation(sumDownTime);
        search.aggregation(minStartTime);
        search.size(1);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_PROCESS_DOWN_LOG, search);
            if(searchResponse.getHits().getHits().length==0){
                return;
            }
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            ParsedValueCount sumNum=aggregations.get("sumDownNum");
            ParsedSum sumTime=aggregations.get("sumDownTime");
            ParsedMin parsedMin=aggregations.get("minStartTime");
            String date=parsedMin.getValueAsString();
            long startL=format.parse(systemQueryDto.getStartTime()).getTime();
            long dateL=new BigDecimal(date).longValue();
            if(dateL<startL){
                processReportTemplate.setExeptionTime(new BigDecimal(sumTime.getValueAsString()).longValue()-(startL-dateL));
            }else {
                processReportTemplate.setExeptionTime(new BigDecimal(sumTime.getValueAsString()).longValue());
            }
            processReportTemplate.setExeptionNum(new BigDecimal(sumNum.getValueAsString()).longValue());
            processReportTemplate.setExeptionTime(new BigDecimal(sumTime.getValueAsString()).longValue());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto,ProcessConfigDto processConfigDto, String type) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolBuilder1 = QueryBuilders.boolQuery();

        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system." + type);
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", processConfigDto.getIp());
        WildcardQueryBuilder wild = QueryBuilders.wildcardQuery("system.process.cmdline", "*"+processConfigDto.getProcessName()+"*");
        WildcardQueryBuilder process = QueryBuilders.wildcardQuery("process.name", "*"+processConfigDto.getProcessName()+"*");
        boolBuilder1.should(wild);
        boolBuilder1.should(process);
        boolBuilder.must(boolBuilder1);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }
}

