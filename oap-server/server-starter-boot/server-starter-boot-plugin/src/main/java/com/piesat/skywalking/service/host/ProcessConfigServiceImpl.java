package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dao.ProcessConfigDao;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.ProcessDetailsDto;
import com.piesat.skywalking.entity.ProcessConfigEntity;
import com.piesat.skywalking.mapstruct.ProcessConfigMapstruct;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.NullUtil;
import com.piesat.util.StringUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
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
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProcessConfigServiceImpl extends BaseService<ProcessConfigEntity> implements ProcessConfigService {
    @Autowired
    private ProcessConfigDao processConfigDao;
    @Autowired
    private ProcessConfigMapstruct processConfigMapstruct;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private AlarmEsLogService alarmEsLogService;
    @Autowired
    private HostConfigService hostConfigService;

    @Override
    public BaseDao<ProcessConfigEntity> getBaseDao() {
        return processConfigDao;
    }

    public PageBean selectPageList(PageForm<ProcessConfigDto> pageForm) {
        ProcessConfigEntity process = processConfigMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(process.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(), process.getIp());
        }
        if (StringUtils.isNotNullString(process.getProcessName())) {
            specificationBuilder.addOr("processName", SpecificationOperator.Operator.likeAll.name(), process.getProcessName());
        }
        if (StringUtils.isNotNullString((String) process.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) process.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) process.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) process.getParamt().get("endTime"));
        }
        if (StringUtil.isNotEmpty(process.getHostId())) {
            specificationBuilder.add("hostId", SpecificationOperator.Operator.eq.name(), process.getHostId());
        }
        if (null != process.getCurrentStatus() && process.getCurrentStatus() > -1) {
            specificationBuilder.add("currentStatus", SpecificationOperator.Operator.eq.name(), process.getCurrentStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<ProcessConfigDto> selectBySpecification(ProcessConfigDto processConfigDto) {
        ProcessConfigEntity process = processConfigMapstruct.toEntity(processConfigDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(process.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.eq.name(), process.getIp());
        }
        if (StringUtils.isNotNullString(process.getProcessName())) {
            specificationBuilder.addOr("processName", SpecificationOperator.Operator.likeAll.name(), process.getProcessName());
        }
        if (StringUtils.isNotNullString((String) process.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) process.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) process.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) process.getParamt().get("endTime"));
        }
        if (StringUtil.isNotEmpty(process.getHostId())) {
            specificationBuilder.add("hostId", SpecificationOperator.Operator.eq.name(), process.getHostId());
        }
        if (null != process.getCurrentStatus() && process.getCurrentStatus() > -1) {
            specificationBuilder.add("currentStatus", SpecificationOperator.Operator.eq.name(), process.getCurrentStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<ProcessConfigEntity> processConfigEntities = this.getAll(specification);
        return processConfigMapstruct.toDto(processConfigEntities);
    }

    @Override
    @Transactional
    public ProcessConfigDto save(ProcessConfigDto processConfigDto) {
        if(null==processConfigDto.getCurrentStatus()){
            processConfigDto.setCurrentStatus(11);
        }
        ProcessConfigEntity processConfigEntity = processConfigMapstruct.toEntity(processConfigDto);
        processConfigEntity = super.saveNotNull(processConfigEntity);
        return processConfigDto;
    }

    @Override
    public ProcessConfigDto findById(String id) {
        return processConfigMapstruct.toDto(super.getById(id));
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
        alarmEsLogService.deleteAlarm(ids);
    }

    public ProcessDetailsDto getDetail(ProcessConfigDto processConfigDto) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime = format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -5);
        String beginTime = format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder agentType = QueryBuilders.matchQuery("agent.type", "metricbeat");
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.process");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", processConfigDto.getIp());
        WildcardQueryBuilder wild = QueryBuilders.wildcardQuery("system.process.cmdline", "*" + processConfigDto.getProcessName() + "*");
        boolBuilder.must(wild);
        boolBuilder.must(agentType);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(1);
        searchSourceBuilder.sort("@timestamp", SortOrder.DESC);

        try {
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));

            if (searchHits.length > 0) {
                ProcessDetailsDto processDetailsDto = new ProcessDetailsDto();
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, searchHits[0].getSourceAsString(), null);
                processDetailsDto.setPid(String.valueOf(jsonMap.get("process.pid")));
                processDetailsDto.setWorkingDirectory(String.valueOf(jsonMap.get("process.working_directory")));
                processDetailsDto.setCmdline(String.valueOf(jsonMap.get("system.process.cmdline")));
                processDetailsDto.setStartTime(JsonParseUtil.formateDate((String) jsonMap.get("system.process.cpu.start_time")));
                processDetailsDto.setCpuTime(new BigDecimal(String.valueOf(jsonMap.get("system.process.cpu.total.value"))).longValue());
                processDetailsDto.setCpuUsage(new BigDecimal(String.valueOf(jsonMap.get("system.process.cpu.total.norm.pct"))).floatValue() * 100);
                processDetailsDto.setMemoryBytes(new BigDecimal(String.valueOf(jsonMap.get("system.process.memory.rss.bytes"))).longValue());
                processDetailsDto.setMemoryUsage(new BigDecimal(String.valueOf(jsonMap.get("system.process.memory.rss.pct"))).floatValue() * 100);
                processDetailsDto.setUserName(String.valueOf(jsonMap.get("user.name")));
                return processDetailsDto;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }

    public long selectCount(ProcessConfigDto processConfigDto) {
        ProcessConfigEntity process = processConfigMapstruct.toEntity(processConfigDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(process.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.eq.name(), process.getIp());
        }
        if (StringUtils.isNotNullString(process.getProcessName())) {
            specificationBuilder.addOr("processName", SpecificationOperator.Operator.likeAll.name(), process.getProcessName());
        }
        if (StringUtils.isNotNullString((String) process.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) process.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) process.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) process.getParamt().get("endTime"));
        }
        if (StringUtil.isNotEmpty(process.getHostId())) {
            specificationBuilder.add("hostId", SpecificationOperator.Operator.eq.name(), process.getHostId());
        }
        if (null != process.getCurrentStatus() && process.getCurrentStatus() > -1) {
            specificationBuilder.add("currentStatus", SpecificationOperator.Operator.eq.name(), process.getCurrentStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        return super.count(specification);
    }

    public List<HostConfigDto> findIp(){
        HostConfigDto hostConfigdto=new HostConfigDto();
        NullUtil.changeToNull(hostConfigdto);
        hostConfigdto.setDeviceType(0);
        List<HostConfigDto> hostConfigDtos=hostConfigService.selectBySpecification(hostConfigdto);
        return hostConfigDtos;
    }
}
