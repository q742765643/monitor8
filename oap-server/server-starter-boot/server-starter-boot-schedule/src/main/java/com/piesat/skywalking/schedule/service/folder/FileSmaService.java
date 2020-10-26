package com.piesat.skywalking.schedule.service.folder;

import cn.hutool.core.date.DateUtil;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.*;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFilenameFilter;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : FileSmaService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 17:02
 */
@Service
public class FileSmaService extends FileBaseService{
    public void singleFile(FileMonitorDto monitor, List<SmbFile> files, ResultT<String> resultT){
        FileMonitorLogDto fileMonitorLogDto=this.insertLog(monitor);
        long starTime=System.currentTimeMillis();
        if(monitor.getFileNum()==1) {
            String folderRegular= DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
            String filenameRegular = DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,"samba","samba");
            String remotePath="smb://10.1.100.8"+folderRegular+"/"+filenameRegular;
            try {
                SmbFile file = new SmbFile(remotePath, auth);
                if (file.exists()&&file.isFile()&&file.length()>0) {
                    files.add(file);
                    fileMonitorLogDto.setFolderRegular(folderRegular);
                    fileMonitorLogDto.setFilenameRegular(filenameRegular);
                    return;
                }
            } catch (Exception e) {
                resultT.setErrorMessage(OwnException.get(e));
            }

        }
        try {
            this.multipleFiles(fileMonitorLogDto,monitor.getJobCron(),files,resultT);
            if(!resultT.isSuccess()){
                return;
            }
            this.insertFilePath(files,fileMonitorLogDto,resultT);
            if(!resultT.isSuccess()){
                return;
            }
            this.updateFileStatistics(fileMonitorLogDto,resultT);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(!resultT.isSuccess()){
                fileMonitorLogDto.setHandleCode(2);
            }else {
                fileMonitorLogDto.setHandleCode(1);
            }
            fileMonitorLogDto.setHandleMsg(resultT.getProcessMsg().toString());
            long endTime=System.currentTimeMillis();
            long elapsedTime=(endTime-starTime)/1000;
            fileMonitorLogDto.setElapsedTime(elapsedTime);
            this.updateLog(fileMonitorLogDto);
        }

    }
    public void multipleFiles(FileMonitorLogDto fileMonitorLogDto,String cron,List<SmbFile> files, ResultT<String> resultT){
        SmbFilenameFilter filenameFilter=this.filterFile(fileMonitorLogDto,cron,resultT);
        if(!resultT.isSuccess()){
            return;
        }
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,"samba","samba");
        try {
            String remotePath="smb://10.1.100.8"+fileMonitorLogDto.getFolderRegular()+"/";
            SmbFile file = new SmbFile(remotePath, auth);
            HtFileUtil.loopFiles(file,files,filenameFilter);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }

    }

    public  SmbFilenameFilter filterFile(FileMonitorLogDto fileMonitorLogDto,String cron,ResultT<String> resultT){
        SmbFilenameFilter fileFilter= null;
        try {
            String expression=this.repalceRegx(fileMonitorLogDto,resultT);
            fileMonitorLogDto.setExpression(expression);
            if(!resultT.isSuccess()){
                return null;
            }
            long endTime=fileMonitorLogDto.getTriggerTime();
            long beginTime= CronUtil.calculateLastTime(cron,endTime);
            fileFilter = new SmbFilenameFilter() {
                @Override
                public boolean accept(SmbFile smbFile, String s) throws SmbException {
                    if(StringUtil.isEmpty(fileMonitorLogDto.getFilenameRegular())){
                        return true;
                    }
                    if(smbFile.isDirectory()){
                        return true;
                    }
                    if(smbFile.length()==0){
                        return false;
                    }
                    boolean isMatch= Pattern.matches(fileMonitorLogDto.getFilenameRegular(), s);
                    if(!isMatch){
                        return false;
                    }
                    if(StringUtil.isEmpty(expression)){
                        return true;
                    }
                    long ddataTime=getDataTime(smbFile.createTime(),s,expression,resultT);
                    if(!resultT.isSuccess()){
                        return false;
                    }
                    if(ddataTime<=beginTime||ddataTime>endTime){
                        return false;
                    }
                    return true;
                }
            };
        } catch (Exception e) {
           resultT.setErrorMessage(OwnException.get(e));
        }
        return fileFilter;
    }
    public void  insertFilePath(List<SmbFile> files, FileMonitorLogDto fileMonitorLogDto,ResultT<String> resultT){

        String indexName= IndexNameConstant.T_MT_FILE_MONITOR;
        List<String> fullpaths=this.findExist(fileMonitorLogDto.getTaskId(),fileMonitorLogDto.getTriggerTime());
        BulkRequest request = new BulkRequest();
        long fileNum=0;
        long fileSize=0;
        String ip= NetUtils.getLocalHost();
        try {
            for(SmbFile file:files){
                String fullpath=file.getPath();
                fileNum=fileNum+1;
                long size= 0;
                size =this.bytes2kb(file.length());
                fileSize=fileSize+size;
                if(fullpaths.contains(fullpath)){
                    continue;
                }

                Map<String,Object> source=new HashMap<>();
                source.put("last_modified_time",new Date(file.getLastModified()));
                source.put("create_time",new Date(file.createTime()));
                long time=getDataTime(file.createTime(),file.getName(), fileMonitorLogDto.getExpression(),resultT) ;
                if(!resultT.isSuccess()){
                    return;
                }
                source.put("d_datetime",new Date(time));
                source.put("full_path",fullpath);
                source.put("parent_path",file.getParent());
                source.put("file_name",file.getName());
                source.put("ip",ip);
                source.put("time_level",fileMonitorLogDto.getTriggerTime());

                source.put("file_bytes",size);
                source.put("task_id",fileMonitorLogDto.getTaskId());
                source.put("is_ontime",0l);
                source.put("timestamp",new Date());
                IndexRequest indexRequest = new ElasticSearch7InsertRequest(indexName, fullpath).source(source);
                request.add(indexRequest);
            }
            if(request.requests().size()>0){
                elasticSearch7Client.bulkEx(request);
            }
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        fileMonitorLogDto.setRealFileNum(fileNum);
        fileMonitorLogDto.setRealFileSize(fileSize);
    }


}

