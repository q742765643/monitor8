package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import com.piesat.util.StringUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : AlarmProcessService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-02 11:09
 */
@Service
public class AlarmProcessService extends AlarmBaseService {
    @GrpcHthtClient
    private ProcessConfigService processConfigService;
    @Autowired
    private RedisUtil redisUtil;

    private static String PROCESS_EXCEPTION="HTHT.PROCESS_EXCEPTION";
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto = (AlarmConfigDto) jobContext.getHtJobInfoDto();
        ProcessConfigDto processConfigQDto=new ProcessConfigDto();
        NullUtil.changeToNull(processConfigQDto);
        List<ProcessConfigDto> processConfigDtos=processConfigService.selectBySpecification(processConfigQDto);
        for (int i = 0; i < processConfigDtos.size(); i++) {
            AlarmLogDto alarmLogDto = new AlarmLogDto();
            ProcessConfigDto processConfigDto=processConfigDtos.get(i);
            alarmLogDto.setUsage(this.findProcess(processConfigDto));
            this.toAlarm(alarmLogDto, alarmConfigDto, processConfigDto);
        }
    }

    public void toAlarm(AlarmLogDto alarmLogDto, AlarmConfigDto alarmConfigDto, ProcessConfigDto processConfigDto) {
        alarmLogDto.setIp(processConfigDto.getIp());
        alarmLogDto.setRelatedId(processConfigDto.getId());
        alarmLogDto.setMediaType(0);
        alarmLogDto.setDeviceType(2);
        this.fitAlarmLog(alarmConfigDto, alarmLogDto);
        this.judgeAlarm(alarmLogDto);
        processConfigDto.setCurrentStatus(3);
        if (alarmLogDto.isAlarm()) {
            String message =processConfigDto.getIp()+":进程"+processConfigDto.getProcessName()+"未采集到进程信息,请检查环境";
            if (alarmLogDto.getUsage() > 0) {
                message =processConfigDto.getIp()+":进程"+processConfigDto.getProcessName()+" cpu变化率为" + new BigDecimal(alarmLogDto.getUsage()).setScale(2,BigDecimal.ROUND_HALF_UP)+"%"  ;
            }
            alarmLogDto.setMessage(message);
            this.insertEs(alarmLogDto);
            processConfigDto.setCurrentStatus(alarmLogDto.getLevel());
        }
        this.outageStatistics(processConfigDto,alarmLogDto);
        this.insertUnprocessed(alarmLogDto);
        processConfigService.save(processConfigDto);
    }


    public float findProcess(ProcessConfigDto processConfigDto){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.process");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", processConfigDto.getIp());
        WildcardQueryBuilder wild = QueryBuilders.wildcardQuery("system.process.cmdline", "*"+processConfigDto.getProcessName()+"*");
        WildcardQueryBuilder processSe = QueryBuilders.wildcardQuery("process.name", "*"+processConfigDto.getProcessName()+"*");
        boolBuilder.should(wild);
        boolBuilder.should(processSe);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(1000);
        float usage=-1;
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            boolean flag=false;
            if(searchHits.length>0){
                Set<String> stringSet=new HashSet<>();
                for (SearchHit hit : searchHits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    Map<String, Object> pro = (Map<String, Object>) map.get("process");
                    Map<String, Object> system = (Map<String, Object>) map.get("system");
                    Map<String, Object> process = (Map<String, Object>) system.get("process");
                    Map<String, Object> cpu = (Map<String, Object>) process.get("cpu");
                    Map<String, Object> total = (Map<String, Object>) cpu.get("total");
                    stringSet.add(String.valueOf(total.get("value")));
                    if(!flag){
                        processConfigDto.setPid(Integer.parseInt(String.valueOf(pro.get("pid"))));
                        processConfigDto.setCmdline(String.valueOf(process.get("cmdline")));
                    }
                    flag=true;
                }
                usage=(new BigDecimal(stringSet.size()).divide(new BigDecimal(searchHits.length),4,BigDecimal.ROUND_HALF_UP)).floatValue()*100;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usage;

    }
    public void outageStatistics(ProcessConfigDto processConfigDto,AlarmLogDto alarmLogDto){
        try {
            Map<String, Object> source=this.findProcessDownLog(processConfigDto,alarmLogDto);
            long startTime= new BigDecimal(String.valueOf(source.get("start_time"))).longValue();
            source.put("end_time",System.currentTimeMillis());
            source.put("duration",System.currentTimeMillis()-startTime);
            if(!alarmLogDto.isAlarm()){
                String indexId= (String) source.get("index_id");
                if(StringUtil.isEmpty(indexId)){
                    return;
                }
                source.put("status",1);
                this.insertPorcessDownLog(source);
            }
            if(alarmLogDto.isAlarm()){
                String indexId= (String) source.get("index_id");
                if(StringUtil.isEmpty(indexId)){
                    this.insertPorcessDownLog(source);
                }else {
                    String oldType= (String) source.get("type");
                    String newType="1";
                    if(alarmLogDto.getUsage()==-1){
                           newType="-1";
                    }
                    if(!oldType.equals(newType)){
                        source.put("status",1);
                    }
                    this.insertPorcessDownLog(source);
                    if(!oldType.equals(newType)){
                        source.put("id",processConfigDto.getId());
                        source.put("ip",processConfigDto.getIp());
                        source.put("@timestamp",new Date());
                        source.put("status",0);
                        if(alarmLogDto.getUsage()==-1){
                            source.put("type","-1");
                        }else {
                            source.put("type","1");
                        }
                        source.put("start_time",System.currentTimeMillis());
                        source.put("end_time",System.currentTimeMillis());
                        source.put("duration",System.currentTimeMillis()-startTime);
                        this.insertPorcessDownLog(source);
                    }
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public long getValue(ProcessConfigDto processConfigDto){
        Object o= redisUtil.hget(PROCESS_EXCEPTION,processConfigDto.getId());
        if(o==null){
            return 0;
        }else {
            return new BigDecimal(String.valueOf(o)).longValue();
        }
    }

    public void insertPorcessDownLog(Map<String, Object> source){
        try {
            String indexId= (String) source.get("index_id");
            if(StringUtil.isEmpty(indexId)){
                indexId=IdUtils.fastUUID();
            }
    /*        boolean isExistsIndex = elasticSearch7Client.isExistsIndex(IndexNameConstant.T_MT_PROCESS_DOWN_LOG);
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
                elasticSearch7Client.createIndex(IndexNameConstant.T_MT_PROCESS_DOWN_LOG, new HashMap<>(), mapping);
            }*/
            elasticSearch7Client.forceInsert(IndexNameConstant.T_MT_PROCESS_DOWN_LOG, indexId, source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object>  findProcessDownLog(ProcessConfigDto processConfigDto,AlarmLogDto alarmLogDto){
        Map<String, Object> source=new HashMap<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchId = QueryBuilders.matchQuery("id", processConfigDto.getId());
        MatchQueryBuilder matchStatus = QueryBuilders.matchQuery("status", 0);
        boolBuilder.must(matchId);
        boolBuilder.must(matchStatus);
        search.query(boolBuilder);
        search.size(1);
        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_PROCESS_DOWN_LOG, search);
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
        source.put("id",processConfigDto.getId());
        source.put("ip",processConfigDto.getIp());
        source.put("@timestamp",new Date());
        source.put("status",0);
        if(alarmLogDto.getUsage()==-1){
            source.put("type","-1");
        }else {
            source.put("type","1");
        }
        source.put("start_time",System.currentTimeMillis());
        return source;
    }
}

