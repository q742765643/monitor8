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

import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.HOST_DWON_TEMPLATE;
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.PROCESS_DWON_TEMPLATE;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = PROCESS_DWON_TEMPLATE, name = "PROCESSDownTemplate")
@Stream(name = ProcessDownTemplate.INDEX_NAME, scopeId = PROCESS_DWON_TEMPLATE, builder = ProcessDownTemplate.Builder.class, processor = ManagementStreamProcessor.class)
public class ProcessDownTemplate extends ManagementData {
    public static final String INDEX_NAME = "t_mt_process_down_log";

    @Column(columnName = "@timestamp")
    private Date timestamp;

    @Column(columnName = "duration")
    private long duration;

    @Column(columnName = "end_time")
    private long endTime;

    @Column(columnName = "id")
    private String id;

    @Column(columnName = "index_id")
    private String indexId;

    @Column(columnName = "ip")
    private String ip;

    @Column(columnName = "start_time")
    private long startTime;

    @Column(columnName = "status")
    private long status;

    @Column(columnName = "type")
    private long type;

    @Override
    public String id() {
        return "";
    }
    public static class Builder implements StorageBuilder<ProcessDownTemplate> {

        @Override
        public ProcessDownTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(ProcessDownTemplate storageData) {
            return null;
        }
    }
}

