package com.piesat.common.config;

import org.apache.skywalking.apm.util.StringUtil;
import org.apache.skywalking.oap.server.library.client.elasticsearch.IndexNameConverter;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.StorageModuleElasticsearch7Provider;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EsConfig {
    @Value("${storage.elasticsearch7.nameSpace}")
    private String nameSpace;
    @Value("${storage.elasticsearch7.clusterNodes}")
    private String clusterNodes;
    @Value("${storage.elasticsearch7.protocol}")
    private String protocol;
    @Value("${storage.elasticsearch7.user}")
    private String user;
    @Value("${storage.elasticsearch7.password}")
    private String password;
    @Value("${storage.elasticsearch7.trustStorePath}")
    private String trustStorePath;
    @Value("${storage.elasticsearch7.trustStorePass}")
    private String trustStorePass;

    @Bean
    public ElasticSearch7Client elasticSearch7Client(){
        if(null==StorageModuleElasticsearch7Provider.ESMAP.get("es")){
            clusterNodes=clusterNodes.replaceAll("\"","");
            protocol=protocol.replaceAll("\"","");
            trustStorePath=trustStorePath.replaceAll("\"","");
            trustStorePass=trustStorePass.replaceAll("\"","");
            user=user.replaceAll("\"","");
            password=password.replaceAll("\"","");
            nameSpace=nameSpace.replaceAll("\"","");
            ElasticSearch7Client elasticSearchClient =new ElasticSearch7Client(
                    clusterNodes, protocol, trustStorePath, trustStorePass,user, password,
                    indexNameConverters(nameSpace)
            );
            try {
                elasticSearchClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return elasticSearchClient;
        }
        return StorageModuleElasticsearch7Provider.ESMAP.get("es");
    }
    public static List<IndexNameConverter> indexNameConverters(String namespace) {
        List<IndexNameConverter> converters = new ArrayList<>();
        converters.add(new NamespaceConverter(namespace));
        return converters;
    }

    private static class NamespaceConverter implements IndexNameConverter {
        private final String namespace;

        public NamespaceConverter(final String namespace) {
            this.namespace = namespace;
        }

        @Override
        public String convert(final String indexName) {
            if (StringUtil.isNotEmpty(namespace)) {
                return namespace + "_" + indexName;
            }

            return indexName;
        }
    }
}
