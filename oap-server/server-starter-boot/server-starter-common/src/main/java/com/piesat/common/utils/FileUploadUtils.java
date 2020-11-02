package com.piesat.common.utils;

import com.piesat.util.ResultT;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-03-20 11:09
 **/
@Service
public class FileUploadUtils {
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;
    @Autowired
    private ConfigurableBeanFactory beanFactory;
    @Value("${fileUpload.savaPath:/zzj/data/upload}")
    private String savePath;
    @Value("${fileUpload.httpPath:/upload}")
    private String httpPath;

    private static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    public static String renameToUUID(String fileName) {
        return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static final String extractFilename(MultipartFile file) {
        String type = file.getContentType();
        String fileName = type.substring(type.lastIndexOf("/"));
        fileName = renameToUUID(fileName.replace("/", "."));
        return fileName;
    }

    public static void uploadFileExl(HSSFWorkbook wb, String filePath) {
        try {
            File dirPath = new File(filePath);
            if (!dirPath.getParentFile().exists()) {
                dirPath.getParentFile().mkdirs();
            }

            // 新建一输出流
            FileOutputStream fout = new FileOutputStream(filePath);
            // 存盘
            wb.write(fout);
            // 清空缓冲区
            fout.flush();
            // 结束关闭
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String upload(String baseDir, MultipartFile file, ResultT<String> resultT) {
        baseDir = savePath + File.separator + baseDir;
        String fileName = extractFilename(file);

        String pathFileName = null;
        try {
            File desc = getAbsoluteFile(baseDir, fileName);
            file.transferTo(desc);
            pathFileName = getPathFileName(baseDir, fileName);
            return pathFileName;

        } catch (IOException e) {
            resultT.setErrorMessage("上传图片异常，请联系管理员");
            return "";
        }

    }

    private String getPathFileName(String uploadDir, String fileName) throws IOException {
        int dirLastIndex = savePath.length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        String pathFileName = httpPath + "/" + currentDir + "/" + fileName;
        return pathFileName;
    }
}

