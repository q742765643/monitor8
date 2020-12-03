/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.core.management.ui.template.mt;

import lombok.Getter;
import lombok.Setter;
import org.apache.skywalking.oap.server.core.analysis.Stream;
import org.apache.skywalking.oap.server.core.analysis.management.ManagementData;
import org.apache.skywalking.oap.server.core.analysis.worker.ManagementStreamProcessor;
import org.apache.skywalking.oap.server.core.source.ScopeDeclaration;
import org.apache.skywalking.oap.server.core.storage.StorageBuilder;
import org.apache.skywalking.oap.server.core.storage.annotation.Column;

import java.util.Date;
import java.util.Map;

import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.ALARM_TEMPLATE;
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.FILE_STATISTICS_TEMPLATE;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = FILE_STATISTICS_TEMPLATE, name = "FILESTATISTICSTemplate")
@Stream(name = FileStatisticsTemplate.INDEX_NAME, scopeId = FILE_STATISTICS_TEMPLATE, builder = FileStatisticsTemplate.Builder.class, processor = ManagementStreamProcessor.class)
public class FileStatisticsTemplate extends ManagementData {
    public static final String INDEX_NAME = "t_mt_file_statistics";

    @Column(columnName = "@timestamp")
    private Date timestamp;

    @Column(columnName = "end_time_a")
    private Date endTimeA;

    @Column(columnName = "file_num")
    private long fileNum;

    @Column(columnName = "file_size")
    private long fileSize;

    @Column(columnName = "filename_regular")
    private String filenameRegular;

    @Column(columnName = "late_num")
    private long lateNum;

    @Column(columnName = "level")
    private long level;

    @Column(columnName = "per_file_num")
    private float perFileNum;

    @Column(columnName = "per_file_size")
    private float perFileSize;

    @Column(columnName = "real_file_num")
    private long realFileNum;

    @Column(columnName = "real_file_size")
    private long realFileSize;

    @Column(columnName = "start_time_a")
    private Date startTimeA;

    @Column(columnName = "start_time_l")
    private Date startTimeL;

    @Column(columnName = "start_time_s")
    private Date startTimeS;

    @Column(columnName = "status")
    private long status;

    @Column(columnName = "task_id")
    private String taskId;

    @Column(columnName = "taskName")
    private String taskName;

    @Column(columnName = "timeliness_rate")
    private float timelinessRate;

    @Override
    public String id() {
        return "";
    }
    public static class Builder implements StorageBuilder<FileStatisticsTemplate> {

        @Override
        public FileStatisticsTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(FileStatisticsTemplate storageData) {
            return null;
        }
    }
}

