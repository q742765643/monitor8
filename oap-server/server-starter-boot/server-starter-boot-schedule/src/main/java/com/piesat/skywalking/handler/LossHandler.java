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
import com.piesat.util.IndexNameUtil;
import com.piesat.util.PingUtil;
import com.piesat.util.ResultT;
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

    private static String HOST_EXCEPTION="HTHT.HOST_EXCEPTION";
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        List<HostConfigDto> hostConfigDtos =hostConfigService.selectOnineAll();
        if(null!=hostConfigDtos&&hostConfigDtos.size()>0){
            for(int i=0;i<hostConfigDtos.size();i++){
                HostConfigDto hostConfigDto=hostConfigDtos.get(i);
                this.insertPacket(hostConfigDto);
            }
        }

    }
    public void insertPacket(HostConfigDto hostConfigDto) {
        try {
            Map<String, Object> basicInfo = new HashMap<>();
            basicInfo.put("ip", hostConfigDto.getIp());
            basicInfo.put("@timestamp", new Date());
            basicInfo.put("hostname", hostConfigDto.getIp());
            Map<String, Object> source = snmpWindowsService.metricbeatMap("packet", basicInfo);
            float usage = this.selectPing(hostConfigDto.getIp());
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
            hostConfigService.save(hostConfigDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float selectPing(String ip) {
        float usage = -1;
        try {
            ResultT<String> resultT = PingUtil.ping(ip);
            if (!resultT.isSuccess()) {
                return usage;
            }
            //Pattern pattern = Pattern.compile("[\\w\\W]*丢失 = ([0-9]\\d*)[\\w\\W]*");
            Pattern pattern = Pattern.compile("([0-9]\\d*)% 丢失");
            Matcher matcher = pattern.matcher(resultT.getData());
            while (matcher.find()) {
                usage = Float.parseFloat(matcher.group(1)) / 100;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usage;

    }
    public void outageStatistics(HostConfigDto hostConfigDto,boolean flag){
        try {
            long value= this.getValue(hostConfigDto);
            if(value==0&&!flag){
                redisUtil.hset(HOST_EXCEPTION,hostConfigDto.getId(),System.currentTimeMillis());
            }
            if(value>0&&flag){
                redisUtil.hdel(HOST_EXCEPTION,hostConfigDto.getId());
                Map<String, Object> source = new HashMap<>();
                try {
                    long endTime=System.currentTimeMillis();
                    source.put("id",hostConfigDto.getId());
                    source.put("ip",hostConfigDto.getIp());
                    source.put("start_time",value);
                    source.put("end_time",System.currentTimeMillis());
                    source.put("duration",endTime-value);
                    source.put("@timestamp",new Date());
                    boolean isExistsIndex = elasticSearch7Client.isExistsIndex(IndexNameConstant.T_MT_HOST_DOWN_LOG);
                    if (!isExistsIndex) {
                        Map<String, Object> ip = new HashMap<>();
                        ip.put("type", "keyword");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put("ip", ip);
                        Map<String, Object> mapping = new HashMap<>();
                        mapping.put("properties", properties);
                        elasticSearch7Client.createIndex(IndexNameConstant.T_MT_HOST_DOWN_LOG, new HashMap<>(), mapping);
                    }
                    elasticSearch7Client.forceInsert(IndexNameConstant.T_MT_HOST_DOWN_LOG, IdUtils.fastUUID(), source);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("related_id", id);
        boolBuilder.must(matchId);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        search.query(boolBuilder).sort("level",SortOrder.DESC);
        search.size(1);
        long level=3;
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM_LOG, search);
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
}

