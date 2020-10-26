package com.piesat.skywalking.schedule.service.folder;

import cn.hutool.core.date.DateUtil;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.util.DateExpressionEngine;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.OwnException;
import com.piesat.util.ResultT;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch.base.RecordEsDAO;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
@Service
public abstract class FileBaseService {
    protected static final String REGEX = "(\\$\\{(.*?)\\})";
    protected static final Pattern PATTERN = Pattern.compile(REGEX);
    @Autowired
    protected FileLogService fileLogService;
    @Autowired
    protected ElasticSearch7Client elasticSearch7Client;
    protected  String getRegFromDatePattern(String datePattern){
        return  datePattern.replaceAll("[ymdhsYMDHS]", "\\\\d");
    }
    public String repalceRegx(FileMonitorLogDto fileMonitorLogDto, ResultT<String> resultT){
        String expression= null;
        try {
            String folderRegular= DateExpressionEngine.formatDateExpression(fileMonitorLogDto.getFolderRegular(), fileMonitorLogDto.getTriggerTime());
            String filenameRegular=fileMonitorLogDto.getFilenameRegular();
            Matcher m = PATTERN.matcher(filenameRegular);
            expression = "";
            while (m.find()){
                expression = m.group(2);
                int start = m.start(1);
                int end = m.end(1);
                String replaceMent =this.getRegFromDatePattern(expression);
                String expressionWrapper = fileMonitorLogDto.getFilenameRegular().substring(start, end);
                filenameRegular= StringUtils.replace(filenameRegular, expressionWrapper, replaceMent);
            }
            filenameRegular=filenameRegular.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
            fileMonitorLogDto.setFolderRegular(folderRegular);
            fileMonitorLogDto.setFilenameRegular(filenameRegular);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        return expression;
    }
    public long getDataTime(long createTime,String fileName,String dataFilePattern,ResultT<String> resultT){
        try {
            if(dataFilePattern != null && !"".equals(dataFilePattern)){
                Pattern p = Pattern.compile(getRegFromDatePattern(dataFilePattern));
                Matcher m = p.matcher(fileName);
                if(m.find()){
                    String timeStr = m.group(0);
                    SimpleDateFormat format=new SimpleDateFormat(dataFilePattern);
                    return DateUtil.parse(timeStr,format).getTime();
                }
            }
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        return createTime;
    }
    public  long bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024);
        long returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .longValue();
        return returnValue;
    }
    public FileMonitorLogDto insertLog(FileMonitorDto fileMonitorDto){
        return fileLogService.insertLog(fileMonitorDto);
    }

    public List<String> findExist(String taskId,long timeLevel){
        List<String> paths=new ArrayList<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchTaskId = QueryBuilders.matchQuery("task_id", taskId);
        MatchQueryBuilder matchTimeLevel = QueryBuilders.matchQuery("time_level", timeLevel);
        boolBuilder.must(matchTaskId);
        boolBuilder.must(matchTimeLevel);
        search.query(boolBuilder);
        search.size(20000);
        search.fetchSource(new String[]{"full_path"},null);

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

        return  paths;
    }
    public void updateFileStatistics(FileMonitorLogDto fileMonitorLogDto,ResultT<String> resultT){
        try {
            FileStatisticsDto fileStatisticsDto=new FileStatisticsDto();
            fileStatisticsDto.setStatus(4);
            fileStatisticsDto.setId(fileMonitorLogDto.getTaskId()+"_"+fileMonitorLogDto.getTriggerTime());
            fileStatisticsDto.setTaskId(fileMonitorLogDto.getTaskId());
            fileStatisticsDto.setFilenameRegular(fileMonitorLogDto.getFilenameRegular());
            fileStatisticsDto.setFileNum(fileMonitorLogDto.getFileNum());
            fileStatisticsDto.setFileSize(fileMonitorLogDto.getFileSize());
            fileStatisticsDto.setStartTimeL(fileMonitorLogDto.getTriggerTime());
            fileStatisticsDto.setStartTimeS(new Date(fileMonitorLogDto.getTriggerTime()));
            fileStatisticsDto.setRealFileSize(fileMonitorLogDto.getRealFileSize());
            fileStatisticsDto.setRealFileNum(fileMonitorLogDto.getRealFileNum());
            fileStatisticsDto.setLateNum(fileMonitorLogDto.getLateNum());
            fileStatisticsDto.setPerFileSize(new BigDecimal(fileMonitorLogDto.getRealFileSize()).divide(new BigDecimal(fileMonitorLogDto.getFileSize()),4,BigDecimal.ROUND_UP).floatValue());
            fileStatisticsDto.setPerFileNum(new BigDecimal(fileMonitorLogDto.getRealFileNum()).divide(new BigDecimal(fileMonitorLogDto.getFileNum()),4,BigDecimal.ROUND_UP).floatValue());
            fileStatisticsDto.setTimelinessRate(new BigDecimal(fileMonitorLogDto.getRealFileNum()+fileMonitorLogDto.getLateNum()).divide(new BigDecimal(fileMonitorLogDto.getFileNum()),4,BigDecimal.ROUND_UP).floatValue());
            Map<String,Object> source=new HashMap<>();
            source.put("task_id",fileStatisticsDto.getTaskId());
            source.put("filename_regular",fileStatisticsDto.getFilenameRegular());
            source.put("file_num",fileStatisticsDto.getFileNum());
            source.put("file_size",fileStatisticsDto.getFileSize());
            source.put("start_time_l",fileStatisticsDto.getStartTimeL());
            source.put("start_time_s",fileStatisticsDto.getStartTimeS());
            source.put("real_file_num",fileStatisticsDto.getRealFileNum());
            source.put("real_file_size",fileStatisticsDto.getRealFileSize());
            source.put("per_file_num",fileStatisticsDto.getPerFileNum());
            source.put("per_file_size",fileStatisticsDto.getPerFileSize());
            source.put("late_num",fileStatisticsDto.getLateNum());
            source.put("timeliness_rate",fileStatisticsDto.getTimelinessRate());
            source.put("status",fileStatisticsDto.getStatus());
            source.put("start_time_a",new Date());
            source.put("end_time_a",new Date());
            source.put("timestamp",new Date());
            String statisticsIndexName= IndexNameConstant.T_MT_FILE_STATISTICS;
            elasticSearch7Client.forceInsert(statisticsIndexName,fileStatisticsDto.getId(),source);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }


    }
    public void  updateLog(FileMonitorLogDto fileMonitorLogDto){
        fileLogService.updateLog(fileMonitorLogDto);
    }
}

