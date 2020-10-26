package com.piesat.skywalking.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFilenameFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtFileUtil extends FileUtil {
    public static void loopFiles(File file, List<File> fileList, FilenameFilter fileFilter){
        if(file.isDirectory()){
            File[] files = file.listFiles(fileFilter);
            if (null!=files&&ArrayUtil.isNotEmpty(files)) {
                for (File tmp : files) {
                    loopFiles(tmp,fileList,fileFilter);
                }
            }
        }else {
            if(file.isFile()){
                fileList.add(file);
            }
        }

    }

    public static void loopFiles(SmbFile file,List<SmbFile> fileList,SmbFilenameFilter fileFilter){
        try {
            if(file.isDirectory()){
                SmbFile[] files = file.listFiles(fileFilter);
                if (null!=files&&ArrayUtil.isNotEmpty(files)) {
                    for (SmbFile tmp : files) {
                       loopFiles(tmp,fileList,fileFilter);
                    }
                }
            }else {
                if(file.isFile()){
                    fileList.add(file);
                }
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }

    }
}
