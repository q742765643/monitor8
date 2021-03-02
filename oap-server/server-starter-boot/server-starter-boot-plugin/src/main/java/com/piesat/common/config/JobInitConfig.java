package com.piesat.common.config;

import com.google.gson.Gson;
import com.piesat.skywalking.service.quartz.timing.*;
import com.piesat.skywalking.service.timing.JobScheduleHelper;
import com.piesat.sso.client.util.RedisUtil;
import io.swagger.annotations.ApiModelProperty;
import org.apache.skywalking.apm.util.StringUtil;
import org.apache.skywalking.oap.server.core.management.ui.template.mt.*;
import org.apache.skywalking.oap.server.core.storage.annotation.Column;
import org.apache.skywalking.oap.server.core.storage.model.Model;
import org.apache.skywalking.oap.server.core.storage.model.ModelColumn;
import org.apache.skywalking.oap.server.library.client.elasticsearch.ElasticSearchClient;
import org.apache.skywalking.oap.server.library.util.ResourceUtils;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch.StorageModuleElasticsearchConfig;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch.base.ColumnTypeEsMapping;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch.base.MatchCNameBuilder;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static io.protostuff.CollectionSchema.MessageFactories.ArrayList;

@Component
@Order(2)
public class JobInitConfig implements ApplicationRunner {
    @Autowired
    private AutoDiscoveryQuartzService autoDiscoveryQuartzService;
    @Autowired
    private HostConfigQuartzService hostConfigQuartzService;
    @Autowired
    private AlarmConfigQuartzService alarmConfigQuartzService;
    @Autowired
    private FileMonitorQuartzService fileMonitorQuartzService;
    @Autowired
    private JobScheduleHelper jobScheduleHelper;
    @Autowired
    private JobInfoQuartzService jobInfoQuartzService;
    private final Gson gson = new Gson();
    private final StorageModuleElasticsearchConfig config =new StorageModuleElasticsearchConfig();
    protected final ColumnTypeEsMapping columnTypeEsMapping =new ColumnTypeEsMapping();
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        redisUtil.deleteAll();
        autoDiscoveryQuartzService.initJob();
        hostConfigQuartzService.initJob();
        alarmConfigQuartzService.initJob();
        fileMonitorQuartzService.initJob();
        jobInfoQuartzService.initJob();
        jobScheduleHelper.start();
        this.createTable(AlarmlogTemplate.INDEX_NAME,AlarmlogTemplate.class);
        this.createTable(AlarmTemplate.INDEX_NAME,AlarmTemplate.class);
        this.createTable(FileMonitorTemplate.INDEX_NAME,FileMonitorTemplate.class);
        this.createTable(FileStatisticsTemplate.INDEX_NAME,FileStatisticsTemplate.class);
        this.createTable(HostDownTemplate.INDEX_NAME,HostDownTemplate.class);
        this.createTable(MediaOverviewTemplate.INDEX_NAME,MediaOverviewTemplate.class);
        this.createTable(MediaReportTemplate.INDEX_NAME,MediaReportTemplate.class);
        this.createTable(MetricbeatTemplate.INDEX_NAME,MetricbeatTemplate.class);
        this.createTable(ProcessDownTemplate.INDEX_NAME,ProcessDownTemplate.class);
        this.createTable(ProcessReportTemplate.INDEX_NAME,ProcessReportTemplate.class);
    }
    public void createTable(String indexName,Class<?> c){
        Map<String, Object> settings = createSetting();
        Map<String, Object> mapping = createMapping(c);
        try {
                if(indexName.indexOf("metricbeat")>-1){
                    InputStream inputStream = ResourceUtils.readToStream("metricbeat.json");
                    String json = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().collect(Collectors.joining(System.lineSeparator()));
                    Gson gson = new Gson();
                    mapping=gson.fromJson(json,Map.class);
                    settings.put("index.mapping.total_fields.limit",30000);
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    if (!elasticSearch7Client.isExistsIndex(indexName)) {
                        boolean isAcknowledged = elasticSearch7Client.createTemplate(indexName, settings, mapping);
                    }else {
                        elasticSearch7Client.updateMapping(indexName,mapping);
                    }
                }else{
                    if (!elasticSearch7Client.isExistsIndex(indexName)) {
                        elasticSearch7Client.createIndex(indexName,settings,mapping);
                    }else {
                        elasticSearch7Client.updateMapping(indexName,mapping);
                    }

                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Map<String, Object> createSetting() {
        Map<String, Object> setting = new HashMap<>();

        setting.put("index.number_of_replicas", config.getIndexReplicasNumber());
        setting.put("index.number_of_shards", true
                ? config.getIndexShardsNumber() * config.getSuperDatasetIndexShardsFactor()
                : config.getIndexShardsNumber());
        setting.put("index.refresh_interval", true
                ? TimeValue.timeValueSeconds(10).toString()
                : TimeValue.timeValueSeconds(config.getFlushInterval()).toString());
        setting.put("analysis.analyzer.oap_analyzer.type", "stop");
        if (!StringUtil.isEmpty(config.getAdvanced())) {
            Map<String, Object> advancedSettings = gson.fromJson(config.getAdvanced(), Map.class);
            advancedSettings.forEach(setting::put);
        }
        return setting;
    }

    protected Map<String, Object> createMapping(Class<?> c) {
        Map<String, Object> mapping = new HashMap<>();

        Map<String, Object> properties = new HashMap<>();
        mapping.put("properties", properties);
        Field[] fields = c.getDeclaredFields();
        Arrays.stream(fields).forEach(f -> {
            if(f.isAnnotationPresent(Column.class)){
                Column annotation = f.getAnnotation(Column.class);
                String name = annotation.columnName();
                Map<String, Object> column = new HashMap<>();
                column.put("type", columnTypeEsMapping.transform(f.getType()));
                properties.put(name, column);
            }
        });

        return mapping;

    }
}
