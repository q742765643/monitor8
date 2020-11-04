package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.util.ResultT;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : AlarmPingService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-02 11:08
 */
@Service
public class AlarmPingService extends AlarmBaseService {
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto = (AlarmConfigDto) jobContext.getHtJobInfoDto();
        List<HostConfigDto> hostConfigDtos =hostConfigService.selectOnineAll();
        Map<String, Float> map = this.findPingAvg();
        for (int i = 0; i < hostConfigDtos.size(); i++) {
            AlarmLogDto alarmLogDto = new AlarmLogDto();
            HostConfigDto hostConfigDto = hostConfigDtos.get(i);
            alarmLogDto.setUsage(this.getMap(hostConfigDto.getIp(), map));
            this.toAlarm(alarmLogDto, alarmConfigDto, hostConfigDto);
        }
    }

    public void toAlarm(AlarmLogDto alarmLogDto, AlarmConfigDto alarmConfigDto, HostConfigDto hostConfigDto) {
        alarmLogDto.setIp(hostConfigDto.getIp());
        alarmLogDto.setRelatedId(hostConfigDto.getId());
        alarmLogDto.setMediaType(hostConfigDto.getMediaType());
        alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
        this.fitAlarmLog(alarmConfigDto, alarmLogDto);
        this.judgeAlarm(alarmLogDto);
        if (alarmLogDto.isAlarm()) {
            String message = hostConfigDto.getIp()+":未采集到丢包率,请检查环境";
            if (alarmLogDto.getUsage() > 0) {
                message = hostConfigDto.getIp()+":丢包率到达" + alarmLogDto.getUsage() + "%";
            }
            alarmLogDto.setMessage(message);
            this.insertEs(alarmLogDto);
        }
    }


    public Map<String, Float> findPingAvg() {
        Map<String, Float> map = new HashMap<>();
        SearchSourceBuilder search = this.buildWhere("packet");
        AvgAggregationBuilder avgPct = AggregationBuilders.avg("avg_packet_pct").field("loss").format("0.0000");
        TermsAggregationBuilder groupByIp = AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupByIp.subAggregation(avgPct);
        search.aggregation(groupByIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return map;
            }
            ParsedStringTerms terms = aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                Terms.Bucket bucket = buckets.get(i);
                String ip = bucket.getKeyAsString();
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg_packet_pct");
                float avgPacketPct = (new BigDecimal(parsedAvg.getValueAsString()).setScale(4,BigDecimal.ROUND_HALF_UP)).floatValue()*100;
                map.put(ip, avgPacketPct);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}

