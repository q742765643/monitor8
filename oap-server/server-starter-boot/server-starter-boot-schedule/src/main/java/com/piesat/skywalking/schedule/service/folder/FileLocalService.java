package com.piesat.skywalking.schedule.service.folder;

import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.*;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFilenameFilter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @ClassName : FileLocalService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 17:10
 */
@Service
public class FileLocalService extends  FileBaseService{

    public void singleFile(FileMonitorDto monitor, List<File> files, ResultT<String> resultT){
        FileMonitorLogDto fileMonitorLogDto=this.insertLog(monitor);
        try {
            if(monitor.getFileNum()==1) {
                String folderRegular= DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
                String filenameRegular = DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
                File file = new File(folderRegular, filenameRegular);
                if (file.exists()&&file.isFile()&&file.length()>0) {
                    files.add(file);
                    fileMonitorLogDto.setFolderRegular(folderRegular);
                    fileMonitorLogDto.setFilenameRegular(filenameRegular);
                    return;
                }

            }
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        this.multipleFiles(fileMonitorLogDto,monitor.getJobCron(),files,resultT);
        if(!resultT.isSuccess()){
            return;
        }
    }
    public void multipleFiles( FileMonitorLogDto fileMonitorLogDto,String cron,List<File> files, ResultT<String> resultT){
        FilenameFilter filenameFilter=this.filterFile(fileMonitorLogDto,cron,resultT);
        if(!resultT.isSuccess()){
            return;
        }
        try {
            File file = new File(fileMonitorLogDto.getFolderRegular());
            HtFileUtil.loopFiles(file,files,filenameFilter);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
    }
    public FilenameFilter filterFile(FileMonitorLogDto fileMonitorLogDto,String cron, ResultT<String> resultT){
        String expression=this.repalceRegx(fileMonitorLogDto,resultT);
        if(!resultT.isSuccess()){
            return null;
        }
        long endTime=fileMonitorLogDto.getTriggerTime();
        long beginTime= CronUtil.calculateLastTime(cron,endTime);
        FilenameFilter fileFilter=new FilenameFilter() {

            @Override
            public boolean accept(File file, String s) {
                if(StringUtil.isEmpty(fileMonitorLogDto.getFilenameRegular())){
                    return true;
                }
                if(file.isDirectory()){
                    return true;
                }
                if(file.length()==0){
                    return false;
                }
                boolean isMatch= Pattern.matches(fileMonitorLogDto.getFilenameRegular(), s);
                if(!isMatch){
                    return false;
                }
                if(StringUtil.isEmpty(expression)){
                    return true;
                }
                long ddataTime=getDataTime(file.lastModified(),s,expression,resultT);
                if(ddataTime<=beginTime||ddataTime>endTime){
                    return false;
                }
                return true;
            }
        };
        return fileFilter;
    }

}

