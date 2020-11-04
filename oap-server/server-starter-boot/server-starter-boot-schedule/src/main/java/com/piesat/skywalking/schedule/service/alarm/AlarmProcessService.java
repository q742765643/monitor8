package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : AlarmProcessService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-02 11:09
 */
@Service
public class AlarmProcessService extends AlarmBaseService {
    @GrpcHthtClient
    private ProcessConfigService processConfigService;
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto = (AlarmConfigDto) jobContext.getHtJobInfoDto();
        ProcessConfigDto processConfigQDto=new ProcessConfigDto();
        NullUtil.changeToNull(processConfigQDto);
        List<ProcessConfigDto> processConfigDtos=processConfigService.selectBySpecification(processConfigQDto);
        for (int i = 0; i < processConfigDtos.size(); i++) {
            AlarmLogDto alarmLogDto = new AlarmLogDto();
            ProcessConfigDto processConfigDto=processConfigDtos.get(i);
            alarmLogDto.setUsage(this.findProcess(processConfigDto));
            this.toAlarm(alarmLogDto, alarmConfigDto, processConfigDto);
        }
    }

    public void toAlarm(AlarmLogDto alarmLogDto, AlarmConfigDto alarmConfigDto, ProcessConfigDto processConfigDto) {
        alarmLogDto.setIp(processConfigDto.getIp());
        alarmLogDto.setRelatedId(processConfigDto.getId());
        alarmLogDto.setMediaType(0);
        alarmLogDto.setDeviceType(2);
        this.fitAlarmLog(alarmConfigDto, alarmLogDto);
        this.judgeAlarm(alarmLogDto);
        if (alarmLogDto.isAlarm()) {
            String message =processConfigDto.getIp()+":进程"+processConfigDto.getProcessName()+"未采集到进程信息,请检查环境";
            if (alarmLogDto.getUsage() > 0) {
                message =processConfigDto.getIp()+":进程"+processConfigDto.getProcessName()+" cpu变化率为" + alarmLogDto.getUsage()+"%"  ;
            }
            alarmLogDto.setMessage(message);
            this.insertEs(alarmLogDto);
        }
    }


    public float findProcess(ProcessConfigDto processConfigDto){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.process");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", processConfigDto.getIp());
        WildcardQueryBuilder wild = QueryBuilders.wildcardQuery("system.process.cmdline", "*"+processConfigDto.getProcessName()+"*");
        boolBuilder.must(wild);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(1000);
        float usage=-1;
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            if(searchHits.length>0){
                Set<String> stringSet=new HashSet<>();
                for (SearchHit hit : searchHits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    Map<String, Object> system = (Map<String, Object>) map.get("system");
                    Map<String, Object> process = (Map<String, Object>) system.get("process");
                    Map<String, Object> cpu = (Map<String, Object>) process.get("cpu");
                    Map<String, Object> total = (Map<String, Object>) cpu.get("total");
                    stringSet.add(String.valueOf(total.get("value")));
                }
                usage=(new BigDecimal(stringSet.size()).divide(new BigDecimal(searchHits.length),4,BigDecimal.ROUND_HALF_UP)).floatValue()*100;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usage;

    }
}

