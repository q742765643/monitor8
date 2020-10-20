package com.piesat.skywalking.service.report;

import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.report.OverviewQService;
import com.piesat.skywalking.dto.OverviewDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.util.JsonParseUtil;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : OverviewServiceImpl
 * @Description : 预览
 * @Author : zzj
 * @Date: 2020-10-19 17:56
 */
@Service
public class OverviewQServiceImpl implements OverviewQService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    public List<OverviewDto> getOverview(){
        long time=System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String endTime= dateFormat.format(time);
        String starTime= dateFormat.format(time-6000000);
        SystemQueryDto systemQueryDto=new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        List<OverviewDto> overviewDtos=this.getOverviewEs(systemQueryDto);
        return overviewDtos;
    }

    public List<OverviewDto> getOverviewEs(SystemQueryDto systemQueryDto) {
        List<OverviewDto> overviewDtos=new ArrayList<>();
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_MEDIA_OVERVIEW, search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                OverviewDto overviewDto=new OverviewDto();
                BigDecimal avgCpuPct=new BigDecimal(String.valueOf(jsonMap.get("avg.cpu.pct")));
                BigDecimal avgmemoryPct=new BigDecimal(String.valueOf(jsonMap.get("avg.memory.pct")));
                BigDecimal filesystemPct=new BigDecimal(String.valueOf(jsonMap.get("filesystem.pct")));
                overviewDto.setAvgCpuPct(avgCpuPct.floatValue());
                overviewDto.setAvgMemoryPct(avgmemoryPct.floatValue());
                overviewDto.setFilesystemPct(filesystemPct.floatValue());
                BigDecimal filesystemSize= new BigDecimal(String.valueOf(jsonMap.get("filesystem.size"))) ;
                BigDecimal cpuCores= new BigDecimal(String.valueOf(jsonMap.get("cpu.cores")));
                BigDecimal memoryTotal= new BigDecimal(String.valueOf(jsonMap.get("memory.total")));
                BigDecimal processSize= new BigDecimal(String.valueOf(jsonMap.get("process.size")));
                BigDecimal filesystemUse= new BigDecimal(String.valueOf(jsonMap.get("filesystem.use.size")));
                overviewDto.setFilesystemSize(filesystemSize.divide(new BigDecimal(1024*1024*1024)).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setCpuCores(cpuCores.intValue());
                overviewDto.setOnline(Integer.parseInt(String.valueOf(jsonMap.get("online"))));
                overviewDto.setIp(String.valueOf(jsonMap.get("ip")));
                overviewDto.setMemoryTotal(memoryTotal.divide(new BigDecimal(1024*1024*1024)).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setProcessSize(processSize.intValue());
                overviewDto.setFilesystemUse(filesystemUse.divide(new BigDecimal(1024*1024*1024)).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
                overviewDto.setFilesystemSize(filesystemSize.divide(new BigDecimal(1024*1024*1024)).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
                if(cpuCores.compareTo(BigDecimal.ZERO)>0){
                    overviewDto.setCpuUse(new BigDecimal(overviewDto.getAvgCpuPct()).multiply(cpuCores).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());
                }
                if(memoryTotal.compareTo(BigDecimal.ZERO)>0){
                    overviewDto.setMemoryUse(new BigDecimal(overviewDto.getAvgMemoryPct()).multiply(new BigDecimal(overviewDto.getMemoryTotal())).setScale(4,BigDecimal.ROUND_HALF_UP).floatValue());
                }
                overviewDtos.add(overviewDto);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return overviewDtos;
    }
    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }
}

