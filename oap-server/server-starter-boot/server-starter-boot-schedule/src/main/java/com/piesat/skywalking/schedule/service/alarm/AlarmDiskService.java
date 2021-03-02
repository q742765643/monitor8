package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.skywalking.vo.FileSystemVo;
import com.piesat.util.ResultT;
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
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : AlarmDiskService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-02 11:08
 */
@Service
public class AlarmDiskService extends AlarmBaseService {
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto = (AlarmConfigDto) jobContext.getHtJobInfoDto();
        List<HostConfigDto> hostConfigDtos = this.selectAvailable();

        for (int i = 0; i < hostConfigDtos.size(); i++) {
            AlarmLogDto alarmLogDto = new AlarmLogDto();
            HostConfigDto hostConfigDto = hostConfigDtos.get(i);
            if (0 != hostConfigDto.getDeviceType()) {
                continue;
            }
            this.toAlarm(alarmLogDto, alarmConfigDto, hostConfigDto);

        }
    }

    public void toAlarm(AlarmLogDto alarmLogDto, AlarmConfigDto alarmConfigDto, HostConfigDto hostConfigDto) {
        List<FileSystemVo> fileSystemVos = this.findDiskList(hostConfigDto.getIp());
        alarmLogDto.setIp(hostConfigDto.getIp());
        alarmLogDto.setRelatedId(hostConfigDto.getId());
        alarmLogDto.setMediaType(hostConfigDto.getMediaType());
        alarmLogDto.setDeviceType(hostConfigDto.getDeviceType());
        if(null==fileSystemVos||fileSystemVos.size()==0){
            alarmLogDto.setUsage(-1f);
            this.fitAlarmLog(alarmConfigDto, alarmLogDto);
            this.judgeAlarm(alarmLogDto);
            if (alarmLogDto.isAlarm()) {
                String message = hostConfigDto.getIp()+":未采集到磁盘使用率,请检查环境";
                alarmLogDto.setMessage(message);
                if(1==alarmConfigDto.getTriggerStatus()){
                    this.insertEs(alarmLogDto);
                }
            }
            if(1==alarmConfigDto.getTriggerStatus()) {
                this.insertUnprocessed(alarmLogDto);
            }
        }else {
            for(int i=0;i<fileSystemVos.size();i++){
                alarmLogDto.setUsage(fileSystemVos.get(i).getUsage().floatValue());
                this.fitAlarmLog(alarmConfigDto, alarmLogDto);
                this.judgeAlarm(alarmLogDto);
                if (alarmLogDto.isAlarm()) {
                    String message =  hostConfigDto.getIp()+":"+fileSystemVos.get(i).getDiskName()+"磁盘使用率到达" + new BigDecimal(alarmLogDto.getUsage()).setScale(2,BigDecimal.ROUND_HALF_UP)  + "%";
                    alarmLogDto.setMessage(message);
                    if(1==alarmConfigDto.getTriggerStatus()) {
                        this.insertEs(alarmLogDto);
                    }
                }
                if(1==alarmConfigDto.getTriggerStatus()) {
                    this.insertUnprocessed(alarmLogDto);
                }
            }

        }

    }

    public List<FileSystemVo> findDiskList(String ip) {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10);
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

        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedMax parsedMax = aggregations.get("@timestamp");
            if (parsedMax != null) {
                String timestamp = parsedMax.getValueAsString();
                if("-Infinity".equals(timestamp)){
                    return fileSystemVos;
                }
                BoolQueryBuilder boolFileBuilder = QueryBuilders.boolQuery();
                QueryBuilder queryBuilder = QueryBuilders.termQuery("@timestamp", timestamp);
                //WildcardQueryBuilder wildDocker = QueryBuilders.wildcardQuery("system.filesystem.mount_point", "*docker*");
                //WildcardQueryBuilder wildKubernetes = QueryBuilders.wildcardQuery("system.filesystem.mount_point", "*kubernetes*");
                boolFileBuilder.must(queryBuilder);
                boolFileBuilder.must(matchEvent);
                boolFileBuilder.must(matchIp);
                //boolFileBuilder.mustNot(wildDocker);
                //boolFileBuilder.mustNot(wildKubernetes);
                SearchSourceBuilder fileSearch = new SearchSourceBuilder();
                fileSearch.query(boolFileBuilder);
                String[] fields = new String[]{"system.filesystem.mount_point", "system.filesystem.free", "system.filesystem.used.pct","system.filesystem.used.bytes"};
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
                    if(fileSystemVo.getDiskName().indexOf("/root")!=-1){
                        continue;
                    }
                    fileSystemVo.setFree(new BigDecimal(String.valueOf(filesystem.get("free"))).divide(new BigDecimal(1024 * 1024 * 1024), 4, BigDecimal.ROUND_HALF_UP));
                    fileSystemVo.setUsage((new BigDecimal(String.valueOf(used.get("pct"))).multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)));
                    fileSystemVos.add(fileSystemVo);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return fileSystemVos;

    }

}

