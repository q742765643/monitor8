package com.piesat.skywalking.service.job;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.skywalking.dao.JobInfoDao;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.mapper.JobInfoMapper;
import com.piesat.skywalking.mapstruct.HtJobInfoMapstruct;
import com.piesat.skywalking.model.HtJobInfo;
import com.piesat.skywalking.service.quartz.timing.JobInfoQuartzService;
import com.piesat.skywalking.service.timing.JobInfoService;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName : CommonJobService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-15 18:01
 */
@Service
public class JobServiceImpl extends BaseService<HtJobInfo> implements JobInfoService {
    @Autowired
    private JobInfoDao jobInfoDao;
    @Autowired
    private HtJobInfoMapstruct htJobInfoMapstruct;
    @Autowired
    private JobInfoQuartzService jobInfoQuartzService;
    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Override
    public BaseDao<HtJobInfo> getBaseDao() {
        return jobInfoDao;
    }

    public PageBean selectPageList(PageForm<HtJobInfoDto> pageForm) {
        HtJobInfo htJobInfo = htJobInfoMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(), pageForm.getPageSize());
        List<HtJobInfo> htJobInfos = jobInfoMapper.selectList(htJobInfo);
        PageInfo<HtJobInfo> pageInfo = new PageInfo<>(htJobInfos);
        List<HtJobInfoDto> htJobInfoDtos = htJobInfoMapstruct.toDto(pageInfo.getList());
        PageBean pageBean = new PageBean(pageInfo.getTotal(), pageInfo.getPages(), htJobInfoDtos);
        return pageBean;

    }

    public List<HtJobInfoDto> selectBySpecification(HtJobInfoDto htJobInfoDto) {
        HtJobInfo htJobInfo = htJobInfoMapstruct.toEntity(htJobInfoDto);
        List<HtJobInfo> htJobInfos = jobInfoMapper.selectList(htJobInfo);
        return htJobInfoMapstruct.toDto(htJobInfos);
    }

    @Transactional
    public HtJobInfoDto save(HtJobInfoDto htJobInfoDto) {
        if (htJobInfoDto.getTriggerType() == null) {
            htJobInfoDto.setTriggerType(0);
        }
        if (htJobInfoDto.getTriggerStatus() == null) {
            htJobInfoDto.setTriggerStatus(1);
        }
        htJobInfoDto.setIsUt(0);
        htJobInfoDto.setDelayTime(0);
        HtJobInfo htJobInfo = htJobInfoMapstruct.toEntity(htJobInfoDto);
        htJobInfo = super.saveNotNull(htJobInfo);
        jobInfoQuartzService.handleJob(htJobInfoMapstruct.toDto(htJobInfo));
        return htJobInfoMapstruct.toDto(htJobInfo);
    }


    @Override
    public HtJobInfoDto findById(String id) {
        return htJobInfoMapstruct.toDto(super.getById(id));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
        jobInfoQuartzService.deleteJob(ids);
    }
}

