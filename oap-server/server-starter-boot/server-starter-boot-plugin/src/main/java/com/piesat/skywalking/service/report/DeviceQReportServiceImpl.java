package com.piesat.skywalking.service.report;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.report.DeviceQReportService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.mapstruct.HostConfigMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : DeviceQReportServiceImpl
 * @Description : 设备报表查询
 * @Author : zzj
 * @Date: 2020-10-18 16:51
 */
@Service
public class DeviceQReportServiceImpl implements DeviceQReportService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private HostConfigService hostConfigService;
    @Autowired
    private HostConfigMapstruct hostConfigMapstruct;

    public PageBean getSystemReport(PageForm<HostConfigDto> pageForm) {
        HostConfigEntity host = hostConfigMapstruct.toEntity(pageForm.getT());
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime((String) host.getParamt().get("beginTime"));
        systemQueryDto.setEndTime((String) host.getParamt().get("endTime"));
        Map<String, Map<String, Object>> ipMap = this.getSystemReportEs(systemQueryDto);
        pageForm.getT().setParams(null);
        PageBean pageBean = hostConfigService.selectPageList(pageForm);
        List<HostConfigDto> hostConfigDtos = pageBean.getPageData();
        for (int i = 0; i < hostConfigDtos.size(); i++) {
            HostConfigDto hostConfigDto = hostConfigDtos.get(i);
            String ip = hostConfigDto.getIp();
            Map<String, Object> map = ipMap.get(ip);
            if (map != null) {
                hostConfigDto.setAvgCpuPct((Float) map.get("avg_cpu_pct"));
                hostConfigDto.setMaxCpuPct((Float) map.get("max_cpu_pct"));
                hostConfigDto.setAvgMemoryPct((Float) map.get("avg_memory_pct"));
                hostConfigDto.setMaxMemoryPct((Float) map.get("max_memory_pct"));
                hostConfigDto.setMaxFilesystemPct((Float) map.get("max_filesystem_pct"));
                hostConfigDto.setMaxProcessSize(Float.valueOf((Float) map.get("max_process_size")).longValue());
                hostConfigDto.setAvgPacketPct((Float) map.get("avg_packet_pct"));
                hostConfigDto.setMaxPacketPct((Float) map.get("max_packet_pct"));
                hostConfigDto.setMaxUptime(Float.valueOf((Float) map.get("max_uptime")).longValue());
                hostConfigDto.setAlarmCount((Long) map.get("sum_alarm_count"));
                hostConfigDto.setDownCount((Long) map.get("sum_down_count"));
                hostConfigDto.setDownTime((Float) map.get("sum_down_time"));
            }

        }
        pageBean.setPageData(hostConfigDtos);
        return pageBean;
    }

    public Map<String, Map<String, Object>> getSystemReportEs(SystemQueryDto systemQueryDto) {
        Map<String, Map<String, Object>> ipMap = new HashMap<>();
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        AvgAggregationBuilder avgCpuPct = AggregationBuilders.avg("avg_cpu_pct").field("avg.cpu.pct").format("0.0000");
        MaxAggregationBuilder maxCpuPct = AggregationBuilders.max("max_cpu_pct").field("max.cpu.pct").format("0.0000");
        AvgAggregationBuilder avgMemoryPct = AggregationBuilders.avg("avg_memory_pct").field("avg.memory.pct").format("0.0000");
        MaxAggregationBuilder maxMemoryPct = AggregationBuilders.max("max_memory_pct").field("max.memory.pct").format("0.0000");
        MaxAggregationBuilder maxFilesystemPct = AggregationBuilders.max("max_filesystem_pct").field("max.filesystem.pct").format("0.0000");
        MaxAggregationBuilder maxProcessSize = AggregationBuilders.max("max_process_size").field("max.process.size");
        AvgAggregationBuilder avgPacketPct = AggregationBuilders.avg("avg_packet_pct").field("avg.packet.pct").format("0.0000");
        MaxAggregationBuilder maxPacketPct = AggregationBuilders.max("max_packet_pct").field("max.packet.pct").format("0.0000");
        MaxAggregationBuilder maxUptime = AggregationBuilders.max("max_uptime").field("max.uptime.pct");
        SumAggregationBuilder sumAlarmCount = AggregationBuilders.sum("sum_alarm_count").field("alarm.num");
        SumAggregationBuilder sumDownTime = AggregationBuilders.sum("sum_down_time").field("down.time");
        SumAggregationBuilder sumDownCount = AggregationBuilders.sum("sum_down_count").field("down.num");
        TermsAggregationBuilder groupByIp = AggregationBuilders.terms("groupby_ip").field("ip").size(10000);
        groupByIp.subAggregation(avgCpuPct);
        groupByIp.subAggregation(maxCpuPct);
        groupByIp.subAggregation(avgMemoryPct);
        groupByIp.subAggregation(maxMemoryPct);
        groupByIp.subAggregation(maxFilesystemPct);
        groupByIp.subAggregation(maxProcessSize);
        groupByIp.subAggregation(avgPacketPct);
        groupByIp.subAggregation(maxPacketPct);
        groupByIp.subAggregation(maxUptime);
        groupByIp.subAggregation(sumAlarmCount);
        groupByIp.subAggregation(sumDownTime);
        groupByIp.subAggregation(sumDownCount);
        search.aggregation(groupByIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_MEDIA_REPORT, search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return ipMap;
            }
            ParsedStringTerms terms = aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                Terms.Bucket bucket = buckets.get(i);
                String ip = bucket.getKeyAsString();
                ParsedAvg parsedCpuAvg = bucket.getAggregations().get("avg_cpu_pct");
                ParsedMax parsedCpuMax = bucket.getAggregations().get("max_cpu_pct");
                ParsedAvg parsedMemoryAvg = bucket.getAggregations().get("avg_memory_pct");
                ParsedMax parsedMemoryMax = bucket.getAggregations().get("max_memory_pct");
                ParsedMax parsedFilesystemMax = bucket.getAggregations().get("max_filesystem_pct");
                ParsedMax parsedProcessMax = bucket.getAggregations().get("max_process_size");
                ParsedAvg parsedPacketAvg = bucket.getAggregations().get("avg_packet_pct");
                ParsedMax parsedPacketMax = bucket.getAggregations().get("max_packet_pct");
                ParsedMax parsedUptimeMax = bucket.getAggregations().get("max_uptime");
                ParsedSum parsedSumAlarmCount = bucket.getAggregations().get("sum_alarm_count");
                ParsedSum parsedSumDownTime = bucket.getAggregations().get("sum_down_time");
                ParsedSum parsedSumDownCount = bucket.getAggregations().get("sum_down_count");
                Map<String, Object> map = new HashMap<>();
                map.put("avg_cpu_pct", new BigDecimal(parsedCpuAvg.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_cpu_pct", new BigDecimal(parsedCpuMax.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("avg_memory_pct", new BigDecimal(parsedMemoryAvg.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_memory_pct", new BigDecimal(parsedMemoryMax.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_filesystem_pct", new BigDecimal(parsedFilesystemMax.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_process_size", new BigDecimal(parsedProcessMax.getValueAsString()).floatValue());
                map.put("avg_packet_pct", new BigDecimal(parsedPacketAvg.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_packet_pct", new BigDecimal(parsedPacketMax.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("sum_alarm_count", new BigDecimal(parsedSumAlarmCount.getValueAsString()).longValue());
                map.put("sum_down_time", new BigDecimal(parsedSumDownTime.getValueAsString()).divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("sum_down_count", new BigDecimal(parsedSumDownCount.getValueAsString()).longValue());
                float hours = new BigDecimal(parsedUptimeMax.getValueAsString()).divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP).floatValue();
                map.put("max_uptime", hours);
                ipMap.put(ip, map);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipMap;
    }

    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
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

