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
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.MEDIA_OVERVIEW_TEMPLATE;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = MEDIA_OVERVIEW_TEMPLATE, name = "MEDIAOVERVIEWTemplate")
@Stream(name = MediaOverviewTemplate.INDEX_NAME, scopeId = MEDIA_OVERVIEW_TEMPLATE, builder = MediaOverviewTemplate.Builder.class, processor = ManagementStreamProcessor.class)
public class MediaOverviewTemplate extends ManagementData {
    public static final String INDEX_NAME = "t_mt_media_overview";

    @Column(columnName = "@timestamp")
    private Date timestamp;

    @Column(columnName = "avg.cpu.pct")
    private float avgCpuPct;

    @Column(columnName = "avg.memory.pct")
    private float avgMemoryPct;

    @Column(columnName = "cpu.cores")
    private long  cpuCores;

    @Column(columnName = "cpu.use")
    private float  cpuUse;

    @Column(columnName = "device_type")
    private long  deviceType;

    @Column(columnName = "filesystem.pct")
    private float filesystemPct;

    @Column(columnName = "filesystem.size")
    private long filesystemSize;

    @Column(columnName = "filesystem.use.size")
    private float filesystemUse;

    @Column(columnName = "ip")
    private String ip;

    @Column(columnName = "memory.total")
    private long memoryTotal;

    @Column(columnName = "memory.use")
    private float memoryUse;

    @Column(columnName = "online")
    private long online;

    @Column(columnName = "process.size")
    private long processSize;

    @Override
    public String id() {
        return "";
    }
    public static class Builder implements StorageBuilder<MediaOverviewTemplate> {

        @Override
        public MediaOverviewTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(MediaOverviewTemplate storageData) {
            return null;
        }
    }
}

