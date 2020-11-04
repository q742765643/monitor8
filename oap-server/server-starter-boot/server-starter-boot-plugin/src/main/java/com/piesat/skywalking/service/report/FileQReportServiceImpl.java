package com.piesat.skywalking.service.report;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.api.folder.FileQReportService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.NullUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
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
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @ClassName : FileQReportServiceImpl
 * @Description : 文件监控报表
 * @Author : zzj
 * @Date: 2020-10-28 14:15
 */
@Service
public class FileQReportServiceImpl implements FileQReportService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private FileMonitorService fileMonitorService;

    public List<Map<String, String>> findHeader() {
        List<Map<String, String>> list = new ArrayList<>();
        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorDto);
        for (int i = 0; i < fileMonitorDtos.size(); i++) {
            FileMonitorDto file = fileMonitorDtos.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("taskId", file.getId());
            map.put("title", file.getTaskName());
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> fileLineDiagram(String taskId) {
        List<Map<String, Object>> list = new ArrayList<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchTaskId = QueryBuilders.matchQuery("task_id", taskId);
        boolBuilder.must(matchTaskId);
        search.query(boolBuilder).sort("@timestamp", SortOrder.DESC);
        search.size(10);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                jsonMap.put("late_num", 1);
                list.add(jsonMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list;
    }


    public List<Map<String, Object>> findFileReport(SystemQueryDto systemQueryDto) {
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.hours(1));
        TermsAggregationBuilder groupByTaskId = AggregationBuilders.terms("groupByTaskId").field("task_id").size(10000);
        SumAggregationBuilder sumRealFileSize = AggregationBuilders.sum("sumRealFileSize").field("real_file_size").format("0.0000");
        SumAggregationBuilder sumRealFileNum = AggregationBuilders.sum("sumRealFileNum").field("real_file_num").format("0.0000");
        SumAggregationBuilder sumLateNum = AggregationBuilders.sum("sumLateNum").field("late_num").format("0.0000");
        SumAggregationBuilder sumFileNum = AggregationBuilders.sum("sumFileNum").field("file_num").format("0.0000");
        groupByTaskId.subAggregation(sumRealFileNum);
        groupByTaskId.subAggregation(sumRealFileSize);
        groupByTaskId.subAggregation(sumLateNum);
        groupByTaskId.subAggregation(sumFileNum);
        dateHis.subAggregation(groupByTaskId);
        search.aggregation(dateHis);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    Histogram.Bucket bucket = buckets.get(i);
                    ZonedDateTime date = (ZonedDateTime) bucket.getKey();
                    long time = Timestamp.from(date.toInstant()).getTime();
                    Map<String, Aggregation> agg = bucket.getAggregations().asMap();
                    ParsedStringTerms parsedStringTerms = (ParsedStringTerms) agg.get("groupByTaskId");
                    List<? extends Terms.Bucket> bucketSum = parsedStringTerms.getBuckets();
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", time);
                    for (int j = 0; j < bucketSum.size(); j++) {
                        Terms.Bucket bucketV = bucketSum.get(j);
                        String taskId = bucketV.getKeyAsString();
                        ParsedSum sumRealFileNumV = bucketV.getAggregations().get("sumRealFileNum");
                        ParsedSum sumLateNumV = bucketV.getAggregations().get("sumLateNum");
                        ParsedSum sumRealFileSizeV = bucketV.getAggregations().get("sumRealFileSize");
                        ParsedSum sumFileNumV = bucketV.getAggregations().get("sumFileNum");
                        long sumRealFileNumL = new BigDecimal(sumRealFileNumV.getValueAsString()).longValue();
                        long sumLateNumL = new BigDecimal(sumLateNumV.getValueAsString()).longValue();
                        long sumRealFileSizeL = new BigDecimal(sumRealFileSizeV.getValueAsString()).divide(new BigDecimal(1024), 0, BigDecimal.ROUND_HALF_UP).longValue();
                        long sumFileNumL = new BigDecimal(sumFileNumV.getValueAsString()).longValue();
                        if (sumFileNumL > 0) {
                            float toQuoteRate = new BigDecimal(sumRealFileNumL + sumLateNumL).divide(new BigDecimal(sumFileNumL), 2, BigDecimal.ROUND_HALF_UP).floatValue();
                            map.put(taskId + "_toQuoteRate", toQuoteRate);
                        }
                        map.put(taskId + "_sumRealFileNum", sumRealFileNumL);
                        map.put(taskId + "_sumLateNum", sumLateNumL);
                        map.put(taskId + "_sumRealFileSize", sumRealFileSizeL);
                        map.put(taskId + "_sumFileNum", sumFileNumL);
                    }
                    list.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;

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

