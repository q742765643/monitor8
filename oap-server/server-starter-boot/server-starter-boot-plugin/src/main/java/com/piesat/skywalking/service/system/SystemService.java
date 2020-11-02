package com.piesat.skywalking.service.system;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.aggregations.pipeline.BucketScriptPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.DerivativePipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.ParsedSimpleValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public List<NetworkVo> getNetwork(SystemQueryDto systemQueryDto) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.network");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        MatchQueryBuilder matchName = QueryBuilders.matchQuery("system.network.name", "lo*");
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        boolBuilder.mustNot(matchName);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);

        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.seconds(30));
        AvgAggregationBuilder sumInBytes = AggregationBuilders.avg("sumInBytes");
        sumInBytes.field("system.network.in.bytes");
        DerivativePipelineAggregationBuilder derivativeInBytes = PipelineAggregatorBuilders.derivative("derivativeInBytes", "sumInBytes");

        Script pipeInBytes = new Script("params.derivativeInBytes / 1024");
        Map<String, String> pipeInBytesMap = new HashMap<>();
        pipeInBytesMap.put("derivativeInBytes", "derivativeInBytes");
        BucketScriptPipelineAggregationBuilder bucketScriptInBytes = PipelineAggregatorBuilders.bucketScript("inSpeed", pipeInBytesMap, pipeInBytes);
        bucketScriptInBytes.format("0.0000");

        AvgAggregationBuilder sumOutBytes = AggregationBuilders.avg("sumOutBytes");
        sumOutBytes.field("system.network.out.bytes");
        DerivativePipelineAggregationBuilder derivativeOutBytes = PipelineAggregatorBuilders.derivative("derivativeOutBytes", "sumOutBytes");
        Script pipeOutBytes = new Script("params.derivativeOutBytes / 1024");
        Map<String, String> pipeOutBytesMap = new HashMap<>();
        pipeOutBytesMap.put("derivativeOutBytes", "derivativeOutBytes");
        BucketScriptPipelineAggregationBuilder bucketScriptOutBytes = PipelineAggregatorBuilders.bucketScript("outSpeed", pipeOutBytesMap, pipeOutBytes);
        bucketScriptOutBytes.format("0.0000");

        dateHis.subAggregation(sumInBytes);
        dateHis.subAggregation(sumOutBytes);


        dateHis.subAggregation(derivativeInBytes);
        dateHis.subAggregation(derivativeOutBytes);

        Script selectInBytes = new Script("params.derivativeOutBytes != null && params.derivativeOutBytes > 0");
        Map<String, String> selectInBytesMap = new HashMap<>();
        selectInBytesMap.put("derivativeOutBytes", "derivativeOutBytes");
        BucketSelectorPipelineAggregationBuilder bucketSelectorInBytes = PipelineAggregatorBuilders.bucketSelector("selectInBytes", selectInBytesMap, selectInBytes);

        dateHis.subAggregation(bucketSelectorInBytes);

        dateHis.subAggregation(bucketScriptInBytes);
        dateHis.subAggregation(bucketScriptOutBytes);

        searchSourceBuilder.aggregation(dateHis);
        searchSourceBuilder.size(0);
        List<NetworkVo> networkVos = new ArrayList<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);

            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            if (buckets.size() > 0) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
                for (int i = 0; i < buckets.size(); i++) {
                    NetworkVo networkVo = new NetworkVo();
                    Histogram.Bucket bucket = buckets.get(i);
                    ZonedDateTime date = (ZonedDateTime) bucket.getKey();
                    networkVo.setTimestamp(formatter.format(date));
                    ParsedSimpleValue inSpeedParsed = bucket.getAggregations().get("inSpeed");
                    if (inSpeedParsed != null) {
                        networkVo.setInSpeed(new BigDecimal(inSpeedParsed.getValueAsString()).doubleValue());
                    }
                    ParsedSimpleValue outSpeedParsed = bucket.getAggregations().get("outSpeed");
                    if (outSpeedParsed != null) {
                        networkVo.setOutSpeed(new BigDecimal(outSpeedParsed.getValueAsString()).doubleValue());
                    }
                    networkVos.add(networkVo);


                }
            }

           /*     SearchHits hits = searchResponse.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                log.info("search -> {}",hit.getSourceAsMap());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return networkVos;

    }

    public List<DiskioVo> getDiskio(SystemQueryDto systemQueryDto) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.diskio");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);

        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.seconds(30));
        AvgAggregationBuilder sumRead = AggregationBuilders.avg("sumRead");
        sumRead.field("system.diskio.read.bytes");
        DerivativePipelineAggregationBuilder derivativeRead = PipelineAggregatorBuilders.derivative("derivativeRead", "sumRead");

        Script pipeRead = new Script("params.derivativeRead / 1024");
        Map<String, String> pipeReadMap = new HashMap<>();
        pipeReadMap.put("derivativeRead", "derivativeRead");
        BucketScriptPipelineAggregationBuilder bucketScriptRead = PipelineAggregatorBuilders.bucketScript("read", pipeReadMap, pipeRead);
        bucketScriptRead.format("0.0000");

        AvgAggregationBuilder sumWrite = AggregationBuilders.avg("sumWrite");
        sumWrite.field("system.diskio.write.bytes");
        DerivativePipelineAggregationBuilder derivativeWrite = PipelineAggregatorBuilders.derivative("derivativeWrite", "sumWrite");
        Script pipeWrite = new Script("params.derivativeWrite / 1024");
        Map<String, String> pipeWriteMap = new HashMap<>();
        pipeWriteMap.put("derivativeWrite", "derivativeWrite");
        BucketScriptPipelineAggregationBuilder bucketScriptWrite = PipelineAggregatorBuilders.bucketScript("write", pipeWriteMap, pipeWrite);
        bucketScriptWrite.format("0.0000");

        dateHis.subAggregation(sumRead);
        dateHis.subAggregation(sumWrite);


        dateHis.subAggregation(derivativeRead);
        dateHis.subAggregation(derivativeWrite);

        Script selectRead = new Script("params.derivativeWrite != null && params.derivativeWrite > 0");
        Map<String, String> selectReadMap = new HashMap<>();
        selectReadMap.put("derivativeWrite", "derivativeWrite");
        BucketSelectorPipelineAggregationBuilder bucketSelectorRead = PipelineAggregatorBuilders.bucketSelector("selectRead", selectReadMap, selectRead);
        dateHis.subAggregation(bucketSelectorRead);

        dateHis.subAggregation(bucketScriptRead);
        dateHis.subAggregation(bucketScriptWrite);


        searchSourceBuilder.aggregation(dateHis);
        searchSourceBuilder.size(0);
        List<DiskioVo> diskioVos = new ArrayList<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);

            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            if (buckets.size() > 0) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
                for (int i = 0; i < buckets.size(); i++) {
                    DiskioVo diskioVo = new DiskioVo();
                    Histogram.Bucket bucket = buckets.get(i);
                    ZonedDateTime date = (ZonedDateTime) bucket.getKey();
                    diskioVo.setTimestamp(formatter.format(date));
                    ParsedSimpleValue readParsed = bucket.getAggregations().get("read");
                    if (readParsed != null) {
                        diskioVo.setRead(new BigDecimal(readParsed.getValueAsString()).doubleValue());
                    }
                    ParsedSimpleValue writeParsed = bucket.getAggregations().get("write");
                    if (writeParsed != null) {
                        diskioVo.setWrite(new BigDecimal(writeParsed.getValueAsString()).doubleValue());
                    }
                    diskioVos.add(diskioVo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diskioVos;

    }

    public List<CpuVo> getCpu(SystemQueryDto systemQueryDto) {
        List<CpuVo> cpuVos = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.cpu");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);

        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.seconds(30));

        AvgAggregationBuilder avgCpu = AggregationBuilders.avg("usage");
        avgCpu.field("system.cpu.total.norm.pct");
        avgCpu.format("0.0000");

        Script selectCpu = new Script("params.usage>0");
        Map<String, String> selectCpuMap = new HashMap<>();
        selectCpuMap.put("usage", "usage");
        BucketSelectorPipelineAggregationBuilder bucketSelector = PipelineAggregatorBuilders.bucketSelector("selectCpu", selectCpuMap, selectCpu);

        dateHis.subAggregation(avgCpu);
        dateHis.subAggregation(bucketSelector);
        searchSourceBuilder.aggregation(dateHis);
        searchSourceBuilder.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);

            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            if (buckets.size() > 0) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
                for (int i = 0; i < buckets.size(); i++) {
                    CpuVo cpuVo = new CpuVo();
                    Histogram.Bucket bucket = buckets.get(i);
                    ZonedDateTime date = (ZonedDateTime) bucket.getKey();
                    cpuVo.setTimestamp(formatter.format(date));
                    ParsedAvg parsedAvg = bucket.getAggregations().get("usage");
                    if (parsedAvg != null) {
                        cpuVo.setUsage(new BigDecimal(parsedAvg.getValueAsString()).doubleValue());
                    }
                    cpuVos.add(cpuVo);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuVos;
    }

    @SneakyThrows
    public List<MemoryVo> getMemory(SystemQueryDto systemQueryDto) {
        List<MemoryVo> memoryVos = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.memory");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);

        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.seconds(30));

        AvgAggregationBuilder avgMemory = AggregationBuilders.avg("usage");
        avgMemory.field("system.memory.used.pct");
        avgMemory.format("0.0000");

        Script selectMemory = new Script("params.usage>0");
        Map<String, String> selectMemoryMap = new HashMap<>();
        selectMemoryMap.put("usage", "usage");
        BucketSelectorPipelineAggregationBuilder bucketSelector = PipelineAggregatorBuilders.bucketSelector("selectMemory", selectMemoryMap, selectMemory);

        dateHis.subAggregation(avgMemory);
        dateHis.subAggregation(bucketSelector);
        searchSourceBuilder.aggregation(dateHis);
        searchSourceBuilder.size(0);

        SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);

        Aggregations aggregations = searchResponse.getAggregations();
        ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
        List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
        if (buckets.size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
            for (int i = 0; i < buckets.size(); i++) {
                MemoryVo memoryVo = new MemoryVo();
                Histogram.Bucket bucket = buckets.get(i);
                ZonedDateTime date = (ZonedDateTime) bucket.getKey();
                memoryVo.setTimestamp(formatter.format(date));
                ParsedAvg parsedAvg = bucket.getAggregations().get("usage");
                if (parsedAvg != null) {
                    memoryVo.setUsage(new BigDecimal(parsedAvg.getValueAsString()).doubleValue());
                }
                memoryVos.add(memoryVo);

            }
        }

        return memoryVos;
    }

    @SneakyThrows
    public List<FileSystemVo> getFileSystem(SystemQueryDto systemQueryDto) {
        List<FileSystemVo> fileSystemVos = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.filesystem");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        MaxAggregationBuilder maxTime = AggregationBuilders.max("@timestamp");
        maxTime.field("@timestamp");
        searchSourceBuilder.aggregation(maxTime);

        SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedMax parsedMax = aggregations.get("@timestamp");
        if (parsedMax != null) {
            String timestamp = parsedMax.getValueAsString();
            BoolQueryBuilder boolFileBuilder = QueryBuilders.boolQuery();
            QueryBuilder queryBuilder = QueryBuilders.termQuery("@timestamp", timestamp);
            WildcardQueryBuilder wildDocker = QueryBuilders.wildcardQuery("system.filesystem.mount_point", "*docker*");
            WildcardQueryBuilder wildKubernetes = QueryBuilders.wildcardQuery("system.filesystem.mount_point", "*kubernetes*");
            boolFileBuilder.must(queryBuilder);
            boolFileBuilder.must(matchEvent);
            boolFileBuilder.must(matchIp);
            boolFileBuilder.mustNot(wildDocker);
            boolFileBuilder.mustNot(wildKubernetes);
            SearchSourceBuilder fileSearch = new SearchSourceBuilder();
            fileSearch.query(boolFileBuilder);
            String[] fields = new String[]{"system.filesystem.mount_point", "system.filesystem.free", "system.filesystem.used.pct"};
            fileSearch.fetchSource(fields, null);
            fileSearch.size(1000);
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", fileSearch);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                FileSystemVo fileSystemVo = new FileSystemVo();
                Map<String, Object> map = hit.getSourceAsMap();
                Map<String, Object> system = (Map<String, Object>) map.get("system");
                Map<String, Object> filesystem = (Map<String, Object>) system.get("filesystem");
                Map<String, Object> used = (Map<String, Object>) filesystem.get("used");
                fileSystemVo.setDiskName((String) filesystem.get("mount_point"));
                fileSystemVo.setFree(new BigDecimal(String.valueOf(filesystem.get("free"))).divide(new BigDecimal(1024 * 1024 * 1024), 4, RoundingMode.HALF_UP).doubleValue());
                fileSystemVo.setUsage(new BigDecimal(String.valueOf(used.get("pct"))).doubleValue());
                fileSystemVos.add(fileSystemVo);
            }

        }


        return fileSystemVos;

    }

    @SneakyThrows
    public List<ProcessVo> getProcess(SystemQueryDto systemQueryDto) {
        List<ProcessVo> processVos = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.process");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        MaxAggregationBuilder maxTime = AggregationBuilders.max("@timestamp");
        maxTime.field("@timestamp");
        searchSourceBuilder.aggregation(maxTime);

        SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedMax parsedMax = aggregations.get("@timestamp");
        if (parsedMax != null) {
            String timestamp = parsedMax.getValueAsString();
            BoolQueryBuilder boolFileBuilder = QueryBuilders.boolQuery();
            QueryBuilder queryBuilder = QueryBuilders.termQuery("@timestamp", timestamp);
            boolFileBuilder.must(queryBuilder);
            boolFileBuilder.must(matchEvent);
            boolFileBuilder.must(matchIp);
            SearchSourceBuilder prcessSearch = new SearchSourceBuilder();
            prcessSearch.query(boolFileBuilder);
            String[] fields = new String[]{"process.name", "system.process.cmdline"};
            prcessSearch.fetchSource(fields, null);
            prcessSearch.size(1000);
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", prcessSearch);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                ProcessVo processVo = new ProcessVo();
                Map<String, Object> map = hit.getSourceAsMap();
                Map<String, Object> process = (Map<String, Object>) map.get("process");
                Map<String, Object> system = (Map<String, Object>) map.get("system");
                Map<String, Object> systemProcess = (Map<String, Object>) system.get("process");
                processVo.setProcessName(String.valueOf(process.get("name")));
                processVo.setCmdline(String.valueOf(systemProcess.get("cmdline")));
                processVos.add(processVo);
            }

        }


        return processVos;

    }
}
