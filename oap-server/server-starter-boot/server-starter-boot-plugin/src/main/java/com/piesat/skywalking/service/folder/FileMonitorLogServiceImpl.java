package com.piesat.skywalking.service.folder;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.folder.FileMonitorLogService;
import com.piesat.skywalking.dao.FileMonitorLogDao;
import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.entity.FileMonitorLogEntity;
import com.piesat.skywalking.mapstruct.FileMonitorLogMapstruct;
import com.piesat.util.JsonParseUtil;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : FileMonitorLogServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-26 09:52
 */
@Service
public class FileMonitorLogServiceImpl extends BaseService<FileMonitorLogEntity> implements FileMonitorLogService {
    @Autowired
    private FileMonitorLogDao fileMonitorLogDao;
    @Autowired
    private FileMonitorLogMapstruct fileMonitorLogMapstruct;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @Override
    public BaseDao<FileMonitorLogEntity> getBaseDao() {
        return fileMonitorLogDao;
    }

    public PageBean selectPageList(PageForm<FileMonitorLogDto> pageForm) {
        FileMonitorLogEntity fileMonitorLogEntity = fileMonitorLogMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        Specification specification = specificationBuilder.generateSpecification();
        if (StringUtils.isNotNullString(fileMonitorLogEntity.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), fileMonitorLogEntity.getTaskName());
        }
        if (StringUtils.isNotNullString((String) fileMonitorLogEntity.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) fileMonitorLogEntity.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) fileMonitorLogEntity.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) fileMonitorLogEntity.getParamt().get("endTime"));
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        pageBean.setPageData(fileMonitorLogMapstruct.toDto(pageBean.getPageData()));
        return pageBean;

    }

    public PageBean selectPageListDetail(PageForm<FileMonitorLogDto> pageForm) {
        PageBean<FileStatisticsDto> pageBean=new PageBean<>();
        FileMonitorLogEntity query = fileMonitorLogMapstruct.toEntity(pageForm.getT());
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
        if (StringUtils.isNotNullString((String) query.getParamt().get("beginTime"))) {
            rangeQueryBuilder.gte((String) query.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) query.getParamt().get("endTime"))) {
            rangeQueryBuilder.lte((String) query.getParamt().get("endTime"));
        }
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        if (StringUtils.isNotEmpty(query.getTaskName())) {
            WildcardQueryBuilder taskName = QueryBuilders.wildcardQuery("task_name", "*" + query.getTaskName() + "*");
            boolBuilder.must(taskName);
        }
        if (StringUtils.isNotEmpty(query.getTaskId())) {
            WildcardQueryBuilder taskId = QueryBuilders.wildcardQuery("task_id", query.getTaskId());
            boolBuilder.must(taskId);
        }
        search.query(boolBuilder).sort("start_time_l", SortOrder.DESC);
        search.trackTotalHits(true);
        try {
            int startIndex = (pageForm.getCurrentPage() - 1) * pageForm.getPageSize();
            search.from(startIndex);
            search.size(pageForm.getPageSize());
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            long count = hits.getTotalHits().value;
            List<FileStatisticsDto> fileStatisticsDtos=new ArrayList<>();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> map = hit.getSourceAsMap();
                FileStatisticsDto fileStatisticsDto=new FileStatisticsDto();
                fileStatisticsDto.setId(String.valueOf(map.get("id")));
                fileStatisticsDto.setTaskId(String.valueOf(map.get("task_id")));
                fileStatisticsDto.setTaskName(String.valueOf(map.get("task_name")));
                fileStatisticsDto.setStartTimeL((Long) map.get("start_time_l"));

                fileStatisticsDto.setDdataTime(JsonParseUtil.formateToDate((String) map.get("d_data_time")));
                String status=String.valueOf(map.get("status"));
                if(StringUtil.isEmpty(status)||"null".equals(status)){
                    status="4";
                }
                fileStatisticsDto.setStatus(Integer.parseInt(status));
                fileStatisticsDto.setFileNum(new BigDecimal(String.valueOf(map.get("file_num"))).longValue());
                fileStatisticsDto.setRealFileNum(new BigDecimal(String.valueOf(map.get("real_file_num"))).longValue());
                fileStatisticsDto.setLateNum(new BigDecimal(String.valueOf(map.get("late_num"))).longValue());
                fileStatisticsDtos.add(fileStatisticsDto);
            }
            pageBean.setPageData(fileStatisticsDtos);
            pageBean.setTotalCount(count);
        }catch (Exception e){
               e.printStackTrace();
        }
        return pageBean;

    }

    @Override
    @Transactional
    public FileMonitorLogDto save(FileMonitorLogDto fileMonitorLogDto) {

        FileMonitorLogEntity fileMonitorLogEntity = fileMonitorLogMapstruct.toEntity(fileMonitorLogDto);
        fileMonitorLogEntity = super.saveNotNull(fileMonitorLogEntity);
        return fileMonitorLogMapstruct.toDto(fileMonitorLogEntity);
    }

    @Override
    public FileMonitorLogDto findById(String id) {
        return fileMonitorLogMapstruct.toDto(super.getById(id));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
    }
}

