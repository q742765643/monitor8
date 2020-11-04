/*
package com.piesat.skywalking.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.api.alarm.AlarmConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.alarm.AlarmLogService;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("fileMonitorHandler11111")
public class FileMonitorHandler implements BaseHandler {
    private static final String REGEX = "(\\$\\{(.*?)\\})";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private AlarmLogService alarmLogService;
    @GrpcHthtClient
    private AlarmConfigService alarmConfigService;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        FileMonitorDto monitor= (FileMonitorDto) jobContext.getHtJobInfoDto();
        List<File> files=new ArrayList<>();
        if(monitor.getFileNum()<=1){
            String folderRegular=DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
            String filenameRegular=DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
            File file=new File(folderRegular,filenameRegular);
            if(file.exists()){
                files.add(file);
            }
        }
        String expression=this.repalceRegx(monitor);
        if(files.size()==0){
            files.addAll(this.filterFile(monitor,expression));
        }
        this.insertEs(files,monitor,expression);

    }

    public String repalceRegx(FileMonitorDto monitor){
        String folderRegular=DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
        String filenameRegular=monitor.getFilenameRegular();
        Matcher m = PATTERN.matcher(filenameRegular);
        String expression="";
        while (m.find()){
            expression = m.group(2);
            int start = m.start(1);
            int end = m.end(1);
            String replaceMent =expression.replaceAll("[ymdhsYMDHS]", "\\\\d");
            String expressionWrapper = monitor.getFilenameRegular().substring(start, end);
            filenameRegular=StringUtils.replace(filenameRegular, expressionWrapper, replaceMent);

        }
        filenameRegular=filenameRegular.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
        monitor.setFolderRegular(folderRegular);
        monitor.setFilenameRegular(filenameRegular);
        return expression;
    }

    public  List<File> filterFile(FileMonitorDto monitor,String expression){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long endTime=monitor.getTriggerLastTime();
        log.info("endTime is {}",format.format(endTime));
        long beginTime= CronUtil.calculateLastTime(monitor.getJobCron(),endTime);
        log.info("beginTime is {}",format.format(beginTime));
        FileFilter fileFilter=new FileFilter() {
            @Override
            public boolean accept(File pathname) {

                try {
                    if(StringUtil.isEmpty(monitor.getFilenameRegular())){
                        return true;
                    }
                    String fileName=FileUtil.getName(pathname);
                    boolean isMatch=Pattern.matches(monitor.getFilenameRegular(), fileName);
                    if(!isMatch){
                        return false;
                    }
                    long time=getDataTimeFromFileNameByPattern(fileName, expression) ;
                    if(time!=-1&&(time<=beginTime||time>endTime)){
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        };
        List<File> files= HtFileUtil.loopFiles(monitor.getFolderRegular(),fileFilter);
        return files;

    }

    public void  insertEs(List<File> files,FileMonitorDto monitor,String expression){

        String indexName=IndexNameUtil.getIndexName(IndexNameConstant.T_MT_FILE_MONITOR);
        BulkRequest request = new BulkRequest();
        long fileNum=0;
        long fileSize=0;
        String ip= NetUtils.getLocalHost();
        for(File file:files){
            String fullpath=file.getPath().replaceAll("\\\\","/");
            Map<String,Object> source=new HashMap<>();
            source.put("last_modified_time",new Date(file.lastModified()));
            long time=getDataTimeFromFileNameByPattern(file.getName(), expression) ;
            if(time!=-1){
                source.put("d_datetime",new Date(time));
            }else {
                source.put("d_datetime",new Date(file.lastModified()));
            }
            source.put("full_path",fullpath);
            source.put("parent_path",file.getParentFile().getPath().replaceAll("\\\\","/"));
            source.put("file_name",file.getName());
            source.put("ip",ip);
            if(file.isDirectory()){
                source.put("is_directory",1);
            }else {
                fileNum=fileNum+1;
                source.put("is_directory",0);
            }
            long size=HtFileUtil.size(file);
            fileSize=fileSize+size;
            source.put("file_bytes",size);
            IndexRequest indexRequest = new ElasticSearch7InsertRequest(indexName, fullpath).source(source);
            request.add(indexRequest);
        }
        if(request.requests().size()>0){
            elasticSearch7Client.synchronousBulk(request);
        }
        Integer currentStatus=3;
        if(monitor.getFileNum()>0){
            float reach=new BigDecimal(fileNum).divide(new BigDecimal(monitor.getFileNum()),4, BigDecimal.ROUND_HALF_UP).floatValue();
            if(reach<1){
                currentStatus=this.toAlarm(monitor,ip,reach);
            }

        }
        String statisticsIndexName= IndexNameUtil.getIndexName(IndexNameConstant.T_MT_FILE_STATISTICS,new Date(monitor.getTriggerLastTime()));
        try {
            GetResponse getResponse=elasticSearch7Client.get(statisticsIndexName,monitor.getId()+"_"+monitor.getTriggerLastTime());
            Map<String,Object> source=getResponse.getSourceAsMap();
            if(null!=source&&source.size()>0){
                source.put("real_file_num",fileNum);
                source.put("real_file_size",fileSize);
                source.put("per_file_num",new BigDecimal((float)fileNum/monitor.getFileNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                source.put("per_file_size",new BigDecimal((float)fileSize/monitor.getFileSize()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                source.put("status",currentStatus);
                source.put("start_time_a",new Date());
                source.put("end_time_a",new Date());
                elasticSearch7Client.forceInsert(statisticsIndexName,monitor.getId()+"_"+monitor.getTriggerLastTime(),source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("filenum为{}",fileNum);
        log.info("filesize为{}",fileSize);
    }

    public long getDataTimeFromFileNameByPattern(String fileName,String dataFilePattern){
        if(dataFilePattern != null && !"".equals(dataFilePattern)){
            Pattern p = Pattern.compile(getRegFromDatePattern(dataFilePattern));
            Matcher m = p.matcher(fileName);
            if(m.find()){
                String timeStr = m.group(0);
                SimpleDateFormat format=new SimpleDateFormat(dataFilePattern);
                return DateUtil.parse(timeStr,format).getTime();
            }
        }
        return -1;
    }

    private  String getRegFromDatePattern(String datePattern){
        String reg = new String(datePattern);
        reg = reg.replaceAll("[ymdhsYMDHS]", "\\\\d");
        return reg;
    }

    public Integer toAlarm(FileMonitorDto monitor,String ip,float reach){
        AlarmLogDto alarmLogDto=new AlarmLogDto();
        String message="文件到达率达达阈值条件当前为"+reach*100+"%";
        alarmLogDto.setMessage(message);
        alarmLogDto.setIp(ip);
        alarmLogDto.setDeviceName(monitor.getFolderRegular()+"文件到达率");
        alarmLogDto.setDeviceType(3);
        alarmLogDto.setMonitorType(MonitorTypeEnum.FILE_REACH.getValue());
        AlarmConfigDto alarmConfigDto=alarmConfigService.findById(MonitorTypeEnum.FILE_REACH.name());
        if(alarmConfigDto!=null){
           return alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto,reach);
        }
        return 3;
    }

}
*/
