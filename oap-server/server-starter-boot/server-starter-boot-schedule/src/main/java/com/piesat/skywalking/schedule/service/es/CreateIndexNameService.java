package com.piesat.skywalking.schedule.service.es;

import com.piesat.constant.IndexNameConstant;
import com.piesat.util.IndexNameUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CreateIndexNameService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public void createAlarmLog(String indexName){
        /*try {
            if(!elasticSearch7Client.isExistsIndex(indexName)){
                Map<String,Object> settings=new HashMap<>();
                Map<String,Object> mapping=new HashMap<>();
                Map<String,Object> properties=new HashMap<>();

                Map<String,Object> deviceType=new HashMap<>();
                deviceType.put("type","text");
                deviceType.put("fielddata",true);

                properties.put("device_type",deviceType);
                mapping.put("properties",properties);
                elasticSearch7Client.createIndex(indexName,settings,mapping);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
}
