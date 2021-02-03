package com.piesat.skywalking.schedule.service.folder;

import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.schedule.service.alarm.AlarmFileService;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @ClassName : FileLocalService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 17:10
 */
@Service
public class FileLocalService extends FileBaseService {
    @Autowired
    private AlarmFileService alarmFileService;


    @Override
    public void singleFile(FileMonitorDto monitor, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        FileMonitorLogDto fileMonitorLogDto = this.insertLog(monitor);
        fileMonitorLogDto.setDdataTime(DdataTimeUtil.repalceRegx(monitor.getFilenameRegular(),monitor.getTriggerLastTime()));
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultT.setSuccessMessage("资料时次为:"+simpleDateFormat.format(fileMonitorLogDto.getDdataTime()));
        long starTime = System.currentTimeMillis();
        String remotePath="";
        try {
            if (monitor.getFileNum() == 1) {
                String folderRegular = DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
                fileMonitorLogDto.setRemotePath(folderRegular);
                String filenameRegular = DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
                remotePath=folderRegular+"/"+filenameRegular;
                File file = new File(folderRegular, filenameRegular);
                if (file.exists() && file.isFile() && file.length() > 0) {
                    this.putFile(file, fileMonitorLogDto, fileList, resultT);
                    fileMonitorLogDto.setFolderRegular(folderRegular);
                    fileMonitorLogDto.setFilenameRegular(filenameRegular);
                }

            }
        } catch (Exception e) {
            //resultT.setSuccessMessage(OwnException.get(e));
        }
        if(fileList.size()==0){
            resultT.setSuccessMessage("检索到文件失败:"+remotePath+"进行目录扫描匹配");
        }

        try {
            if(fileList.size()==0){
                this.multipleFiles(fileMonitorLogDto, fileList, resultT);

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
            this.updateLog(fileMonitorLogDto);
        }
    }

    public void multipleFiles(FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, ResultT<String> resultT) {

        try {
            File file = new File(fileMonitorLogDto.getFolderRegular());
            fileMonitorLogDto.setRemotePath(fileMonitorLogDto.getFolderRegular());
            FileFilter fileFilter = this.filterFile(fileMonitorLogDto, fileList, resultT);
            if (!resultT.isSuccess()) {
                return;
            }
            HtFileUtil.loopFiles(file, fileFilter);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }finally {
            if(fileList.size()==0){
                resultT.setErrorMessage("扫描文件夹:"+fileMonitorLogDto.getFolderRegular()+"未扫描到文件" );
            }else {
                resultT.setSuccessMessage("扫描文件夹:"+fileMonitorLogDto.getFolderRegular()+"检索到文件" );
                for(int i=0;i<fileList.size();i++){
                    resultT.setSuccessMessage(String.valueOf(fileList.get(i).get("full_path")));
                }
            }
        }
    }

    public FileFilter filterFile(FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        String expression = this.repalceRegx(fileMonitorLogDto, resultT);
        if (!resultT.isSuccess()) {
            return null;
        }
        FileFilter fileFilter = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            long endTime= fileMonitorLogDto.getTriggerTime();
            long beginTime=CronUtil.calculateLastTime(fileMonitorLogDto.getJobCron(), endTime);
            if(StringUtil.isNotEmpty(fileMonitorLogDto.getAllExpression())){
                beginTime =format.parse(DateExpressionEngine.formatDateExpression(fileMonitorLogDto.getAllExpression(), beginTime)).getTime();
                endTime = format.parse(DateExpressionEngine.formatDateExpression(fileMonitorLogDto.getAllExpression(),fileMonitorLogDto.getTriggerTime())).getTime();
            }
            List<String> fullpaths = this.findExist(fileMonitorLogDto.getTaskId(), fileMonitorLogDto.getTriggerTime());
            long finalBeginTime = beginTime;
            long finalEndTime = endTime;
            fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (fullpaths.contains(file.getPath())) {
                        return false;
                    }
                    if (StringUtil.isEmpty(fileMonitorLogDto.getFilenameRegular())) {
                        return true;
                    }
                    if (file.isDirectory()) {
                        return true;
                    }
                    long size = file.length();
                    if (size == 0) {
                        return false;
                    }
                    boolean isMatch = Pattern.matches(fileMonitorLogDto.getFilenameRegular(), file.getName());
                    if (!isMatch) {
                        return false;
                    }
                    if (StringUtil.isEmpty(expression)) {
                        return true;
                    }
                    long createTime = HtFileUtil.getCreateTime(file.toPath());
                    long lastModified = file.lastModified();
                    if (1 == fileMonitorLogDto.getIsUt()) {
                        createTime = createTime - 1000 * 3600 * 8;
                        lastModified = lastModified - 1000 * 3600 * 8;
                    }
                    long ddataTime = getDataTime(createTime, file.getName(), expression, resultT);
                    if (ddataTime < finalBeginTime || ddataTime >= finalEndTime) {
                        return false;
                    }
                    String fullpath = file.getPath();
                    Map<String, Object> source = new HashMap<>();
                    source.put("last_modified_time", new Date(lastModified));
                    source.put("create_time", new Date(createTime));
                    source.put("d_datetime", new Date(ddataTime));
                    source.put("full_path", fullpath);
                    source.put("parent_path", file.getParentFile().getPath());
                    source.put("file_name", file.getName());
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

    public void putFile(File file, FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        String expression = this.repalceRegx(fileMonitorLogDto, resultT);
        long createTime = HtFileUtil.getCreateTime(file.toPath());
        long lastModified = file.lastModified();
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
        source.put("parent_path", file.getParentFile().getPath());
        source.put("file_name", file.getName());
        source.put("file_bytes", file.length());
        fileList.add(source);
    }
}

