package com.piesat.skywalking.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class HtFileUtil extends FileUtil {
    public static void loopFilesLocal(File file, FileFilter fileFilter) {
        if (file.isDirectory()) {
            File[] files = file.listFiles(fileFilter);
            if (null != files && ArrayUtil.isNotEmpty(files)) {
                for (File tmp : files) {
                    loopFiles(tmp, fileFilter);
                }
            }
        } else {
        }

    }

    public static void loopFiles(SmbFile file, SmbFileFilter fileFilter) {
        try {
            if (file.isDirectory()) {
                SmbFile[] files = file.listFiles(fileFilter);
                if (null != files && ArrayUtil.isNotEmpty(files)) {
                    for (SmbFile tmp : files) {
                        loopFiles(tmp, fileFilter);
                    }
                }
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }

    }

    public static Long getCreateTime(Path path) {

        BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class,
                LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr;
        try {
            attr = basicview.readAttributes();
            return attr.creationTime().toMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }
}
