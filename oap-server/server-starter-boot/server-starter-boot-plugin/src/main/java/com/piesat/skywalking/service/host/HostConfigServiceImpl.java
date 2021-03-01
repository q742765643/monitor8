package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dao.HostConfigDao;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.mapper.HostConfigMapper;
import com.piesat.skywalking.mapstruct.HostConfigMapstruct;
import com.piesat.skywalking.service.quartz.timing.HostConfigQuartzService;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import lombok.SneakyThrows;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HostConfigServiceImpl extends BaseService<HostConfigEntity> implements HostConfigService {
    @Autowired
    private HostConfigDao hostConfigDao;
    @Autowired
    private HostConfigQuartzService hostConfigQuartzService;
    @Autowired
    private HostConfigMapstruct hostConfigMapstruct;
    @Autowired
    private HostConfigMapper hostConfigMapper;
    @Autowired
    private AlarmEsLogService alarmEsLogService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @Override
    public BaseDao<HostConfigEntity> getBaseDao() {
        return hostConfigDao;
    }

    public PageBean selectPageList(PageForm<HostConfigDto> pageForm) {
        HostConfigEntity host = hostConfigMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(host.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(), host.getIp());
        }
        if (StringUtils.isNotNullString(host.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), host.getTaskName());
        }
        if (StringUtils.isNotNullString((String) host.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) host.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) host.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) host.getParamt().get("endTime"));
        }
        if (null != host.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), host.getTriggerStatus());
        }
        if (StringUtils.isNotNullString(host.getOs())) {
            specificationBuilder.add("os", SpecificationOperator.Operator.likeAll.name(), host.getOs());
        }
        if (null != host.getMediaType()) {
            specificationBuilder.add("mediaType", SpecificationOperator.Operator.eq.name(), host.getMediaType());
        }
        if (null != host.getCurrentStatus()) {
            specificationBuilder.add("currentStatus", SpecificationOperator.Operator.eq.name(), host.getCurrentStatus());
        }
        if (null != host.getDeviceType()) {
            specificationBuilder.add("deviceType", SpecificationOperator.Operator.eq.name(), host.getDeviceType());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.ASC, "ip");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        pageBean.setPageData(hostConfigMapstruct.toDto(pageBean.getPageData()));
        return pageBean;

    }

    public List<HostConfigDto> selectBySpecification(HostConfigDto hostConfigdto) {
        HostConfigEntity hostConfig = hostConfigMapstruct.toEntity(hostConfigdto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(hostConfig.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(), hostConfig.getIp());
        }
        if (StringUtils.isNotNullString(hostConfig.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), hostConfig.getTaskName());
        }
        if (StringUtils.isNotNullString((String) hostConfig.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) hostConfig.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) hostConfig.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) hostConfig.getParamt().get("endTime"));
        }
        if (null != hostConfig.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), hostConfig.getTriggerStatus());
        }
        if (StringUtils.isNotNullString(hostConfig.getOs())) {
            specificationBuilder.add("os", SpecificationOperator.Operator.likeAll.name(), hostConfig.getOs());
        }
        if (null != hostConfig.getMediaType()) {
            specificationBuilder.add("mediaType", SpecificationOperator.Operator.eq.name(), hostConfig.getMediaType());
        }
        if (null != hostConfig.getDeviceType()) {
            specificationBuilder.add("deviceType", SpecificationOperator.Operator.eq.name(), hostConfig.getDeviceType());
        }
        if (null != hostConfig.getCurrentStatus()) {
            specificationBuilder.add("currentStatus", SpecificationOperator.Operator.eq.name(), hostConfig.getCurrentStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities = this.getAll(specification);
        return hostConfigMapstruct.toDto(hostConfigEntities);
    }

    @Transactional
    public HostConfigDto save(HostConfigDto hostConfigDto) {
        if (hostConfigDto.getTriggerType() == null) {
            hostConfigDto.setTriggerType(0);
        }
        if (hostConfigDto.getTriggerStatus() == null) {
            hostConfigDto.setTriggerStatus(1);
        }
        if (hostConfigDto.getIsHost() == null) {
            hostConfigDto.setIsHost(0);
        }
        hostConfigDto.setIsUt(0);
        hostConfigDto.setDelayTime(0);
        hostConfigDto.setJobHandler("hostConfigHandler");
        HostConfigEntity hostConfig = hostConfigMapstruct.toEntity(hostConfigDto);
        HostConfigEntity hostConfigNew = super.saveNotNull(hostConfig);
        if(hostConfigNew.getMonitoringMethods()==2){
            hostConfigQuartzService.handleJob(hostConfigMapstruct.toDto(hostConfigNew));
        }
        return hostConfigMapstruct.toDto(hostConfig);
    }
    public HostConfigDto updateHost(HostConfigDto hostConfigDto) {
        HostConfigEntity hostConfig = hostConfigMapstruct.toEntity(hostConfigDto);
        HostConfigEntity hostConfigNew  = super.saveNotNull(hostConfig);
        if(null!=hostConfigNew.getMonitoringMethods()){
            if(hostConfigNew.getMonitoringMethods()==2){
                hostConfigQuartzService.handleJob(hostConfigMapstruct.toDto(hostConfigNew));
            }else{
                hostConfig.setTriggerStatus(0);
                hostConfigQuartzService.handleJob(hostConfigMapstruct.toDto(hostConfigNew));
            }
        }

        return hostConfigMapstruct.toDto(hostConfig);
    }

    public List<HostConfigDto> selectAll() {
        Sort sort = Sort.by("area");
        List<HostConfigEntity> list = super.getAll(sort);
        return hostConfigMapstruct.toDto(list);
    }
    public List<HostConfigDto> selectAllOrderByIp() {
        Sort sort = Sort.by("ip");
        List<HostConfigEntity> list = super.getAll(sort);
        return hostConfigMapstruct.toDto(list);
    }

    @Override
    public HostConfigDto findById(String id) {
        return hostConfigMapstruct.toDto(super.getById(id));
    }

    @SneakyThrows
    @Transactional
    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
        hostConfigQuartzService.deleteJob(ids);
        alarmEsLogService.deleteAlarm(ids);
    }

    public List<String> selectOnine() {
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        specificationBuilder.add("monitoringMethods", SpecificationOperator.Operator.eq.name(), 1);
        specificationBuilder.addOr("monitoringMethods", SpecificationOperator.Operator.eq.name(), 2);
        Specification specification = specificationBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities = this.getAll(specification);
        List<String> ips = new ArrayList<>();
        for (int i = 0; i < hostConfigEntities.size(); i++) {
            ips.add(hostConfigEntities.get(i).getIp());
        }
        return ips;

    }

    public List<HostConfigDto> selectOnineAll() {
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), 1);
        Specification specification = specificationBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities = this.getAll(specification);
        return hostConfigMapstruct.toDto(hostConfigEntities);
    }

    public List<HostConfigDto> selectAvailable() {
        SimpleSpecificationBuilder triggerStatusBuilder = new SimpleSpecificationBuilder();
        triggerStatusBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), 1);
        SimpleSpecificationBuilder monitoringBuilder = new SimpleSpecificationBuilder();
        monitoringBuilder.add("monitoringMethods", SpecificationOperator.Operator.eq.name(), 1);
        monitoringBuilder.addOr("monitoringMethods", SpecificationOperator.Operator.eq.name(), 2);
        Specification specification = triggerStatusBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities = this.getAll(specification.and(monitoringBuilder.generateSpecification()));
        return hostConfigMapstruct.toDto(hostConfigEntities);
    }

    public long selectCount(HostConfigDto hostConfigdto) {
        HostConfigEntity hostConfig = hostConfigMapstruct.toEntity(hostConfigdto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(hostConfig.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(), hostConfig.getIp());
        }
        if (StringUtils.isNotNullString(hostConfig.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), hostConfig.getTaskName());
        }
        if (StringUtils.isNotNullString((String) hostConfig.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) hostConfig.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) hostConfig.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) hostConfig.getParamt().get("endTime"));
        }
        if (null != hostConfig.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), hostConfig.getTriggerStatus());
        }
        if (StringUtils.isNotNullString(hostConfig.getOs())) {
            specificationBuilder.add("os", SpecificationOperator.Operator.likeAll.name(), hostConfig.getOs());
        }
        if (null != hostConfig.getMediaType()) {
            specificationBuilder.add("mediaType", SpecificationOperator.Operator.eq.name(), hostConfig.getMediaType());
        }
        if (null != hostConfig.getDeviceType()) {
            specificationBuilder.add("deviceType", SpecificationOperator.Operator.eq.name(), hostConfig.getDeviceType());
        }
        if (null != hostConfig.getCurrentStatus()) {
            specificationBuilder.add("currentStatus", SpecificationOperator.Operator.eq.name(), hostConfig.getCurrentStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        return super.count(specification);
    }
    public List<Map<String,Object>> findStateStatistics(){
        List<Map<String, Object>> mapList=hostConfigMapper.findStateStatistics();
        if(null!=mapList&&mapList.size()>0){
            for(Map<String,Object> map:mapList){
                String name=String.valueOf(map.get("name"));
                if("11".equals(name)){
                    map.put("name","未知");
                    map.put("color1","#FC000D");
                    map.put("color2","#E10008");
                }
                if("0".equals(name)){
                    map.put("name","一般");
                    map.put("color1","#E4A302");
                    map.put("color2","#FDF901");
                }
                if("1".equals(name)){
                    map.put("name","危险");
                    map.put("color1","#329A2E");
                    map.put("color2","#5DFC57");
                }
                if("2".equals(name)){
                    map.put("name","故障");
                    map.put("color1","#0063F2");
                    map.put("color2","#0065F5");
                }
                if("3".equals(name)){
                    map.put("name","正常");
                    map.put("color1","#FF00FF");
                    map.put("color2","#FF00FF");
                }
            }
        }
        return mapList;
    }

    public void trigger(String id){
        hostConfigQuartzService.trigger(this.findById(id));
    }

    public void getUpdateTime(HostConfigDto hostConfigDto){
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.uptime");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", hostConfigDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        search.query(boolBuilder);
        search.size(1);
        search.fetchSource(new String[]{"system.uptime.duration.ms"}, null);
        search.sort("@timestamp", SortOrder.DESC);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                long hours = new BigDecimal(String.valueOf(jsonMap.get("system.uptime.duration.ms"))).divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP).longValue();
                hostConfigDto.setMaxUptime(hours);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getPacketLoss(HostConfigDto hostConfigDto){
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.packet");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", hostConfigDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        search.query(boolBuilder);
        search.size(1);
        search.fetchSource(new String[]{"loss"}, null);
        search.sort("@timestamp", SortOrder.DESC);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                float loss = new BigDecimal(String.valueOf(hit.getSourceAsMap().get("loss"))).floatValue();
                hostConfigDto.setPacketLoss(loss*100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
