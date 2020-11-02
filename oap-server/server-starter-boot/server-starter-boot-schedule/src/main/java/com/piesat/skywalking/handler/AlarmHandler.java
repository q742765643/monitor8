/*
package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseShardHandler;
import com.piesat.skywalking.schedule.service.alarm.AlarmLogService;
import com.piesat.skywalking.util.Ping;
import com.piesat.skywalking.vo.FileSystemVo;
import com.piesat.util.CompareUtil;
import com.piesat.util.PingUtil;
import com.piesat.util.ResultT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("alarmHandler")
public class AlarmHandler  implements BaseShardHandler {
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private AlarmLogService alarmLogService;
    @GrpcHthtClient
    private ProcessConfigService processConfigService;
    @Override
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto= (AlarmConfigDto) jobContext.getHtJobInfoDto();
        if(MonitorTypeEnum.PRCESS==MonitorTypeEnum.match(alarmConfigDto.getMonitorType())){
            ProcessConfigDto processConfigDto=new ProcessConfigDto();
            List<ProcessConfigDto> list=processConfigService.selectBySpecification(processConfigDto);
            return list;
        }
        if(MonitorTypeEnum.PING==MonitorTypeEnum.match(alarmConfigDto.getMonitorType())){
            List<HostConfigDto>  exists=hostConfigService.selectAll();
            return exists;
        }
        List<HostConfigDto>  exists=hostConfigService.selectOnineAll();
        return exists;
    }

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto= (AlarmConfigDto) jobContext.getHtJobInfoDto();
        if(MonitorTypeEnum.PRCESS==MonitorTypeEnum.match(alarmConfigDto.getMonitorType())){
            List<ProcessConfigDto> list=jobContext.getLists();

            for(int i=0;i<list.size();i++){
                AlarmLogDto alarmLogDto=new AlarmLogDto();
                ProcessConfigDto processConfigDto=list.get(i);
                alarmLogDto.setHostId(processConfigDto.getHostId());
                alarmLogDto.setProcessId(processConfigDto.getId());
                float usage=this.selectProcess(processConfigDto);
                String message="";
                if(usage==-1){
                    message="SNMP,代理无法采集进程信息或者进程被关闭";
                }else {
                    message="进程cpu变化次数为"+usage+"次";
                }
                alarmLogDto.setMessage(message);
                alarmLogDto.setIp(processConfigDto.getIp());
                alarmLogDto.setDeviceType(2);
                alarmLogDto.setDeviceName(processConfigDto.getProcessName());
                alarmLogDto.setMonitorType(MonitorTypeEnum.PRCESS.getValue());
                alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto,usage);
            }

            return;
        }
        List<HostConfigDto> list=jobContext.getLists();
        for(int i=0;i<list.size();i++){
            try {
                this.selectSystem(alarmConfigDto,list.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    public void selectSystem(AlarmConfigDto alarmConfigDto,HostConfigDto hostConfigDto){
        String ip=hostConfigDto.getIp();
        MonitorTypeEnum typeEnum=MonitorTypeEnum.match(alarmConfigDto.getMonitorType());
        AlarmLogDto alarmLogDto=new AlarmLogDto();
        alarmLogDto.setHostId(hostConfigDto.getId());
        alarmLogDto.setMediaType(hostConfigDto.getMediaType());
        if(MonitorTypeEnum.CPU_USAGE==typeEnum){
            float usage=this.selectCpu(ip);
            String message="";
            if(usage==-1){
                message="SNMP或者代理无法采集CPU信息";
            }else {
                message="CPU使用率到达阈值条件当前为"+usage+"%";
            }
            alarmLogDto.setMessage(message);
            alarmLogDto.setIp(ip);
            alarmLogDto.setDeviceName("cpu使用率");
            alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
            alarmLogDto.setMonitorType(MonitorTypeEnum.CPU_USAGE.getValue());
            alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto,usage);
        }
        if(MonitorTypeEnum.MEMORY_USAGE==typeEnum){
            float usage=this.selectMemory(ip);
            String message="";
            if(usage==-1){
                message="SNMP或者代理无法采集内存信息";
            }else {
                message="内存使用率到达阈值条件当前为"+usage+"%";
            }
            alarmLogDto.setMessage(message);
            alarmLogDto.setIp(ip);
            alarmLogDto.setDeviceName("内存使用率");
            alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
            alarmLogDto.setMonitorType(MonitorTypeEnum.MEMORY_USAGE.getValue());
            alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto,usage);
        }
        if(MonitorTypeEnum.PING==typeEnum){
            float usage=this.selectPing(ip);
            String message="";
            if(usage==-1){
                message="PING 不可达";
            }else {
                message="Ping 丢包率达到"+usage+"%";
            }
            alarmLogDto.setMessage(message);
            alarmLogDto.setIp(ip);
            alarmLogDto.setDeviceName("PING");
            alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
            alarmLogDto.setMonitorType(MonitorTypeEnum.PING.getValue());
            alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto,usage);
        }
        if(MonitorTypeEnum.DISK_USAGE==typeEnum&&(hostConfigDto.getDeviceType()==0||hostConfigDto.getDeviceType()==1)){
            List<FileSystemVo> fileSystemVos=this.getFileSystem(ip);
            String message="";
            if(fileSystemVos==null||fileSystemVos.size()==0){
                message="SNMP或者代理无法采集磁盘信息";
                alarmLogDto.setMessage(message);
                alarmLogDto.setIp(ip);
                alarmLogDto.setDeviceName("磁盘使用率");
                alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
                alarmLogDto.setMonitorType(MonitorTypeEnum.PRCESS.getValue());
                alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto,-1);
                return;
            }
            for(FileSystemVo fileSystemVo:fileSystemVos){
                message="磁盘使用率到达阈值条件当前为"+fileSystemVo.getUsage()+"%";
                alarmLogDto.setMessage(message);
                alarmLogDto.setIp(ip);
                alarmLogDto.setDeviceName(fileSystemVo.getDiskName());
                alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
                alarmLogDto.setMonitorType(MonitorTypeEnum.PRCESS.getValue());
                alarmLogService.checkAndInsert(alarmConfigDto,alarmLogDto, (float) fileSystemVo.getUsage());
            }

        }
    }

    public float selectCpu(String ip){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.cpu");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", ip);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);

        AvgAggregationBuilder avgCpu = AggregationBuilders.avg("usage");
        avgCpu.field("system.cpu.total.norm.pct");
        avgCpu.format("0.0000");

        searchSourceBuilder.aggregation(avgCpu);

        searchSourceBuilder.size(1);
        SearchResponse searchResponse = null;
        float usage=-1;
        try {
            searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedAvg parsedAvg=aggregations.get("usage");
            String usageString=parsedAvg.getValueAsString();
            if(!usageString.equals("Infinity")){
                usage=Float.parseFloat(usageString)*100;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return usage;
    }
    public float selectMemory(String ip){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.memory");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", ip);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);

        AvgAggregationBuilder avgCpu = AggregationBuilders.avg("usage");
        avgCpu.field("system.memory.used.pct");
        avgCpu.format("0.0000");

        searchSourceBuilder.aggregation(avgCpu);

        searchSourceBuilder.size(1);
        SearchResponse searchResponse = null;
        float usage=-1;
        try {
            searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedAvg parsedAvg=aggregations.get("usage");
            String usageString=parsedAvg.getValueAsString();
            if(!usageString.equals("Infinity")){
                usage=Float.parseFloat(usageString)*100;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return usage;
    }
    @SneakyThrows
    public List<FileSystemVo> getFileSystem(String ip) {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime=format.format(calendar.getTime());
        List<FileSystemVo> fileSystemVos = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.filesystem");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", ip);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        MaxAggregationBuilder maxTime = AggregationBuilders.max("@timestamp");
        maxTime.field("@timestamp");
        searchSourceBuilder.aggregation(maxTime);

        SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedMax parsedMax = aggregations.get("@timestamp");
        if (parsedMax != null) {
            String timestamp = parsedMax.getValueAsString();
            BoolQueryBuilder boolFileBuilder = QueryBuilders.boolQuery();
            QueryBuilder queryBuilder = QueryBuilders.termQuery("@timestamp", timestamp);
            WildcardQueryBuilder wildDocker = QueryBuilders.wildcardQuery("system.filesystem.mount_point", "*docker*");
            WildcardQueryBuilder wildKubernetes = QueryBuilders.wildcardQuery("system.filesystem.mount_point", "*kubernetes*");
            boolFileBuilder.must(queryBuilder);
            boolFileBuilder.must(matchEvent);
            boolFileBuilder.must(matchIp);
            boolFileBuilder.mustNot(wildDocker);
            boolFileBuilder.mustNot(wildKubernetes);
            SearchSourceBuilder fileSearch = new SearchSourceBuilder();
            fileSearch.query(boolFileBuilder);
            String[] fields = new String[]{"system.filesystem.mount_point", "system.filesystem.free", "system.filesystem.used.pct"};
            fileSearch.fetchSource(fields, null);
            fileSearch.size(1000);
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", fileSearch);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                FileSystemVo fileSystemVo = new FileSystemVo();
                Map<String, Object> map = hit.getSourceAsMap();
                Map<String, Object> system = (Map<String, Object>) map.get("system");
                Map<String, Object> filesystem = (Map<String, Object>) system.get("filesystem");
                Map<String, Object> used = (Map<String, Object>) filesystem.get("used");
                fileSystemVo.setDiskName((String) filesystem.get("mount_point"));
                fileSystemVo.setFree(new BigDecimal(String.valueOf(filesystem.get("free"))).divide(new BigDecimal(1024 * 1024 * 1024), 4, RoundingMode.HALF_UP).doubleValue());
                fileSystemVo.setUsage(new BigDecimal(String.valueOf(used.get("pct"))).doubleValue()*100);
                fileSystemVos.add(fileSystemVo);
            }

        }


        return fileSystemVos;

    }

    public float selectProcess(ProcessConfigDto processConfigDto){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.process");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", processConfigDto.getIp());
        WildcardQueryBuilder wild = QueryBuilders.wildcardQuery("system.process.cmdline", "*"+processConfigDto.getProcessName()+"*");
        boolBuilder.must(wild);
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
            if(searchHits.length>0){
                Set<String> stringSet=new HashSet<>();
                for (SearchHit hit : searchHits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    Map<String, Object> system = (Map<String, Object>) map.get("system");
                    Map<String, Object> process = (Map<String, Object>) system.get("process");
                    Map<String, Object> cpu = (Map<String, Object>) process.get("cpu");
                    Map<String, Object> total = (Map<String, Object>) cpu.get("total");
                    stringSet.add(String.valueOf(total.get("value")));
                }
                usage=stringSet.size();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return usage;

    }

    public float selectPing(String ip){
        float usage=-1;
        try {
           ResultT<String> resultT= PingUtil.ping(ip);
           if(!resultT.isSuccess()){
               return usage;
           }
           Pattern pattern = Pattern.compile("[\\w\\W]*丢失 = ([0-9]\\d*)[\\w\\W]*");
           Matcher matcher = pattern.matcher(resultT.getData());
           while (matcher.find()) {
             usage=Float.parseFloat(matcher.group(1));
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usage;

    }
}
*/
