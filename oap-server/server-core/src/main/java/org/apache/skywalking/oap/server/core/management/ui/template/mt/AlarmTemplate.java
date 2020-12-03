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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.skywalking.oap.server.core.analysis.Stream;
import org.apache.skywalking.oap.server.core.analysis.management.ManagementData;
import org.apache.skywalking.oap.server.core.analysis.worker.ManagementStreamProcessor;
import org.apache.skywalking.oap.server.core.management.ui.template.UITemplate;
import org.apache.skywalking.oap.server.core.source.ScopeDeclaration;
import org.apache.skywalking.oap.server.core.storage.StorageBuilder;
import org.apache.skywalking.oap.server.core.storage.annotation.Column;

import java.util.Date;
import java.util.Map;

import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.ALARM_TEMPLATE;
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.UI_TEMPLATE;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = ALARM_TEMPLATE, name = "ALARMTemplate")
@Stream(name = AlarmTemplate.INDEX_NAME, scopeId = ALARM_TEMPLATE, builder = AlarmTemplate.Builder.class, processor = ManagementStreamProcessor.class)
public class AlarmTemplate extends ManagementData {
    public static final String INDEX_NAME = "t_mt_alarm";

    @Column(columnName = "@timestamp")
    private Date timestamp;

    @Column(columnName = "alarm_kpi")
    private long alarmKpi;

    @Column(columnName = "device_type")
    private long deviceType;

    @Column(columnName = "end_time")
    private long endTime;

    @Column(columnName = "index_id")
    private String indexId;

    @Column(columnName = "ip")
    private String ip;

    @Column(columnName = "level")
    private long level;

    @Column(columnName = "media_type")
    private long mediaType;

    @Column(columnName = "message")
    private String message;

    @Column(columnName = "monitor_type")
    private long monitorType;

    @Column(columnName = "related_id")
    private String relatedId;

    @Column(columnName = "start_time")
    private long startTime;

    @Column(columnName = "status")
    private long status;

    @Column(columnName = "usage")
    private float usage;

    @Override
    public String id() {
        return "";
    }
    public static class Builder implements StorageBuilder<AlarmTemplate> {

        @Override
        public AlarmTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(AlarmTemplate storageData) {
            return null;
        }
    }
}

