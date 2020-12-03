package com.piesat.skywalking.service.report;

import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.api.report.ProcessQReportService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.ProcessReportDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.entity.ProcessConfigEntity;
import com.piesat.skywalking.excel.LinkExcelVo;
import com.piesat.skywalking.excel.ProcessReportVo;
import com.piesat.skywalking.mapstruct.ProcessConfigMapstruct;
import com.piesat.skywalking.vo.ImageVo;
import com.piesat.util.ImageUtils;
import com.piesat.util.NullUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : ProcessQReportServiceImpl
 * @Description : 报表查询
 * @Author : zzj
 * @Date: 2020-12-03 09:47
 */
@Service
public class ProcessQReportServiceImpl implements ProcessQReportService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private ProcessConfigService processConfigService;
    @Autowired
    private ProcessConfigMapstruct processConfigMapstruct;

    public List<ProcessReportDto> getProcessReport(ProcessConfigDto query) {
        ProcessConfigEntity processQuery = processConfigMapstruct.toEntity(query);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        NullUtil.changeToNull(systemQueryDto);
        systemQueryDto.setStartTime((String) processQuery.getParamt().get("beginTime"));
        systemQueryDto.setEndTime((String) processQuery.getParamt().get("endTime"));
        Map<String, Map<String, Object>> idMap = this.getProcessReportEs(systemQueryDto);
        query.setParams(null);
        List<ProcessConfigDto> processConfigDtos=processConfigService.selectBySpecification(query);
        List<ProcessReportDto> processReportDtos=new ArrayList<>();
        for (int i = 0; i < processConfigDtos.size(); i++) {
            ProcessReportDto processReportDto=new ProcessReportDto();
            ProcessConfigDto processConfigDto = processConfigDtos.get(i);
            String ip = processConfigDto.getIp();
            Map<String, Object> map = idMap.get(processConfigDto.getId());
            processReportDto.setIp(ip);
            processReportDto.setProcessName(processConfigDto.getProcessName());
            processReportDto.setRelatedId(processConfigDto.getId());
            if (map != null) {
                processReportDto.setAvgCpuPct((Float) map.get("avg_cpu_pct"));
                processReportDto.setMaxCpuPct((Float) map.get("max_cpu_pct"));
                processReportDto.setAvgMemoryPct((Float) map.get("avg_memory_pct"));
                processReportDto.setMaxMemoryPct((Float) map.get("max_memory_pct"));
                processReportDto.setMaxUptimePct(Float.valueOf((Float) map.get("max_uptime")).longValue());
                processReportDto.setAlarmNum((Long) map.get("sum_alarm_count"));
                processReportDto.setDownNum((Long) map.get("sum_down_count"));
                processReportDto.setDownTime((Float) map.get("sum_down_time"));
                processReportDto.setExeptionNum((Long) map.get("sum_exeption_count"));
                processReportDto.setExeptionTime((Float) map.get("sum_exeption_time"));
            }
            processReportDtos.add(processReportDto);

        }
        return processReportDtos;
    }
    public Map<String, Map<String, Object>> getProcessReportEs(SystemQueryDto systemQueryDto) {
        Map<String, Map<String, Object>> idMap = new HashMap<>();
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        AvgAggregationBuilder avgCpuPct = AggregationBuilders.avg("avg_cpu_pct").field("avg.cpu.pct").format("0.0000");
        MaxAggregationBuilder maxCpuPct = AggregationBuilders.max("max_cpu_pct").field("max.cpu.pct").format("0.0000");
        AvgAggregationBuilder avgMemoryPct = AggregationBuilders.avg("avg_memory_pct").field("avg.memory.pct").format("0.0000");
        MaxAggregationBuilder maxMemoryPct = AggregationBuilders.max("max_memory_pct").field("max.memory.pct").format("0.0000");
        MaxAggregationBuilder maxUptime = AggregationBuilders.max("max_uptime").field("max.uptime.pct");
        SumAggregationBuilder sumAlarmCount = AggregationBuilders.sum("sum_alarm_count").field("alarm.num");
        SumAggregationBuilder sumDownTime = AggregationBuilders.sum("sum_down_time").field("down.time");
        SumAggregationBuilder sumDownCount = AggregationBuilders.sum("sum_down_count").field("down.num");
        SumAggregationBuilder sumExeptionTime = AggregationBuilders.sum("sum_exeption_time").field("exeption.time");
        SumAggregationBuilder sumExeptionCount = AggregationBuilders.sum("sum_exeption_count").field("exeption.num");
        TermsAggregationBuilder groupById = AggregationBuilders.terms("group_related_id").field("related_id").size(10000);
        groupById.subAggregation(avgCpuPct);
        groupById.subAggregation(maxCpuPct);
        groupById.subAggregation(avgMemoryPct);
        groupById.subAggregation(maxMemoryPct);
        groupById.subAggregation(sumExeptionTime);
        groupById.subAggregation(sumExeptionCount);
        groupById.subAggregation(maxUptime);
        groupById.subAggregation(sumAlarmCount);
        groupById.subAggregation(sumDownTime);
        groupById.subAggregation(sumDownCount);
        search.aggregation(groupById);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_PROCESS_REPORT, search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return idMap;
            }
            ParsedStringTerms terms = aggregations.get("group_related_id");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                Terms.Bucket bucket = buckets.get(i);
                String id = bucket.getKeyAsString();
                ParsedAvg parsedCpuAvg = bucket.getAggregations().get("avg_cpu_pct");
                ParsedMax parsedCpuMax = bucket.getAggregations().get("max_cpu_pct");
                ParsedAvg parsedMemoryAvg = bucket.getAggregations().get("avg_memory_pct");
                ParsedMax parsedMemoryMax = bucket.getAggregations().get("max_memory_pct");
                ParsedMax parsedUptimeMax = bucket.getAggregations().get("max_uptime");
                ParsedSum parsedSumAlarmCount = bucket.getAggregations().get("sum_alarm_count");
                ParsedSum parsedSumDownTime = bucket.getAggregations().get("sum_down_time");
                ParsedSum parsedSumDownCount = bucket.getAggregations().get("sum_down_count");
                ParsedSum parsedSumExeptionTime = bucket.getAggregations().get("sum_exeption_time");
                ParsedSum parsedSumExeptionCount = bucket.getAggregations().get("sum_exeption_count");
                Map<String, Object> map = new HashMap<>();
                map.put("avg_cpu_pct", new BigDecimal(parsedCpuAvg.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_cpu_pct", new BigDecimal(parsedCpuMax.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("avg_memory_pct", new BigDecimal(parsedMemoryAvg.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("max_memory_pct", new BigDecimal(parsedMemoryMax.getValueAsString()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("sum_alarm_count", new BigDecimal(parsedSumAlarmCount.getValueAsString()).longValue());
                map.put("sum_down_time", new BigDecimal(parsedSumDownTime.getValueAsString()).divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("sum_down_count", new BigDecimal(parsedSumDownCount.getValueAsString()).longValue());
                float hours = new BigDecimal(parsedUptimeMax.getValueAsString()).divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP).floatValue();
                map.put("max_uptime", hours);
                map.put("sum_exeption_time", new BigDecimal(parsedSumExeptionTime.getValueAsString()).divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                map.put("sum_exeption_count", new BigDecimal(parsedSumExeptionCount.getValueAsString()).longValue());
                idMap.put(id, map);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idMap;
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

    public void exportExcel(ProcessConfigDto query) {
        ExcelUtil<ProcessReportVo> util = new ExcelUtil(ProcessReportVo.class);
        List<ProcessReportDto> list=this.getProcessReport(query);
        List<ProcessReportVo> processReportVos=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            ProcessReportVo processReportVo=new ProcessReportVo();
            BeanUtils.copyProperties(list.get(i),processReportVo);
            processReportVos.add(processReportVo);
        }
        List<ImageVo> imageVos=new ArrayList<>();
        ImageVo process=new ImageVo();
        byte[] chartByte= ImageUtils.generateImageToByte(query.getProcessChart());
        process.setCol1(1);
        process.setCol2(9);
        process.setRow1(0);
        process.setRow2(15);
        process.setBytes(chartByte);
        imageVos.add(process);
        util.exportExcelImage(processReportVos, "进程运行情况",imageVos,15);
    }

    public void exportPdf(ProcessConfigDto query) {
        ExcelUtil<ProcessReportVo> util = new ExcelUtil(ProcessReportVo.class);
        List<ProcessReportDto> list=this.getProcessReport(query);
        List<ProcessReportVo> processReportVos=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            ProcessReportVo processReportVo=new ProcessReportVo();
            BeanUtils.copyProperties(list.get(i),processReportVo);
            processReportVos.add(processReportVo);
        }
        List<ImageVo> imageVos=new ArrayList<>();
        ImageVo process=new ImageVo();
        byte[] chartByte= ImageUtils.generateImageToByte(query.getProcessChart());
        process.setCol1(1);
        process.setCol2(9);
        process.setRow1(0);
        process.setRow2(15);
        process.setBytes(chartByte);
        imageVos.add(process);
        util.exportPdf(processReportVos, "进程运行情况",imageVos,15);
    }
}

