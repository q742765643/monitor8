package com.piesat.skywalking.schedule.service.folder;

import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
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
    long starTime = System.currentTimeMillis();

    @Override
    public void singleFile(FileMonitorDto monitor, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        FileMonitorLogDto fileMonitorLogDto = this.insertLog(monitor);
        try {
            if (monitor.getFileNum() == 1) {
                String folderRegular = DateExpressionEngine.formatDateExpression(monitor.getFolderRegular(), monitor.getTriggerLastTime());
                String filenameRegular = DateExpressionEngine.formatDateExpression(monitor.getFilenameRegular(), monitor.getTriggerLastTime());
                File file = new File(folderRegular, filenameRegular);
                if (file.exists() && file.isFile() && file.length() > 0) {
                    this.putFile(file, fileMonitorLogDto, fileList, resultT);
                    fileMonitorLogDto.setFolderRegular(folderRegular);
                    fileMonitorLogDto.setFilenameRegular(filenameRegular);
                    return;
                }

            }
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
        try {
            this.multipleFiles(fileMonitorLogDto, fileList, resultT);
            if (!resultT.isSuccess()) {
                return;
            }
            this.insertFilePath(fileList, fileMonitorLogDto, resultT);
            if (!resultT.isSuccess()) {
                return;
            }
            this.updateFileStatistics(fileMonitorLogDto, resultT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        FileFilter fileFilter = this.filterFile(fileMonitorLogDto, fileList, resultT);
        if (!resultT.isSuccess()) {
            return;
        }
        try {
            File file = new File(fileMonitorLogDto.getFolderRegular());
            HtFileUtil.loopFiles(file, fileFilter);
        } catch (Exception e) {
            resultT.setErrorMessage(OwnException.get(e));
        }
    }

    public FileFilter filterFile(FileMonitorLogDto fileMonitorLogDto, List<Map<String, Object>> fileList, ResultT<String> resultT) {
        String expression = this.repalceRegx(fileMonitorLogDto, resultT);
        if (!resultT.isSuccess()) {
            return null;
        }
        long endTime = fileMonitorLogDto.getTriggerTime();
        long beginTime = CronUtil.calculateLastTime(fileMonitorLogDto.getJobCron(), endTime);
        List<String> fullpaths = this.findExist(fileMonitorLogDto.getTaskId(), fileMonitorLogDto.getTriggerTime());
        FileFilter fileFilter = null;
        try {
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
                    if (ddataTime <= beginTime || ddataTime > endTime) {
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

