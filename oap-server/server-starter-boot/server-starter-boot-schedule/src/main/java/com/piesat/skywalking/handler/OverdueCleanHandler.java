package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.common.OverdueCleanService;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.util.DateExpressionEngine;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.skywalking.oap.server.core.Const;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch.base.TimeSeriesUtils;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName : OverdueCleanHandler
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-03 17:22
 */
@Slf4j
@Service("overdueCleanHandler")
public class OverdueCleanHandler implements BaseHandler {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @GrpcHthtClient
    private OverdueCleanService overdueCleanService;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        this.deleteMysqlRecord("t_mt_file_monitor_log",3);
        this.deleteEsRecordString(IndexNameConstant.T_MT_ALARM_LOG,"@timestamp",30);
        this.deleteEsRecordString(IndexNameConstant.T_MT_FILE_MONITOR,"@timestamp",30);
        this.deleteEsIndex(IndexNameConstant.METRICBEAT,7);
        elasticSearch7Client.forceMerge();

    }
    public void deleteMysqlRecord(String table, int ttl){
        long time = System.currentTimeMillis();
        String endTimeBucket = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,-"+ttl+"d}", time);
        overdueCleanService.deleteRecord(table,endTimeBucket);
    }
    public void deleteEsIndex(String indexName, int ttl){
        long deadline = Long.valueOf(new DateTime().plusDays(0 - ttl).toString("yyyyMMdd"));
        try {
            List<String> indexes = elasticSearch7Client.retrievalIndexByAliases(indexName);
            List<String> prepareDeleteIndexes = new ArrayList<>();
            List<String> leftIndices = new ArrayList<>();
            for (String index : indexes) {
                long timeSeries = isolateTimeFromIndexName(index);
                if (deadline >= timeSeries) {
                    prepareDeleteIndexes.add(index);
                } else {
                    leftIndices.add(index);
                }
            }
            for (String prepareDeleteIndex : prepareDeleteIndexes) {
                elasticSearch7Client.deleteByIndexName(prepareDeleteIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void deleteEsRecordLong(String indexName, String timeBucketColumnName, int ttl){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long endTimeBucket = calendar.getTime().getTime()-86400*1000*ttl;
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
        deleteByQueryRequest.setAbortOnVersionConflict(false);
        deleteByQueryRequest.setQuery(QueryBuilders.rangeQuery(timeBucketColumnName).lte(endTimeBucket));
        try {
            elasticSearch7Client.deleteByQueryRequest(deleteByQueryRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(
                "delete indexName: {}, by query request: {}", indexName, deleteByQueryRequest
        );

    }
    public void deleteEsRecordString(String indexName, String timeBucketColumnName, int ttl){
        long time = System.currentTimeMillis();
        String endTimeBucket = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,-"+ttl+"d}", time);
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
        deleteByQueryRequest.setAbortOnVersionConflict(false);
        deleteByQueryRequest.setQuery(QueryBuilders.rangeQuery(timeBucketColumnName).lte(endTimeBucket).timeZone("+08:00").format("yyyy-MM-dd HH:mm:ss"));
        try {
            elasticSearch7Client.deleteByQueryRequest(deleteByQueryRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(
                "delete indexName: {}, by query request: {}", indexName, deleteByQueryRequest
        );
    }

    static long isolateTimeFromIndexName(String indexName) {
        return Long.valueOf(indexName.substring(indexName.lastIndexOf(Const.LINE) + 1));
    }
}

