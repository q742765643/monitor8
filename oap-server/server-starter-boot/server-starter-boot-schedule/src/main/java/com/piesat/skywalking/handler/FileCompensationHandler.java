package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : FileCompensationHandler
 * @Description : 文件监控补偿
 * @Author : zzj
 * @Date: 2020-10-26 19:25
 */
@Slf4j
@Service("fileCompensationHandler")
public class FileCompensationHandler implements BaseHandler {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @GrpcHthtClient
    private FileMonitorService fileMonitorService;
    @Autowired
    private FileGuardHandler fileGuardHandler;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String endTime = dateFormat.format(time);
        String starTime = dateFormat.format(time - 1000 * 86400 * 3);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        List<Map> maps = this.findProcessed(systemQueryDto);
        for (int i = 0; i < maps.size(); i++) {
            try {
                long triggerTime = new BigDecimal(String.valueOf(maps.get(i).get("start_time_l"))).longValue();
                String taskId = (String) maps.get(i).get("task_id");
                FileMonitorDto fileMonitorDto = fileMonitorService.findById(taskId);
                fileMonitorDto.setTriggerLastTime(triggerTime);
                fileMonitorDto.setIsCompensation(1);
                JobContext job = new JobContext();
                job.setHtJobInfoDto(fileMonitorDto);
                fileGuardHandler.execute(job, new ResultT<String>());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public List<Map> findProcessed(SystemQueryDto systemQueryDto) {
        List<Map> maps = new ArrayList<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();

        RangeQueryBuilder perPct = QueryBuilders.rangeQuery("per_file_num");
        perPct.lt(1f);

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.must(perPct);
        boolBuilder.mustNot(QueryBuilders.matchQuery("status", 4));
        boolBuilder.filter(rangeQueryBuilder);
        search.query(boolBuilder);

        search.size(10000);
        search.fetchSource(new String[]{"task_id", "start_time_l"}, null);

        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                maps.add(jsonMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;

    }
}

