package com.piesat.skywalking.schedule.service.folder;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.folder.DirectoryAccountService;
import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.schedule.service.alarm.AlarmFileService;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.*;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @ClassName : FileSmaService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 17:02
 */
@Slf4j
@Service
public class FileSmaService extends FileBaseService {
    @GrpcHthtClient
    private DirectoryAccountService directoryAccountService;
    @Autowired
    private AlarmFileService alarmFileService;

    @Override
    public void singleFile(FileMonitorDto monitor, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        FileMonitorLogDto fileMonitorLogDto = this.insertLog(monitor);
        //fileMonitorLogDto.setDdataTime(DdataTimeUtil.repalceRegx(monitor.getFilenameRegular(),monitor.getTriggerLastTime(),monitor.getIsUt()));
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(1==monitor.getIsUt()){
            resultT.setSuccessMessage("资料为北京时"+simpleDateFormat.format(fileMonitorLogDto.getDdataTime())+",实际文件名称中时间 -8");
        }else {
            resultT.setSuccessMessage("资料为北京时"+simpleDateFormat.format(fileMonitorLogDto.getDdataTime())+",实际文件名称中时间 -0");
        }
        long starTime = System.currentTimeMillis();
        DirectoryAccountDto directoryAccountDto = directoryAccountService.findById(monitor.getAcountId());
        if (monitor.getFileNum() == 1) {
            String folderRegular = DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
            String filenameRegular = DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,"samba","samba");
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, directoryAccountDto.getUser(), directoryAccountDto.getPass());
            String remotePath = "smb://" + directoryAccountDto.getIp() + folderRegular + "/" + filenameRegular;
            fileMonitorLogDto.setRemotePath(remotePath);
            try {
                SmbFile file = new SmbFile(remotePath, auth);
                if (file.exists() && file.isFile() && file.length() > 0) {
                    this.putFile(file, fileMonitorLogDto, fileList, resultT);
                    fileMonitorLogDto.setFolderRegular(folderRegular);
                    fileMonitorLogDto.setFilenameRegular(filenameRegular);
                    resultT.setSuccessMessage("检索到文件成功:"+remotePath);
                }
            } catch (Exception e) {
            }
            if(fileList.size()==0){
                resultT.setSuccessMessage("检索到文件失败:"+remotePath+"进行目录扫描匹配");
            }

        }
        try {
            if(fileList.size()==0){
                this.multipleFiles(fileMonitorLogDto, fileList, directoryAccountDto, resultT);
            }
            if (!resultT.isSuccess()) {
                return;
            }
            this.insertFilePath(fileList, fileMonitorLogDto, resultT);
            if (!resultT.isSuccess()) {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            alarmFileService.execute(fileMonitorLogDto);
            this.updateFileStatistics(fileMonitorLogDto, resultT);
            if (!resultT.isSuccess()) {
                fileMonitorLogDto.setHandleCode(2);
            } else {
                fileMonitorLogDto.setHandleCode(1);
            }
            fileMonitorLogDto.setHandleMsg(resultT.getProcessMsg().toString());
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - starTime) / 1000;
            fileMonitorLogDto.setElapsedTime(elapsedTime);
            fileMonitorLogDto.setRealFileSize(new BigDecimal(fileMonitorLogDto.getRealFileSize()).divide(new BigDecimal(1024), 0, BigDecimal.ROUND_UP).longValue());
            log.info("资料{}更新开始",fileMonitorLogDto.getTaskName());
            this.updateLog(fileMonitorLogDto);
            log.info("资料{}更新结束",fileMonitorLogDto.getTaskName());


        }

    }

    public void multipleFiles(FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, DirectoryAccountDto directoryAccountDto, ResultT<String> resultT) {
        String remotePath="";
        try {
            remotePath = "smb://" + directoryAccountDto.getIp() + fileMonitorLogDto.getFolderRegular() + "/";
            fileMonitorLogDto.setRemotePath(remotePath);
            SmbFileFilter smbFileFilter = this.filterFile(fileMonitorLogDto, fileList, resultT);
            if (!resultT.isSuccess()) {
                return;
            }
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, directoryAccountDto.getUser(), directoryAccountDto.getPass());
            SmbFile file = new SmbFile(remotePath, auth);
            HtFileUtil.loopFiles(file, smbFileFilter);
        } catch (Exception e) {
            //resultT.setErrorMessage(OwnException.get(e));
        }finally {
            if(fileList.size()==0){
                resultT.setErrorMessage("扫描文件夹:"+remotePath+"未扫描到文件" );
            }else {
                resultT.setSuccessMessage("扫描文件夹:"+remotePath+"检索到文件" );
                for(int i=0;i<fileList.size();i++){
                    resultT.setSuccessMessage(String.valueOf(fileList.get(i).get("full_path")));
                }
            }
        }

    }

    public SmbFileFilter filterFile(FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        SmbFileFilter fileFilter = null;
        try {
            String expression = this.repalceRegx(fileMonitorLogDto, resultT);
            fileMonitorLogDto.setExpression(expression);
            if (!resultT.isSuccess()) {
                return null;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
         /*   if("测试".equals(fileMonitorLogDto.getTaskName())){
                log.info("1");
                fileMonitorLogDto.setTriggerTime(1612317600000l);
            }*/
            long endTime= fileMonitorLogDto.getTriggerTime();
            long beginTime=CronUtil.calculateLastTime(fileMonitorLogDto.getJobCron(), endTime);
            if(StringUtil.isNotEmpty(fileMonitorLogDto.getAllExpression())){
                beginTime =format.parse(DateExpressionEngine.formatDateExpression(fileMonitorLogDto.getAllExpression(),beginTime)).getTime();
                endTime = format.parse(DateExpressionEngine.formatDateExpression(fileMonitorLogDto.getAllExpression(),fileMonitorLogDto.getTriggerTime())).getTime();
            }
            List<String> fullpaths = this.findExist(fileMonitorLogDto.getTaskId(), fileMonitorLogDto.getTriggerTime());
            long finalBeginTime = beginTime;
            long finalEndTime = endTime;
            fileFilter = new SmbFileFilter() {
                @Override
                public boolean accept(SmbFile smbFile) throws SmbException {
                    if (fullpaths.contains(smbFile.getPath())) {
                        return false;
                    }
                    if (StringUtil.isEmpty(fileMonitorLogDto.getFilenameRegular())) {
                        return true;
                    }
                    if (smbFile.isDirectory()) {
                        return true;
                    }
                    long size = smbFile.length();
                    if (size == 0) {
                        return false;
                    }
                    boolean isMatch = Pattern.matches(fileMonitorLogDto.getFilenameRegular(), smbFile.getName());
                    if (!isMatch) {
                        return false;
                    }
                    if (StringUtil.isEmpty(expression)) {
                        return true;
                    }
                    long createTime = smbFile.createTime();
                    long lastModified = smbFile.getLastModified();
                    if (1 == fileMonitorLogDto.getIsUt()) {
                        createTime = createTime - 1000 * 3600 * 8;
                        lastModified = lastModified - 1000 * 3600 * 8;
                    }
                    long ddataTime = getDataTime(createTime, smbFile.getName(), expression, resultT);
                    if (!resultT.isSuccess()) {
                        return false;
                    }
                    if (ddataTime <= finalBeginTime || ddataTime > finalEndTime) {
                        return false;
                    }
                    String fullpath = smbFile.getPath();
                    Map<String, Object> source = new HashMap<>();
                    source.put("last_modified_time", new Date(lastModified));
                    source.put("create_time", new Date(createTime));
                    source.put("d_datetime", new Date(ddataTime));
                    source.put("full_path", fullpath);
                    source.put("parent_path", smbFile.getParent());
                    source.put("file_name", smbFile.getName());
                    source.put("file_bytes", size);
                    fileList.add(source);
                    return true;
                }
            };
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        return fileFilter;
    }


    public void putFile(SmbFile file, FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        try {
            String expression = this.repalceRegx(fileMonitorLogDto, resultT);
            long createTime = file.createTime();
            long lastModified = file.getLastModified();
            if (1 == fileMonitorLogDto.getIsUt()) {
                createTime = createTime - 1000 * 3600 * 8;
                lastModified = lastModified - 1000 * 3600 * 8;
            }
            long ddataTime = getDataTime(createTime, file.getName(), expression, resultT);
            String fullpath = file.getPath();
            Map<String, Object> source = new HashMap<>();
            source.put("last_modified_time", new Date(lastModified));
            source.put("create_time", new Date(createTime));
            source.put("d_datetime", new Date(ddataTime));
            source.put("full_path", fullpath);
            source.put("parent_path", file.getParent());
            source.put("file_name", file.getName());
            source.put("file_bytes", file.length());
            fileList.add(source);
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }
}

