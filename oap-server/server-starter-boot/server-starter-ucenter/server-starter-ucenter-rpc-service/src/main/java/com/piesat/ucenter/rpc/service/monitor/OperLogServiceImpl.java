package com.piesat.ucenter.rpc.service.monitor;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.dao.monitor.OperLogDao;
import com.piesat.ucenter.entity.monitor.OperLogEntity;
import com.piesat.ucenter.entity.system.DictDataEntity;
import com.piesat.ucenter.entity.system.UserEntity;
import com.piesat.ucenter.mapper.monitor.OperLogMapper;
import com.piesat.ucenter.rpc.api.monitor.OperLogService;
import com.piesat.ucenter.rpc.dto.monitor.OperLogDto;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.mapstruct.monitor.OperLogMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-16 15:07
 **/
@Service
public class OperLogServiceImpl extends BaseService<OperLogEntity> implements OperLogService{
    @Autowired
    private OperLogMapstruct operLogMapstruct;
    @Autowired
    private OperLogDao operLogDao;
    @Autowired
    private OperLogMapper operLogMapper;
    @Override
    public BaseDao<OperLogEntity> getBaseDao() {
        return operLogDao;
    }
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(OperLogDto operLog)
    {
        OperLogEntity operLogEntity=operLogMapstruct.toEntity(operLog);
        this.saveNotNull(operLogEntity);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param pageForm 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public PageBean selectOperLogList(PageForm<OperLogDto> pageForm)
    {
        OperLogEntity operLogEntity=operLogMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(),pageForm.getPageSize());
        List<OperLogEntity> operLogEntities=operLogMapper.selectOperLogList(operLogEntity);
        PageInfo<OperLogEntity> pageInfo = new PageInfo<>(operLogEntities);
        List<OperLogDto> operLogDtos= operLogMapstruct.toDto(pageInfo.getList());
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getPages(),operLogDtos);
        return pageBean;
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public void deleteOperLogByIds(String[] operIds)
    {
        this.deleteByIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public OperLogDto selectOperLogById(String operId)
    {
        OperLogEntity operLogEntity=this.getById(operId);
        return operLogMapstruct.toDto(operLogEntity);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
       this.deleteAll();
    }


    @Override
    public void exportExcel(OperLogDto operLogDto){
        OperLogEntity operLogEntity=operLogMapstruct.toEntity(operLogDto);
        List<OperLogEntity> entities=operLogMapper.selectOperLogList(operLogEntity);
        ExcelUtil<OperLogEntity> util=new ExcelUtil(OperLogEntity.class);
        util.exportExcel(entities,"操作日志");
    }

    @Override
    public List<OperLogDto> findByOperNameAndTitle(String operName, String title) {
        List<OperLogEntity> operLogEntities = this.operLogDao.findByOperNameAndTitle(operName, title);
        return operLogMapstruct.toDto(operLogEntities);
    }
}

