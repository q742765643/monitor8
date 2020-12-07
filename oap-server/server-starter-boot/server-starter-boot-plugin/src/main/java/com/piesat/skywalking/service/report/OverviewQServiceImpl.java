package com.piesat.skywalking.service.report;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.report.OverviewQService;
import com.piesat.skywalking.dto.OverviewDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.NodeStatusDto;
import com.piesat.skywalking.dto.model.OverviewNodeDto;
import com.piesat.util.JsonParseUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : OverviewServiceImpl
 * @Description : 预览
 * @Author : zzj
 * @Date: 2020-10-19 17:56
 */
@Service
public class OverviewQServiceImpl implements OverviewQService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public List<OverviewDto> getOverview() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String endTime = dateFormat.format(time);
        String starTime = dateFormat.format(time - 6000000);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        List<OverviewDto> overviewDtos = this.getOverviewEs(systemQueryDto);
        return overviewDtos;
    }

    public OverviewNodeDto getNodes() {
        OverviewNodeDto overviewNodeDto = new OverviewNodeDto();
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String endTime = dateFormat.format(time);
        String starTime = dateFormat.format(time - 6000000);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);

        TermsAggregationBuilder groupbyIp = AggregationBuilders.terms("groupby_online").field("online").size(10000);
        search.aggregation(groupbyIp);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_MEDIA_OVERVIEW, search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return overviewNodeDto;
            }
            Map<String, Aggregation> aggregationMap = aggregations.asMap();
            ParsedLongTerms terms = (ParsedLongTerms) aggregationMap.get("groupby_online");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                Terms.Bucket bucket = buckets.get(i);
                long count = bucket.getDocCount();
                if ("1".equals(bucket.getKeyAsString())) {
                    overviewNodeDto.setReady(count);
                } else {
                    overviewNodeDto.setDown(count);
                }
            }
            overviewNodeDto.setTotal(overviewNodeDto.getDown() + overviewNodeDto.getReady());
            if (overviewNodeDto.getTotal() > 0) {
                BigDecimal use = new BigDecimal(overviewNodeDto.getReady()).divide(new BigDecimal(overviewNodeDto.getTotal()), 2, BigDecimal.ROUND_HALF_UP);
                overviewNodeDto.setUse(use.floatValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return overviewNodeDto;
    }

    public NodeStatusDto getNodesStatus() {
        NodeStatusDto nodeStatusDto = new NodeStatusDto();

        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String endTime = dateFormat.format(time);
        String starTime = dateFormat.format(time - 6000000);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        SumAggregationBuilder sumCpuUse = AggregationBuilders.sum("sumCpuUse").field("cpu.use").format("0.0000");
        SumAggregationBuilder sumMemoryUse = AggregationBuilders.sum("sumMemoryUse").field("memory.use").format("0.0000");
        SumAggregationBuilder sumCpu = AggregationBuilders.sum("sumCpu").field("cpu.cores").format("0.0000");
        SumAggregationBuilder sumMemory = AggregationBuilders.sum("sumMemory").field("memory.total").format("0.0000");
        search.aggregation(sumCpuUse);
        search.aggregation(sumMemoryUse);
        search.aggregation(sumCpu);
        search.aggregation(sumMemory);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_MEDIA_OVERVIEW, search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return nodeStatusDto;
            }
            Map<String, Aggregation> aggregationMap = aggregations.asMap();
            ParsedSum parsedSumCpuUse = (ParsedSum) aggregationMap.get("sumCpuUse");
            ParsedSum parsedSumCpu = (ParsedSum) aggregationMap.get("sumCpu");
            ParsedSum parsedSumMemoryUse = (ParsedSum) aggregationMap.get("sumMemoryUse");
            ParsedSum parsedSumMemory = (ParsedSum) aggregationMap.get("sumMemory");
            nodeStatusDto.setCpuUse(new BigDecimal(parsedSumCpuUse.getValueAsString()).floatValue());
            nodeStatusDto.setCpuCores(new BigDecimal(parsedSumCpu.getValueAsString()).floatValue());
            nodeStatusDto.setMemoryUse(new BigDecimal(parsedSumMemoryUse.getValueAsString()).divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());
            nodeStatusDto.setMemoryTotal(new BigDecimal(parsedSumMemory.getValueAsString()).divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nodeStatusDto;
    }

    public List<OverviewDto> getOverviewEs(SystemQueryDto systemQueryDto) {
        List<OverviewDto> overviewDtos = new ArrayList<>();
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        search.size(10000);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_MEDIA_OVERVIEW, search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                OverviewDto overviewDto = new OverviewDto();
                BigDecimal avgCpuPct = new BigDecimal(String.valueOf(jsonMap.get("avg.cpu.pct")));
                BigDecimal avgmemoryPct = new BigDecimal(String.valueOf(jsonMap.get("avg.memory.pct")));
                BigDecimal filesystemPct = new BigDecimal(String.valueOf(jsonMap.get("filesystem.pct")));
                overviewDto.setAvgCpuPct(avgCpuPct.floatValue());
                overviewDto.setAvgMemoryPct(avgmemoryPct.floatValue());
                overviewDto.setFilesystemPct(filesystemPct.floatValue());
                BigDecimal filesystemSize = new BigDecimal(String.valueOf(jsonMap.get("filesystem.size")));
                BigDecimal cpuCores = new BigDecimal(String.valueOf(jsonMap.get("cpu.cores")));
                BigDecimal memoryTotal = new BigDecimal(String.valueOf(jsonMap.get("memory.total")));
                BigDecimal processSize = new BigDecimal(String.valueOf(jsonMap.get("process.size")));
                BigDecimal filesystemUse = new BigDecimal(String.valueOf(jsonMap.get("filesystem.use.size")));
                BigDecimal cpuUse = new BigDecimal(String.valueOf(jsonMap.get("cpu.use")));
                BigDecimal memoryUse = new BigDecimal(String.valueOf(jsonMap.get("memory.use")));
                overviewDto.setFilesystemSize(filesystemSize.divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setCpuCores(cpuCores.intValue());
                overviewDto.setOnline(Integer.parseInt(String.valueOf(jsonMap.get("online"))));
                overviewDto.setIp(String.valueOf(jsonMap.get("ip")));
                overviewDto.setMemoryTotal(memoryTotal.divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setProcessSize(processSize.intValue());
                overviewDto.setFilesystemUse(filesystemUse.divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setFilesystemSize(filesystemSize.divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setCpuUse(cpuUse.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setMemoryUse(memoryUse.divide(new BigDecimal(1024 * 1024 * 1024), 2, BigDecimal.ROUND_HALF_UP).floatValue());

                overviewDtos.add(overviewDto);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return overviewDtos;
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
        searchSourceBuilder.sort("ip", SortOrder.DESC);
        return searchSourceBuilder;
    }
}

