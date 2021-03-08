package com.piesat.skywalking.schedule.service.folder.base;

import cn.hutool.core.date.DateUtil;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.schedule.service.folder.FileLogService;
import com.piesat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : FlieBaseService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 17:00
 */
@Slf4j
@Service
public abstract class FileBaseService {
    protected static final String REGEX = "(\\$\\{(.*?)\\})";
    protected static final Pattern PATTERN = Pattern.compile(REGEX);
    @Autowired
    protected FileLogService fileLogService;
    @Autowired
    protected ElasticSearch7Client elasticSearch7Client;

    protected String getRegFromDatePattern(String datePattern) {
        return datePattern.replaceAll("[ymdhsYMDHS]", "\\\\d");
    }

    public abstract void singleFile(FileMonitorDto monitor, List<Map<String, Object>> fileList, ResultT<String> resultT);

    public String repalceRegx(FileMonitorLogDto fileMonitorLogDto, ResultT<String> resultT) {
        String expression = "";
        try {
            String folderRegular = DateExpressionEngine.formatDateExpression(fileMonitorLogDto.getFolderRegular(), fileMonitorLogDto.getTriggerTime());
            String filenameRegular = fileMonitorLogDto.getFilenameRegular();
            Matcher m = PATTERN.matcher(filenameRegular);
            while (m.find()) {
                String expression1 = m.group(2);
                if (expression1.length() > expression.length()) {
                    expression = expression1.split(",")[0];
                }
                fileMonitorLogDto.setAllExpression("${" + expression1.replace(expression,"yyyyMMddHHmmss") + "}");
                String replaceMent = expression1.split(",")[0].replaceAll("[ymdhsYMDHS]", "\\\\d");
                filenameRegular = filenameRegular.replace("${" + expression1 + "}", "("+replaceMent+")");
            }
            /*while (m.find()){
                expression = m.group(2);
                int start = m.start(1);
                int end = m.end(1);
                String replaceMent =this.getRegFromDatePattern(expression);
                String expressionWrapper = fileMonitorLogDto.getFilenameRegular().substring(start, end);
                filenameRegular= StringUtils.replace(filenameRegular, expressionWrapper, replaceMent);
            }*/
            filenameRegular = filenameRegular.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
            fileMonitorLogDto.setFolderRegular(folderRegular);
            fileMonitorLogDto.setFilenameRegular(filenameRegular);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        return expression;
    }

    public long getDataTime(long createTime,String filenameRegular, String fileName, String dataFilePattern, ResultT<String> resultT) {
        try {
            if (dataFilePattern != null && !"".equals(dataFilePattern)) {
                Pattern p = Pattern.compile(filenameRegular);
                Matcher m = p.matcher(fileName);
                while (m.find()) {
                    for (int j = 0; j <= m.groupCount(); j++){
                        try {
                            String timeStr = m.group(j);
                            SimpleDateFormat format = new SimpleDateFormat(dataFilePattern);
                            Map<String, String> map = HtDateUtil.getTime(new Date(createTime));
                            Date fileTime=DateUtil.parse(timeStr, format);
                            return HtDateUtil.matchingTime(fileTime, dataFilePattern, map);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        return createTime;
    }

    public long bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024);
        long returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_HALF_UP)
                .longValue();
        return returnValue;
    }

    public FileMonitorLogDto insertLog(FileMonitorDto fileMonitorDto) {
        return fileLogService.insertLog(fileMonitorDto);
    }

    public List<String> findExist(String taskId, long timeLevel) {
        List<String> paths = new ArrayList<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchTaskId = QueryBuilders.matchQuery("task_id", taskId);
        MatchQueryBuilder matchTimeLevel = QueryBuilders.matchQuery("time_level", timeLevel);
        boolBuilder.must(matchTaskId);
        boolBuilder.must(matchTimeLevel);
        search.query(boolBuilder);
        search.size(10000);
        search.fetchSource(new String[]{"full_path"}, null);

        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_MONITOR, search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                paths.add((String) jsonMap.get("full_path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return paths;
    }

    public void updateFileStatistics(FileMonitorLogDto fileMonitorLogDto, ResultT<String> resultT) {
        try {
            FileStatisticsDto fileStatisticsDto = new FileStatisticsDto();
            fileStatisticsDto.setStatus(fileMonitorLogDto.getStatus());
            fileStatisticsDto.setId(fileMonitorLogDto.getTaskId() + "_" + fileMonitorLogDto.getTriggerTime());
            fileStatisticsDto.setTaskId(fileMonitorLogDto.getTaskId());
            fileStatisticsDto.setTaskName(fileMonitorLogDto.getTaskName());
            fileStatisticsDto.setFilenameRegular(fileMonitorLogDto.getFilenameRegular());
            fileStatisticsDto.setFileNum(fileMonitorLogDto.getFileNum());
            fileStatisticsDto.setFileSize(fileMonitorLogDto.getFileSize());
            fileStatisticsDto.setStartTimeL(fileMonitorLogDto.getTriggerTime());
            fileStatisticsDto.setStartTimeS(new Date(fileMonitorLogDto.getTriggerTime()));
            fileStatisticsDto.setRealFileSize(fileMonitorLogDto.getRealFileSize());
            fileStatisticsDto.setRealFileNum(fileMonitorLogDto.getRealFileNum());
            fileStatisticsDto.setLateNum(fileMonitorLogDto.getLateNum());
            fileStatisticsDto.setPerFileSize(new BigDecimal(fileMonitorLogDto.getRealFileSize()).divide(new BigDecimal(fileMonitorLogDto.getFileSize()), 4, BigDecimal.ROUND_HALF_UP).floatValue());
            fileStatisticsDto.setTimelinessRate(new BigDecimal(fileMonitorLogDto.getRealFileNum()).divide(new BigDecimal(fileMonitorLogDto.getFileNum()), 4, BigDecimal.ROUND_HALF_UP).floatValue());
            fileStatisticsDto.setPerFileNum(new BigDecimal(fileMonitorLogDto.getRealFileNum() + fileMonitorLogDto.getLateNum()).divide(new BigDecimal(fileMonitorLogDto.getFileNum()), 4, BigDecimal.ROUND_HALF_UP).floatValue());
            fileStatisticsDto.setDdataTime(fileMonitorLogDto.getDdataTime());
            fileStatisticsDto.setErrorReason(fileMonitorLogDto.getErrorReason());
            fileStatisticsDto.setIp(fileMonitorLogDto.getIp());
            Map<String, Object> source = new HashMap<>();
            source.put("task_id", fileStatisticsDto.getTaskId());
            source.put("task_name", fileStatisticsDto.getTaskName());
            source.put("filename_regular", fileStatisticsDto.getFilenameRegular());
            source.put("file_num", fileStatisticsDto.getFileNum());
            source.put("file_size", fileStatisticsDto.getFileSize());
            source.put("start_time_l", fileStatisticsDto.getStartTimeL());
            source.put("start_time_s", fileStatisticsDto.getStartTimeS());
            source.put("real_file_num", fileStatisticsDto.getRealFileNum());
            source.put("real_file_size", fileStatisticsDto.getRealFileSize());
            source.put("per_file_num", fileStatisticsDto.getPerFileNum());
            source.put("per_file_size", fileStatisticsDto.getPerFileSize());
            source.put("late_num", fileStatisticsDto.getLateNum());
            source.put("timeliness_rate", fileStatisticsDto.getTimelinessRate());
            source.put("status", fileStatisticsDto.getStatus());
            source.put("error_reason", fileStatisticsDto.getErrorReason());
            source.put("ip", fileStatisticsDto.getIp());
            if(null==fileStatisticsDto.getStatus()){
                log.info("状态为null");
            }
            source.put("start_time_a", new Date());
            source.put("end_time_a", new Date());
            source.put("@timestamp", new Date());
            source.put("d_data_time", fileStatisticsDto.getDdataTime());
            String statisticsIndexName = IndexNameConstant.T_MT_FILE_STATISTICS;
   /*         try {
                boolean flag = elasticSearch7Client.isExistsIndex(statisticsIndexName);
                if (!flag) {
                    Map<String, Object> taskId = new HashMap<>();
                    taskId.put("type", "keyword");
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("task_id", taskId);
                    Map<String, Object> mapping = new HashMap<>();
                    mapping.put("properties", properties);
                    elasticSearch7Client.createIndex(statisticsIndexName, new HashMap<>(), mapping);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            try {
                GetResponse getResponse=elasticSearch7Client.get(statisticsIndexName,fileStatisticsDto.getId());
                Map<String,Object> map=getResponse.getSourceAsMap();
                if(null!=map.get("error_reason")){
                    source.put("error_reason", map.get("error_reason"));
                }
                if(null!=map.get("remark")) {
                    source.put("remark", map.get("remark"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            elasticSearch7Client.forceInsert(statisticsIndexName, fileStatisticsDto.getId(), source);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }


    }

    public void updateLog(FileMonitorLogDto fileMonitorLogDto) {
        fileLogService.updateLog(fileMonitorLogDto);
    }


    public void findSum(FileMonitorLogDto fileMonitorLogDto) {
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchTaskId = QueryBuilders.matchQuery("task_id", fileMonitorLogDto.getTaskId());
        MatchQueryBuilder matchTimeLevel = QueryBuilders.matchQuery("time_level", fileMonitorLogDto.getTriggerTime());
        boolBuilder.must(matchTaskId);
        boolBuilder.must(matchTimeLevel);
        search.query(boolBuilder);
        search.size(0);
        SumAggregationBuilder sumFileBytes = AggregationBuilders.sum("sumFileBytes").field("file_bytes");
        SumAggregationBuilder sumOntime = AggregationBuilders.sum("sumOntime").field("ontime");
        SumAggregationBuilder sumNoOnTime = AggregationBuilders.sum("sumNoOnTime").field("no_ontime");
        search.aggregation(sumFileBytes);
        search.aggregation(sumOntime);
        search.aggregation(sumNoOnTime);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_MONITOR, search);
            Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations == null) {
                return;
            }
            Map<String, Aggregation> aggregationMap = aggregations.asMap();
            ParsedSum parsedSumFileBytes = (ParsedSum) aggregationMap.get("sumFileBytes");
            ParsedSum parsedSumOntime = (ParsedSum) aggregationMap.get("sumOntime");
            ParsedSum parsedSumNoOnTime = (ParsedSum) aggregationMap.get("sumNoOnTime");
            fileMonitorLogDto.setRealFileSize(new BigDecimal(parsedSumFileBytes.getValueAsString()).longValue());
            fileMonitorLogDto.setRealFileNum(new BigDecimal(parsedSumOntime.getValueAsString()).longValue());
            fileMonitorLogDto.setLateNum(new BigDecimal(parsedSumNoOnTime.getValueAsString()).longValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertFilePath(List<Map<String, Object>> fileList, FileMonitorLogDto fileMonitorLogDto, ResultT<String> resultT) {

        String indexName = IndexNameConstant.T_MT_FILE_MONITOR;
        BulkRequest request = new BulkRequest();
        String ip = NetUtils.getLocalHost();
        try {
            for (int i = 0; i < fileList.size(); i++) {
                Map<String, Object> source = fileList.get(i);
                source.put("ip", ip);
                source.put("time_level", fileMonitorLogDto.getTriggerTime());
                source.put("task_id", fileMonitorLogDto.getTaskId());
                source.put("ontime", 1l);
                source.put("no_ontime", 0l);
                if (fileMonitorLogDto.getIsCompensation() == 1) {
                    source.put("ontime", 0l);
                    source.put("no_ontime", 1l);
                }
                source.put("@timestamp", new Date());
                IndexRequest indexRequest = new ElasticSearch7InsertRequest(indexName, (String) source.get("full_path")).source(source);
                request.add(indexRequest);
            }
            if (request.requests().size() > 0) {
                elasticSearch7Client.bulkEx(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultT.setErrorMessage(OwnException.get(e));
        }
        this.findSum(fileMonitorLogDto);
    }
}

