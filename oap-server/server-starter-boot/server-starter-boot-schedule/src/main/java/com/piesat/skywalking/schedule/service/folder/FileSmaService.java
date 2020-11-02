package com.piesat.skywalking.schedule.service.folder;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.folder.DirectoryAccountService;
import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.*;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileFilter;
import org.springframework.stereotype.Service;

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
@Service
public class FileSmaService extends FileBaseService {
    @GrpcHthtClient
    private DirectoryAccountService directoryAccountService;

    @Override
    public void singleFile(FileMonitorDto monitor, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        FileMonitorLogDto fileMonitorLogDto = this.insertLog(monitor);
        long starTime = System.currentTimeMillis();
        DirectoryAccountDto directoryAccountDto = directoryAccountService.findById(monitor.getAcountId());
        if (monitor.getFileNum() == 1) {
            String folderRegular = DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
            String filenameRegular = DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,"samba","samba");
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,directoryAccountDto.getUser(),directoryAccountDto.getPass());
            String remotePath="smb://"+directoryAccountDto.getIp()+folderRegular+"/"+filenameRegular;
            try {
                SmbFile file = new SmbFile(remotePath, auth);
                if (file.exists()&&file.isFile()&&file.length()>0) {
                    this.putFile(file,fileMonitorLogDto,fileList,resultT);
                    fileMonitorLogDto.setFolderRegular(folderRegular);
                    fileMonitorLogDto.setFilenameRegular(filenameRegular);
                    return;
                }
            } catch (Exception e) {
                resultT.setErrorMessage(OwnException.get(e));
            }

        }
        try {
            this.multipleFiles(fileMonitorLogDto,fileList,directoryAccountDto,resultT);
            if(!resultT.isSuccess()){
                return;
            }
            this.insertFilePath(fileList,fileMonitorLogDto,resultT);
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
    public void multipleFiles(FileMonitorLogDto fileMonitorLogDto,List<Map<String,Object>> fileList,DirectoryAccountDto directoryAccountDto, ResultT<String> resultT){
        SmbFileFilter smbFileFilter=this.filterFile(fileMonitorLogDto,fileList,resultT);
        if(!resultT.isSuccess()){
            return;
        }
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null,directoryAccountDto.getUser(),directoryAccountDto.getPass());
        try {
            String remotePath="smb://"+directoryAccountDto.getIp()+fileMonitorLogDto.getFolderRegular()+"/";
            SmbFile file = new SmbFile(remotePath, auth);
            HtFileUtil.loopFiles(file,smbFileFilter);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }

    }

    public  SmbFileFilter filterFile(FileMonitorLogDto fileMonitorLogDto,List<Map<String,Object>> fileList,ResultT<String> resultT){
        SmbFileFilter fileFilter= null;
        try {
            String expression=this.repalceRegx(fileMonitorLogDto,resultT);
            fileMonitorLogDto.setExpression(expression);
            if(!resultT.isSuccess()){
                return null;
            }
            long endTime=fileMonitorLogDto.getTriggerTime();
            long beginTime= CronUtil.calculateLastTime(fileMonitorLogDto.getJobCron(),endTime);
            List<String> fullpaths=this.findExist(fileMonitorLogDto.getTaskId(),fileMonitorLogDto.getTriggerTime());
            fileFilter = new SmbFileFilter() {
                @Override
                public boolean accept(SmbFile smbFile) throws SmbException {
                    if(fullpaths.contains(smbFile.getPath())){
                        return false;
                    }
                    if(StringUtil.isEmpty(fileMonitorLogDto.getFilenameRegular())){
                        return true;
                    }
                    if(smbFile.isDirectory()){
                        return true;
                    }
                    long size =smbFile.length();
                    if(size==0){
                        return false;
                    }
                    boolean isMatch= Pattern.matches(fileMonitorLogDto.getFilenameRegular(), smbFile.getName());
                    if(!isMatch){
                        return false;
                    }
                    if(StringUtil.isEmpty(expression)){
                        return true;
                    }
                    long createTime=smbFile.createTime();
                    long lastModified=smbFile.getLastModified();
                    if(1==fileMonitorLogDto.getIsUt()){
                        createTime=createTime-1000*3600*8;
                        lastModified=lastModified-1000*3600*8;
                    }
                    long ddataTime=getDataTime(createTime,smbFile.getName(),expression,resultT);
                    if(!resultT.isSuccess()){
                        return false;
                    }
                    if(ddataTime<=beginTime||ddataTime>endTime){
                        return false;
                    }
                    String fullpath=smbFile.getPath();
                    Map<String,Object> source=new HashMap<>();
                    source.put("last_modified_time",new Date(lastModified));
                    source.put("create_time",new Date(createTime));
                    source.put("d_datetime",new Date(ddataTime));
                    source.put("full_path",fullpath);
                    source.put("parent_path",smbFile.getParent());
                    source.put("file_name",smbFile.getName());
                    source.put("file_bytes",size);
                    fileList.add(source);
                    return true;
                }
            };
        } catch (Exception e) {
           resultT.setErrorMessage(OwnException.get(e));
        }
        return fileFilter;
    }


    public void putFile(SmbFile file,FileMonitorLogDto fileMonitorLogDto,List<Map<String,Object>> fileList, ResultT<String> resultT){
        try {
            String expression=this.repalceRegx(fileMonitorLogDto,resultT);
            long createTime= file.createTime();
            long lastModified=file.getLastModified();
            if(1==fileMonitorLogDto.getIsUt()){
                createTime=createTime-1000*3600*8;
                lastModified=lastModified-1000*3600*8;
            }
            long ddataTime=getDataTime(createTime,file.getName(),expression,resultT);
            String fullpath=file.getPath();
            Map<String,Object> source=new HashMap<>();
            source.put("last_modified_time",new Date(lastModified));
            source.put("create_time",new Date(createTime));
            source.put("d_datetime",new Date(ddataTime));
            source.put("full_path",fullpath);
            source.put("parent_path",file.getParent());
            source.put("file_name",file.getName());
            source.put("file_bytes",file.length());
            fileList.add(source);
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }
}

