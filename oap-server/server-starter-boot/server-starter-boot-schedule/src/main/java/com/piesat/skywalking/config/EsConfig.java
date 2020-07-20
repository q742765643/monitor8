package com.piesat.skywalking.config;

import org.apache.skywalking.apm.util.StringUtil;
import org.apache.skywalking.oap.server.library.client.elasticsearch.IndexNameConverter;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.skywalking.oap.server.storage.plugin.elasticsearch.StorageModuleElasticsearchProvider.indexNameConverters;

@Configuration
public class EsConfig {
    @Value("${storage.elasticsearch.nameSpace}")
    private String nameSpace;
    @Value("${storage.elasticsearch.clusterNodes}")
    private String clusterNodes;
    @Value("${storage.elasticsearch.protocol}")
    private String protocol;
    @Value("${storage.elasticsearch.user}")
    private String user;
    @Value("${storage.elasticsearch.password}")
    private String password;
    @Value("${storage.elasticsearch.trustStorePath}")
    private String trustStorePath;
    @Value("${storage.elasticsearch.trustStorePass}")
    private String trustStorePass;
    @Bean
    public ElasticSearch7Client elasticSearch7Client(){
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
