package com.piesat.skywalking.schedule.service.report;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.skywalking.util.Ping;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.util.NullUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : OverviewService
 * @Description : 资源概览
 * @Author : zzj
 * @Date: 2020-10-19 14:56
 */
@Service
public class OverviewService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @GrpcHthtClient
    private HostConfigService hostConfigService;

    public void getSystem(SystemQueryDto systemQueryDto) {
        Map<String, Map<String, Object>> baseInfo = new HashMap<>();
        this.getCpu(systemQueryDto, baseInfo);
        this.getMemory(systemQueryDto, baseInfo);
        this.getFilesystem(systemQueryDto, baseInfo);
        this.getProcess(systemQueryDto, baseInfo);
        this.getCpuSize(systemQueryDto, baseInfo);
        this.getMemorySize(systemQueryDto, baseInfo);
        HostConfigDto hostConfigdto = new HostConfigDto();
        NullUtil.changeToNull(hostConfigdto);
        hostConfigdto.setDeviceType(0);
        List<HostConfigDto> hostConfigDtos = hostConfigService.selectBySpecification(hostConfigdto);
        BulkRequest request = new BulkRequest();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = new Date();
        for (HostConfigDto hostConfigDto : hostConfigDtos) {
            Map<String, Object> source = this.getMap();
            source.put("ip", hostConfigDto.getIp());
            source.put("@timestamp", time);
            source.put("device_type", hostConfigDto.getDeviceType());
            if (null != baseInfo.get(hostConfigDto.getIp())) {
                source.putAll(baseInfo.get(hostConfigDto.getIp()));
            }
            long memoryTotal = (long) source.get("memory.total");
            long cpuCores = (long) source.get("cpu.cores");
            float cpuUse = (float) source.get("avg.cpu.pct");
            float memoryUse = (float) source.get("avg.memory.pct");
            source.put("cpu.use", cpuUse * cpuCores);
            source.put("memory.use", memoryUse * memoryTotal);
            boolean flag=false;
            try {
                 flag= Ping.ping(hostConfigDto.getIp());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!flag) {
                source.put("online", 0l);
            }
            IndexRequest indexRequest = new ElasticSearch7InsertRequest(IndexNameConstant.T_MT_MEDIA_OVERVIEW, hostConfigDto.getIp()).source(source);
            request.add(indexRequest);
        }
        elasticSearch7Client.synchronousBulk(request);

    }

    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto, String type) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system." + type);
        //MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        //boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }

    public Map<String, Object> getValueMap(Map<String, Map<String, Object>> baseInfo, String ip) {
        if (null != baseInfo.get(ip)) {
            return baseInfo.get(ip);
        } else {
            return new HashMap<>();
        }
    }

    public void getCpu(SystemQueryDto systemQueryDto, Map<String, Map<String, Object>> baseInfo) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto, "cpu");
        AvgAggregationBuilder avgPct = AggregationBuilders.avg("avg_cpu_pct").field("system.cpu.total.norm.pct").format("0.0000");
        TermsAggregationBuilder groupById = AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupById.subAggregation(avgPct);
        search.aggregation(groupById);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            ParsedStringTerms terms = aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                Terms.Bucket bucket = buckets.get(i);
                String ip = bucket.getKeyAsString();
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg_cpu_pct");
                float avgCpuPct = Float.parseFloat(parsedAvg.getValueAsString());
                Map<String, Object> value = this.getValueMap(baseInfo, ip);
                value.put("avg.cpu.pct", avgCpuPct);
                baseInfo.put(ip, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getMemory(SystemQueryDto systemQueryDto, Map<String, Map<String, Object>> baseInfo) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto, "memory");
        AvgAggregationBuilder avgPct = AggregationBuilders.avg("avg_memory_pct").field("system.memory.used.pct").format("0.0000");
        TermsAggregationBuilder groupById = AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupById.subAggregation(avgPct);
        search.aggregation(groupById);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            ParsedStringTerms terms = aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                Terms.Bucket bucket = buckets.get(i);
                String ip = bucket.getKeyAsString();
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg_memory_pct");
                float avgMemoryPct = Float.parseFloat(parsedAvg.getValueAsString());
                Map<String, Object> value = this.getValueMap(baseInfo, ip);
                value.put("avg.memory.pct", avgMemoryPct);
                baseInfo.put(ip, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getFilesystem(SystemQueryDto systemQueryDto, Map<String, Map<String, Object>> baseInfo) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto, "filesystem");
        TermsAggregationBuilder groupbyIp = AggregationBuilders.terms("groupby_ip ").field("host.name").size(10000);
        TermsAggregationBuilder groupbyTime = AggregationBuilders.terms("groupby_time").field("@timestamp").size(1).order(BucketOrder.key(false));
        SumAggregationBuilder filesystemUse = AggregationBuilders.sum("use_filesystem_size").field("system.filesystem.used.bytes").format("0.0000");
        SumAggregationBuilder sumFileSize = AggregationBuilders.sum("sum_filesystem_size").field("system.filesystem.total").format("0.0000");
        groupbyTime.subAggregation(filesystemUse);
        groupbyTime.subAggregation(sumFileSize);
        groupbyIp.subAggregation(groupbyTime);
        search.aggregation(groupbyIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            List<Aggregation> aggs = aggregations.asList();
            for (int i = 0; i < aggs.size(); i++) {
                ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggs.get(i);
                List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
                for (int j = 0; j < buckets.size(); j++) {
                    Terms.Bucket bucket = buckets.get(j);
                    String ip = bucket.getKeyAsString();
                    ParsedLongTerms parsedLongTerms = bucket.getAggregations().get("groupby_time");
                    List<? extends Terms.Bucket> bucketsTime = parsedLongTerms.getBuckets();

                    for (int k = 0; k < bucketsTime.size(); k++) {
                        Terms.Bucket bucketTime = bucketsTime.get(k);
                        Map<String, Object> value = this.getValueMap(baseInfo, ip);
                        ParsedSum parsedUseSum = bucketTime.getAggregations().get("use_filesystem_size");
                        ParsedSum parsedSum = bucketTime.getAggregations().get("sum_filesystem_size");
                        value.put("filesystem.use.size", Float.parseFloat(parsedUseSum.getValueAsString()));
                        value.put("filesystem.size", new BigDecimal(parsedSum.getValueAsString()).longValue());
                        BigDecimal filesystemPct = new BigDecimal(parsedUseSum.getValueAsString()).divide(new BigDecimal(parsedSum.getValueAsString()), 4, BigDecimal.ROUND_HALF_UP);
                        value.put("filesystem.pct", filesystemPct.floatValue());

                        baseInfo.put(ip, value);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProcess(SystemQueryDto systemQueryDto, Map<String, Map<String, Object>> baseInfo) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto, "process");
        TermsAggregationBuilder groupbyIp = AggregationBuilders.terms("groupby_ip ").field("host.name").size(10000);
        TermsAggregationBuilder groupbyTime = AggregationBuilders.terms("groupby_time").field("@timestamp").size(1).order(BucketOrder.key(false));
        ValueCountAggregationBuilder sumProcess = AggregationBuilders.count("sum_process").field("process.pid");
        groupbyTime.subAggregation(sumProcess);
        groupbyIp.subAggregation(groupbyTime);
        search.aggregation(groupbyIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            List<Aggregation> aggs = aggregations.asList();
            for (int i = 0; i < aggs.size(); i++) {
                ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggs.get(i);
                List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
                for (int j = 0; j < buckets.size(); j++) {
                    Terms.Bucket bucket = buckets.get(j);
                    String ip = bucket.getKeyAsString();
                    ParsedLongTerms parsedLongTerms = bucket.getAggregations().get("groupby_time");
                    List<? extends Terms.Bucket> bucketsTime = parsedLongTerms.getBuckets();

                    for (int k = 0; k < bucketsTime.size(); k++) {
                        Terms.Bucket bucketTime = bucketsTime.get(k);
                        Map<String, Object> value = this.getValueMap(baseInfo, ip);
                        ParsedValueCount parsedValueCount = bucketTime.getAggregations().get("sum_process");
                        value.put("process.size", new BigDecimal(parsedValueCount.getValueAsString()).longValue());
                        baseInfo.put(ip, value);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCpuSize(SystemQueryDto systemQueryDto, Map<String, Map<String, Object>> baseInfo) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto, "cpu");
        TermsAggregationBuilder groupbyIp = AggregationBuilders.terms("groupby_ip ").field("host.name").size(10000);
        TermsAggregationBuilder groupbyTime = AggregationBuilders.terms("groupby_time").field("@timestamp").size(1).order(BucketOrder.key(false));
        MaxAggregationBuilder maxCpuCores = AggregationBuilders.max("sum_cpu_size").field("system.cpu.cores").format("0");
        groupbyTime.subAggregation(maxCpuCores);
        groupbyIp.subAggregation(groupbyTime);
        search.aggregation(groupbyIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            List<Aggregation> aggs = aggregations.asList();
            for (int i = 0; i < aggs.size(); i++) {
                ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggs.get(i);
                List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
                for (int j = 0; j < buckets.size(); j++) {
                    Terms.Bucket bucket = buckets.get(j);
                    String ip = bucket.getKeyAsString();
                    ParsedLongTerms parsedLongTerms = bucket.getAggregations().get("groupby_time");
                    List<? extends Terms.Bucket> bucketsTime = parsedLongTerms.getBuckets();

                    for (int k = 0; k < bucketsTime.size(); k++) {
                        Terms.Bucket bucketTime = bucketsTime.get(k);
                        Map<String, Object> value = this.getValueMap(baseInfo, ip);
                        ParsedMax parsedMax = bucketTime.getAggregations().get("sum_cpu_size");
                        value.put("cpu.cores", new BigDecimal(parsedMax.getValueAsString()).longValue());
                        baseInfo.put(ip, value);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMemorySize(SystemQueryDto systemQueryDto, Map<String, Map<String, Object>> baseInfo) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto, "memory");
        TermsAggregationBuilder groupbyIp = AggregationBuilders.terms("groupby_ip ").field("host.name").size(10000);
        TermsAggregationBuilder groupbyTime = AggregationBuilders.terms("groupby_time").field("@timestamp").size(1).order(BucketOrder.key(false));
        MaxAggregationBuilder maxMemory = AggregationBuilders.max("sum_memory_size").field("system.memory.total").format("0");
        groupbyTime.subAggregation(maxMemory);
        groupbyIp.subAggregation(groupbyTime);
        search.aggregation(groupbyIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            List<Aggregation> aggs = aggregations.asList();
            for (int i = 0; i < aggs.size(); i++) {
                ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggs.get(i);
                List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
                for (int j = 0; j < buckets.size(); j++) {
                    Terms.Bucket bucket = buckets.get(j);
                    String ip = bucket.getKeyAsString();
                    ParsedLongTerms parsedLongTerms = bucket.getAggregations().get("groupby_time");
                    List<? extends Terms.Bucket> bucketsTime = parsedLongTerms.getBuckets();

                    for (int k = 0; k < bucketsTime.size(); k++) {
                        Terms.Bucket bucketTime = bucketsTime.get(k);
                        Map<String, Object> value = this.getValueMap(baseInfo, ip);
                        ParsedMax parsedMax = bucketTime.getAggregations().get("sum_memory_size");
                        value.put("memory.total", new BigDecimal(parsedMax.getValueAsString()).longValue());
                        baseInfo.put(ip, value);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("avg.cpu.pct", 0f);
        map.put("avg.memory.pct", 0f);
        map.put("filesystem.pct", 0f);
        map.put("filesystem.use.size", 0f);
        map.put("filesystem.size", 0l);
        map.put("process.size", 0l);
        map.put("cpu.cores", 0l);
        map.put("cpu.use", 0f);
        map.put("memory.total", 0l);
        map.put("memory.use", 0f);
        map.put("online", 1l);
        return map;
    }


}

