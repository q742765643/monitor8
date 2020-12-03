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

package org.apache.skywalking.oap.server.storage.plugin.elasticsearch.base;

import com.google.gson.Gson;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.util.StringUtil;
import org.apache.skywalking.oap.server.core.MonitorConstant;
import org.apache.skywalking.oap.server.core.storage.StorageException;
import org.apache.skywalking.oap.server.core.storage.model.Model;
import org.apache.skywalking.oap.server.core.storage.model.ModelColumn;
import org.apache.skywalking.oap.server.core.storage.model.ModelInstaller;
import org.apache.skywalking.oap.server.library.client.Client;
import org.apache.skywalking.oap.server.library.client.elasticsearch.ElasticSearchClient;
import org.apache.skywalking.oap.server.library.module.ModuleManager;
import org.apache.skywalking.oap.server.library.util.ResourceUtils;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch.StorageModuleElasticsearchConfig;
import org.elasticsearch.common.unit.TimeValue;

@Slf4j
public class StorageEsInstaller extends ModelInstaller {
    private final Gson gson = new Gson();

    private final StorageModuleElasticsearchConfig config;
    protected final ColumnTypeEsMapping columnTypeEsMapping;

    public StorageEsInstaller(Client client,
                              ModuleManager moduleManager,
                              final StorageModuleElasticsearchConfig config) {
        super(client, moduleManager);
        this.columnTypeEsMapping = new ColumnTypeEsMapping();
        this.config = config;
    }

    @Override
    protected boolean isExists(Model model) throws StorageException {
        ElasticSearchClient esClient = (ElasticSearchClient) client;
        try {
            String timeSeriesIndexName =
                model.isTimeSeries() ?
                    TimeSeriesUtils.latestWriteIndexName(model) :
                    model.getName();
            if (!MonitorConstant.ISENABLE) {
                boolean flag = esClient.isExistsTemplate(model.getName()) && esClient.isExistsIndex(timeSeriesIndexName);
                if (timeSeriesIndexName.indexOf("ui_template") > -1 || timeSeriesIndexName.indexOf("t_mt") > -1 || timeSeriesIndexName.indexOf("metricbeat") > -1 ) {
                    return flag;
                }
                return true;
            }
            return esClient.isExistsTemplate(model.getName()) && esClient.isExistsIndex(timeSeriesIndexName);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    protected void createTable(Model model) throws StorageException {
        ElasticSearchClient esClient = (ElasticSearchClient) client;

        Map<String, Object> settings = createSetting(model);
        Map<String, Object> mapping = createMapping(model);
        log.info("index {}'s columnTypeEsMapping builder str: {}", esClient.formatIndexName(model.getName()), mapping
            .toString());

        try {
            String indexName;
            if (!model.isTimeSeries()) {
                indexName = model.getName();
            } else {
                if (!esClient.isExistsTemplate(model.getName())) {
                    indexName=model.getName();
                    if(model.getName().indexOf("metricbeat")>-1){
                        InputStream inputStream = ResourceUtils.readToStream("metricbeat.json");
                        String json = new BufferedReader(new InputStreamReader(inputStream))
                                .lines().collect(Collectors.joining(System.lineSeparator()));
                        Gson gson = new Gson();
                        mapping=gson.fromJson(json,Map.class);
                        settings.put("index.mapping.total_fields.limit",30000);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        indexName = model.getName() + "-" + format.format(new Date());
                    }
                    boolean isAcknowledged = esClient.createTemplate(indexName, settings, mapping);
                    log.info(
                        "create {} index template finished, isAcknowledged: {}", model.getName(), isAcknowledged);
                    if (!isAcknowledged) {
                        throw new StorageException("create " + model.getName() + " index template failure, ");
                    }
                }
                indexName = TimeSeriesUtils.latestWriteIndexName(model);
            }
            if (!esClient.isExistsIndex(indexName)) {
                if(indexName.indexOf("t_mt")>-1){
                    esClient.createIndex(indexName,settings,mapping);
                }else {
                    boolean isAcknowledged = esClient.createIndex(indexName);
                    log.info("create {} index finished, isAcknowledged: {}", indexName, isAcknowledged);
                    if (!isAcknowledged) {
                        throw new StorageException("create " + indexName + " time series index failure, ");
                    }
                }

            }

        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
    }

    protected Map<String, Object> createSetting(Model model) {
        Map<String, Object> setting = new HashMap<>();

        setting.put("index.number_of_replicas", config.getIndexReplicasNumber());
        setting.put("index.number_of_shards", model.isSuperDataset()
            ? config.getIndexShardsNumber() * config.getSuperDatasetIndexShardsFactor()
            : config.getIndexShardsNumber());
        setting.put("index.refresh_interval", model.isRecord()
            ? TimeValue.timeValueSeconds(10).toString()
            : TimeValue.timeValueSeconds(config.getFlushInterval()).toString());
        setting.put("analysis.analyzer.oap_analyzer.type", "stop");
        if (!StringUtil.isEmpty(config.getAdvanced())) {
            Map<String, Object> advancedSettings = gson.fromJson(config.getAdvanced(), Map.class);
            advancedSettings.forEach(setting::put);
        }
        return setting;
    }

    protected Map<String, Object> createMapping(Model model) {
        Map<String, Object> mapping = new HashMap<>();
        Map<String, Object> type = new HashMap<>();

        mapping.put(ElasticSearchClient.TYPE, type);

        Map<String, Object> properties = new HashMap<>();
        type.put("properties", properties);

        for (ModelColumn columnDefine : model.getColumns()) {
            if (columnDefine.isMatchQuery()) {
                String matchCName = MatchCNameBuilder.INSTANCE.build(columnDefine.getColumnName().getName());

                Map<String, Object> originalColumn = new HashMap<>();
                originalColumn.put("type", columnTypeEsMapping.transform(columnDefine.getType()));
                originalColumn.put("copy_to", matchCName);
                properties.put(columnDefine.getColumnName().getName(), originalColumn);

                Map<String, Object> matchColumn = new HashMap<>();
                matchColumn.put("type", "text");
                matchColumn.put("analyzer", "oap_analyzer");
                properties.put(matchCName, matchColumn);
            } else {
                Map<String, Object> column = new HashMap<>();
                column.put("type", columnTypeEsMapping.transform(columnDefine.getType()));
                if (columnDefine.isStorageOnly()) {
                    column.put("index", false);
                }
                properties.put(columnDefine.getColumnName().getName(), column);
            }
        }

        log.debug("elasticsearch index template setting: {}", mapping.toString());

        return mapping;
    }
}
