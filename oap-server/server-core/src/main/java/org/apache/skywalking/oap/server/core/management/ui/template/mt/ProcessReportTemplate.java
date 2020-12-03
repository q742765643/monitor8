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
import java.util.HashMap;
import java.util.Map;

import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.MEDIA_REPORT_TEMPLATE;
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.PROCESS_REPORT_TEMPLATE;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = PROCESS_REPORT_TEMPLATE, name = "PROCESSREPORTTemplate")
@Stream(name = ProcessReportTemplate.INDEX_NAME, scopeId = PROCESS_REPORT_TEMPLATE, builder = ProcessReportTemplate.Builder.class, processor = ManagementStreamProcessor.class)
public class ProcessReportTemplate extends ManagementData {
    public static final String INDEX_NAME = "t_mt_process_report";

    @Column(columnName = "@timestamp")
    private Date timestamp;

    @Column(columnName = "alarm.num")
    private long alarmNum;

    @Column(columnName = "avg.cpu.pct")
    private float avgCpuPct;

    @Column(columnName = "avg.memory.pct")
    private float avgMemoryPct;

    @Column(columnName = "ip")
    private String ip;

    @Column(columnName = "related_id")
    private String relatedId;

    @Column(columnName = "max.cpu.pct")
    private float maxCpuPct;

    @Column(columnName = "max.memory.pct")
    private float maxMemoryPct;

    @Column(columnName = "max.uptime.pct")
    private float maxUptimePct;

    @Column(columnName = "down.num")
    private long downNum;

    @Column(columnName = "down.time")
    private long downTime;

    @Column(columnName = "exeption.num")
    private long exeptionNum;

    @Column(columnName = "exeption.time")
    private long exeptionTime;

    @Column(columnName = "processName")
    private String processName;
    @Override
    public String id() {
        return "";
    }
    public static class Builder implements StorageBuilder<ProcessReportTemplate> {

        @Override
        public ProcessReportTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(ProcessReportTemplate storageData) {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("@timestamp", storageData.getTimestamp());
            map.put("alarm.num", storageData.getAlarmNum());
            map.put("avg.cpu.pct", storageData.getAvgCpuPct());
            map.put("avg.memory.pct", storageData.getAvgMemoryPct());
            map.put("ip", storageData.getIp());
            map.put("related_id", storageData.getRelatedId());
            map.put("max.cpu.pct", storageData.getMaxCpuPct());
            map.put("max.memory.pct", storageData.getMaxMemoryPct());
            map.put("max.uptime.pct", storageData.getMaxUptimePct());
            map.put("down.num", storageData.getDownNum());
            map.put("down.time", storageData.getDownTime());
            map.put("exeption.num", storageData.getExeptionNum());
            map.put("exeption.time", storageData.getExeptionTime());
            map.put("processName", storageData.getProcessName());
            return map;
        }
    }
}

