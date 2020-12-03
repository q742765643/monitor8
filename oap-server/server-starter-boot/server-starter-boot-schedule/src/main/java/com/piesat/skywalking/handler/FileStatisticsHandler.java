package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.util.CronExpression;
import com.piesat.util.IndexNameUtil;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


@Slf4j
@Service("fileStatisticsHandler")
public class FileStatisticsHandler implements BaseHandler {
    @GrpcHthtClient
    private FileMonitorService fileMonitorService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

/*    @Override
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT) {
        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        fileMonitorDto.setTriggerStatus(null);
        List<FileMonitorDto> fileMonitorDtos=fileMonitorService.selectBySpecification(fileMonitorDto);
        if(fileMonitorDtos==null){
            return null;
        }
        return fileMonitorDtos;
    }*/

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        FileMonitorDto fileMonitorQDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorQDto);
        fileMonitorQDto.setTriggerStatus(1);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorQDto);
        List<FileStatisticsDto> fileStatisticsDtos = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTime().getTime();
        long endTime = startTime + 86400 * 1000;

        for (FileMonitorDto fileMonitorDto : fileMonitorDtos) {
            long nowTime = startTime;
            int i = 0;
            while (nowTime <= endTime) {
                try {
                    Date nextValidTime = new CronExpression(fileMonitorDto.getJobCron()).getNextValidTimeAfter(new Date(nowTime));
                    nowTime = nextValidTime.getTime();
                    if (nowTime <= endTime) {
                        FileStatisticsDto fileStatisticsDto = new FileStatisticsDto();
                        fileStatisticsDto.setStatus(4);
                        fileStatisticsDto.setId(fileMonitorDto.getId() + "_" + nowTime);
                        fileStatisticsDto.setTaskId(fileMonitorDto.getId());
                        fileStatisticsDto.setFilenameRegular(fileMonitorDto.getFilenameRegular());
                        fileStatisticsDto.setFileNum(fileMonitorDto.getFileNum());
                        fileStatisticsDto.setFileSize(fileMonitorDto.getFileSize());
                        fileStatisticsDto.setStartTimeL(nowTime);
                        fileStatisticsDto.setStartTimeS(new Date(nowTime));
                        fileStatisticsDtos.add(fileStatisticsDto);
                    }
                } catch (ParseException e) {
                    i++;
                    e.printStackTrace();
                    if (i > 1) {
                        break;
                    }
                }

            }
        }

        BulkRequest request = new BulkRequest();
        String indexName = IndexNameConstant.T_MT_FILE_STATISTICS;
        for (FileStatisticsDto fileStatisticsDto : fileStatisticsDtos) {
            Map<String, Object> source = new HashMap<>();
            boolean flag=this.findById(fileStatisticsDto.getId());
            if(flag){
                continue;
            }
            source.put("task_id", fileStatisticsDto.getTaskId());
            source.put("filename_regular", fileStatisticsDto.getFilenameRegular());
            source.put("file_num", fileStatisticsDto.getFileNum());
            source.put("file_size", fileStatisticsDto.getFileSize());
            source.put("start_time_l", fileStatisticsDto.getStartTimeL());
            source.put("start_time_s", fileStatisticsDto.getStartTimeS());
            source.put("real_file_num", fileStatisticsDto.getRealFileNum());
            source.put("real_file_size", fileStatisticsDto.getRealFileSize());
            source.put("per_file_num", fileStatisticsDto.getPerFileNum());
            source.put("per_file_size", fileStatisticsDto.getPerFileSize());
            source.put("status", fileStatisticsDto.getStatus());
            source.put("@timestamp", new Date());
            IndexRequest indexRequest = new ElasticSearch7InsertRequest(indexName, fileStatisticsDto.getId()).source(source);
            request.add(indexRequest);
        }
        if (request.requests().size() > 0) {
        /*    try {
                boolean flag = elasticSearch7Client.isExistsIndex(indexName);
                if (!flag) {
                    Map<String, Object> taskId = new HashMap<>();
                    taskId.put("type", "keyword");
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("task_id", taskId);
                    Map<String, Object> mapping = new HashMap<>();
                    mapping.put("properties", properties);
                    elasticSearch7Client.createIndex(indexName, new HashMap<>(), mapping);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            elasticSearch7Client.synchronousBulk(request);
        }
    }
    public boolean findById(String id){
        try {
            GetResponse response = elasticSearch7Client.get(IndexNameConstant.T_MT_FILE_STATISTICS,id);
            return response.isExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
