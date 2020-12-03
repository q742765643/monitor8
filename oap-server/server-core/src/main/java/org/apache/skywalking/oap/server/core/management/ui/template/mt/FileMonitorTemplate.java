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
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.FILE_MONITOR_TEMPLATE;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = FILE_MONITOR_TEMPLATE, name = "FILEMONITORTemplate")
@Stream(name = FileMonitorTemplate.INDEX_NAME, scopeId = FILE_MONITOR_TEMPLATE, builder = FileMonitorTemplate.Builder.class, processor = ManagementStreamProcessor.class)
public class FileMonitorTemplate extends ManagementData {
    public static final String INDEX_NAME = "t_mt_file_monitor";

    @Column(columnName = "@timestamp")
    private Date timestamp;

    @Column(columnName = "last_modified_time")
    private Date lastModifiedTime;

    @Column(columnName = "create_time")
    private Date createTime;

    @Column(columnName = "d_datetime")
    private Date ddatetime;

    @Column(columnName = "full_path")
    private String fullPath;

    @Column(columnName = "parent_path")
    private String parentPath;

    @Column(columnName = "file_name")
    private String file_name;

    @Column(columnName = "file_bytes")
    private long fileBytes;

    @Column(columnName = "ip")
    private String ip;

    @Column(columnName = "time_level")
    private long timeLevel;

    @Column(columnName = "task_id")
    private String taskId;

    @Column(columnName = "ontime")
    private long ontime;

    @Column(columnName = "no_ontime")
    private long no_ontime;

    @Override
    public String id() {
        return "";
    }
    public static class Builder implements StorageBuilder<FileMonitorTemplate> {

        @Override
        public FileMonitorTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(FileMonitorTemplate storageData) {
            return null;
        }
    }
}

