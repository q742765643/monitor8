package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.snmp.*;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.skywalking.vo.FileSystemVo;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.ucenter.rpc.api.system.DictDataService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.util.IndexNameUtil;
import com.piesat.util.PingUtil;
import com.piesat.util.ResultT;
import com.piesat.util.StringUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : LossHandler
 * @Description : 丢包率采集
 * @Author : zzj
 * @Date: 2020-11-04 11:00
 */
@Service("lossHandler")
public class LossHandler implements BaseHandler {
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @Autowired
    private SNMPWindowsService snmpWindowsService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private RedisUtil redisUtil;
    @GrpcHthtClient
    private DictDataService dictDataService;

    private static String HOST_EXCEPTION="HTHT.HOST_EXCEPTION";
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        List<HostConfigDto> hostConfigDtos =hostConfigService.selectOnineAll();
        List<DictDataDto> dictDataDtoList = dictDataService.selectDictDataByType("packet_loss_regular");
        if(null!=hostConfigDtos&&hostConfigDtos.size()>0){
            for(int i=0;i<hostConfigDtos.size();i++){
                HostConfigDto hostConfigDto=hostConfigDtos.get(i);
                this.insertPacket(hostConfigDto,dictDataDtoList);
            }
        }

    }
    public void insertPacket(HostConfigDto hostConfigDto,List<DictDataDto> dictDataDtoList) {
        try {
            Map<String, Object> basicInfo = new HashMap<>();
            basicInfo.put("ip", hostConfigDto.getIp());
            basicInfo.put("@timestamp", new Date());
            basicInfo.put("hostname", hostConfigDto.getIp());
            Map<String, Object> source = snmpWindowsService.metricbeatMap("packet", basicInfo);
            float usage = this.selectPing(hostConfigDto.getIp(),dictDataDtoList);
            source.put("loss", (float) (Math.round(usage * 100) / 100));
            boolean flag=false;
            if (usage > -1) {
                flag = true;
                String indexName = IndexNameUtil.getIndexName(IndexNameConstant.METRICBEAT, new Date());
                elasticSearch7Client.forceInsert(indexName, IdUtils.fastUUID(), source);
            }
            this.outageStatistics(hostConfigDto,flag);
            long level=this.findLevel(hostConfigDto.getId());
            hostConfigDto.setCurrentStatus((int) level);
            hostConfigService.upateStatus(hostConfigDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float selectPing(String ip,List<DictDataDto> dictDataDtoList) {
        float usage = -1;
        try {
            ResultT<String> resultT = PingUtil.ping(ip);
            if (!resultT.isSuccess()) {
                return usage;
            }
            //Pattern pattern = Pattern.compile("[\\w\\W]*丢失 = ([0-9]\\d*)[\\w\\W]*");
            if(null!=dictDataDtoList&&dictDataDtoList.size()>0){
                for(DictDataDto dictDataDto:dictDataDtoList){
                    Pattern pattern = Pattern.compile(dictDataDto.getDictValue());
                    Matcher matcher = pattern.matcher(resultT.getData());
                    while (matcher.find()) {
                        usage = Float.parseFloat(matcher.group(1)) / 100;
                        break;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usage;

    }
    public void outageStatistics(HostConfigDto hostConfigDto,boolean flag){
        try {
            Map<String, Object> source=this.findDownLog(hostConfigDto);
            long startTime= new BigDecimal(String.valueOf(source.get("start_time"))).longValue();
            source.put("end_time",System.currentTimeMillis());
            source.put("duration",System.currentTimeMillis()-startTime);
            if(!flag){
                this.insertDownLog(source);
            }
            if(flag){
               String indexId= (String) source.get("index_id");
               if(StringUtil.isEmpty(indexId)){
                   return;
               }
               source.put("status",1);
               this.insertDownLog(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long getValue(HostConfigDto hostConfigDto){
        Object o= redisUtil.hget(HOST_EXCEPTION,hostConfigDto.getId());
        if(o==null){
            return 0;
        }else {
            return new BigDecimal(String.valueOf(o)).longValue();
        }
    }
    public long findLevel(String id){

        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("related_id", id);
        MatchQueryBuilder matchStatus = QueryBuilders.matchQuery("status", 0);
        boolBuilder.must(matchId);
        boolBuilder.must(matchStatus);
        search.query(boolBuilder).sort("level",SortOrder.DESC);
        search.size(1);
        long level=3;
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, search);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            if(searchHits.length>0){
                for (SearchHit hit : searchHits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    return new BigDecimal(String.valueOf(map.get("level"))).longValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }
    public void insertDownLog(Map<String, Object> source){
        try {
            String indexId= (String) source.get("index_id");
            if(StringUtil.isEmpty(indexId)){
                indexId=IdUtils.fastUUID();
            }
      /*      boolean isExistsIndex = elasticSearch7Client.isExistsIndex(IndexNameConstant.T_MT_HOST_DOWN_LOG);
            if (!isExistsIndex) {
                Map<String, Object> ip = new HashMap<>();
                ip.put("type", "keyword");
                Map<String, Object> id = new HashMap<>();
                id.put("type", "keyword");
                Map<String, Object> properties = new HashMap<>();
                properties.put("ip", ip);
                properties.put("id", id);
                Map<String, Object> mapping = new HashMap<>();
                mapping.put("properties", properties);
                elasticSearch7Client.createIndex(IndexNameConstant.T_MT_HOST_DOWN_LOG, new HashMap<>(), mapping);
            }*/
            elasticSearch7Client.forceInsert(IndexNameConstant.T_MT_HOST_DOWN_LOG, indexId, source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object>  findDownLog(HostConfigDto hostConfigDto){
        Map<String, Object> source=new HashMap<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("id", hostConfigDto.getId());
        MatchQueryBuilder matchStatus = QueryBuilders.matchQuery("status", 0);
        boolBuilder.must(matchId);
        boolBuilder.must(matchStatus);
        search.query(boolBuilder);
        search.size(1);
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_HOST_DOWN_LOG, search);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            if(searchHits.length>0){
                for (SearchHit hit : searchHits) {
                      source = hit.getSourceAsMap();
                      source.put("index_id",hit.getId());
                      return source;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        source.put("id",hostConfigDto.getId());
        source.put("ip",hostConfigDto.getIp());
        source.put("@timestamp",new Date());
        source.put("status",0);
        source.put("start_time",System.currentTimeMillis());
        return source;
    }
}

