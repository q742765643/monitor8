package com.piesat.ucenter.rpc.service.monitor;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.dao.monitor.LoginInfoDao;
import com.piesat.ucenter.entity.monitor.LoginInfoEntity;
import com.piesat.ucenter.mapper.monitor.LoginInfoMapper;
import com.piesat.ucenter.rpc.api.monitor.LoginInfoService;
import com.piesat.ucenter.rpc.dto.monitor.LoginInfoDto;
import com.piesat.ucenter.rpc.mapstruct.monitor.LoginInfoMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 14:13
 **/
@Service
public class LoginInfoServiceImpl extends BaseService<LoginInfoEntity> implements LoginInfoService {
    @Autowired
    private LoginInfoDao loginInfoDao;
    @Autowired
    private LoginInfoMapstruct loginInfoMapstruct;
    @Autowired
    private LoginInfoMapper loginInfoMapper;

    @Override
    public BaseDao<LoginInfoEntity> getBaseDao() {
        return loginInfoDao;
    }

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(LoginInfoDto logininfor) {
        LoginInfoEntity loginInfoEntity = loginInfoMapstruct.toEntity(logininfor);
        this.saveNotNull(loginInfoEntity);

    }

    /**
     * 查询系统登录日志集合
     *
     * @param
     * @return 登录记录集合
     */
    @Override
    public PageBean selectLogininforList(PageForm<LoginInfoDto> pageForm) {
        LoginInfoEntity loginInfoEntity = loginInfoMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(), pageForm.getPageSize());
        List<LoginInfoEntity> loginInfoEntities = loginInfoMapper.selectLogininforList(loginInfoEntity);
        PageInfo<LoginInfoEntity> pageInfo = new PageInfo<>(loginInfoEntities);
        List<LoginInfoDto> loginInfoDtos = loginInfoMapstruct.toDto(pageInfo.getList());
        PageBean pageBean = new PageBean(pageInfo.getTotal(), pageInfo.getPages(), loginInfoDtos);
        return pageBean;
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    @Override
    public void deleteLogininforByIds(String[] infoIds) {
        this.deleteByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        this.deleteAll();
    }

    @Override
    public void exportExcel(LoginInfoDto loginInfoDto) {
        LoginInfoEntity loginInfoEntity = loginInfoMapstruct.toEntity(loginInfoDto);
        List<LoginInfoEntity> entities = loginInfoMapper.selectLogininforList(loginInfoEntity);
        ExcelUtil<LoginInfoEntity> util = new ExcelUtil(LoginInfoEntity.class);
        util.exportExcel(entities, "登录日志");
    }
}

