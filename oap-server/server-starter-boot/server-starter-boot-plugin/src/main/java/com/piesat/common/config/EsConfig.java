package com.piesat.common.config;

import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.StorageModuleElasticsearch7Provider;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {
    @Bean
    public ElasticSearch7Client elasticSearch7Client(){
        return StorageModuleElasticsearch7Provider.esMap.get("es");
    }
}
