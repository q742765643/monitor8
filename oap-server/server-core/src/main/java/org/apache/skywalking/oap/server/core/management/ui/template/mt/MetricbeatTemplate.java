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
import org.apache.skywalking.oap.server.core.analysis.metrics.Metrics;
import org.apache.skywalking.oap.server.core.analysis.record.Record;
import org.apache.skywalking.oap.server.core.analysis.worker.ManagementStreamProcessor;
import org.apache.skywalking.oap.server.core.analysis.worker.RecordStreamProcessor;
import org.apache.skywalking.oap.server.core.remote.grpc.proto.RemoteData;
import org.apache.skywalking.oap.server.core.source.ScopeDeclaration;
import org.apache.skywalking.oap.server.core.storage.StorageBuilder;
import org.apache.skywalking.oap.server.core.storage.annotation.Column;

import java.util.Date;
import java.util.Map;

import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.ALARM_TEMPLATE;
import static org.apache.skywalking.oap.server.core.source.DefaultScopeDefine.METRICBEAT;

/**
 * @ClassName : AalarmTemplate
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-01 13:16
 */
@Setter
@Getter
@ScopeDeclaration(id = METRICBEAT, name = "METRICBEATTemplate")
@Stream(name = MetricbeatTemplate.INDEX_NAME, scopeId = METRICBEAT, builder = MetricbeatTemplate.Builder.class, processor = RecordStreamProcessor.class)
public class MetricbeatTemplate extends Metrics {
    public static final String INDEX_NAME = "metricbeat";


    @Override
    public String id() {
        return "";
    }

    @Override
    public void combine(Metrics metrics) {

    }

    @Override
    public void calculate() {

    }

    @Override
    public Metrics toHour() {
        return null;
    }

    @Override
    public Metrics toDay() {
        return null;
    }

    @Override
    public int remoteHashCode() {
        return 0;
    }

    @Override
    public void deserialize(RemoteData remoteData) {

    }

    @Override
    public RemoteData.Builder serialize() {
        return null;
    }

    public static class Builder implements StorageBuilder<MetricbeatTemplate> {

        @Override
        public MetricbeatTemplate map2Data(Map<String, Object> dbMap) {
            return null;
        }

        @Override
        public Map<String, Object> data2Map(MetricbeatTemplate storageData) {
            return null;
        }
    }
}

