package com.piesat.skywalking.service.report;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.SystemQueryDto;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName : FileQReportServiceImpl
 * @Description : 文件监控报表
 * @Author : zzj
 * @Date: 2020-10-28 14:15
 */
@Service
public class FileQReportServiceImpl {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public void findFileReport(SystemQueryDto systemQueryDto){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto);
        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("@timestamp");
        dateHis.dateHistogramInterval(DateHistogramInterval.hours(1));
        TermsAggregationBuilder groupByTaskId=AggregationBuilders.terms("groupByTaskId").field("task_id").size(10000);
        SumAggregationBuilder sumRealFileSize = AggregationBuilders.sum("sumRealFileSize").field("real_file_size").format("0.0000");
        SumAggregationBuilder sumRealFileNum = AggregationBuilders.sum("sumRealFileNum").field("real_file_num").format("0.0000");
        SumAggregationBuilder sumLateNum = AggregationBuilders.sum("sumLateNum").field("late_num").format("0.0000");
        SumAggregationBuilder sumFileNum = AggregationBuilders.sum("sumFileNum").field("file_num").format("0.0000");
        groupByTaskId.subAggregation(sumRealFileNum);
        groupByTaskId.subAggregation(sumRealFileSize);
        groupByTaskId.subAggregation(sumLateNum);
        groupByTaskId.subAggregation(sumFileNum);
        dateHis.subAggregation(groupByTaskId);

        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            Aggregations aggregations = searchResponse.getAggregations();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto){
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

