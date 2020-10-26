package com.piesat.skywalking.handler;

import cn.hutool.core.date.DateUtil;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.folder.FileSmaService;
import com.piesat.skywalking.util.HtFileUtil;
import com.piesat.util.CronUtil;
import com.piesat.util.DateExpressionEngine;
import com.piesat.util.ResultT;
import com.piesat.util.StringUtil;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFilenameFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : FileGuardHandler
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-23 19:30
 */
@Slf4j
@Service("fileMonitorHandler")
public class FileGuardHandler implements BaseHandler {

    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private FileSmaService fileSmaService;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        FileMonitorDto monitor= (FileMonitorDto) jobContext.getHtJobInfoDto();
        List<SmbFile> files=new ArrayList<>();
        fileSmaService.singleFile(monitor,files,resultT);
    }





}

