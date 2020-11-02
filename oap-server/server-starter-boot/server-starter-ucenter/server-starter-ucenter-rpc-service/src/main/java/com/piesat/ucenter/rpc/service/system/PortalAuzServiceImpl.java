package com.piesat.ucenter.rpc.service.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.ucenter.dao.system.PortalAuzDao;
import com.piesat.ucenter.entity.system.PortalAuzEntity;
import com.piesat.ucenter.mapper.system.PortalAuzMapper;
import com.piesat.ucenter.rpc.api.system.PortalAuzService;
import com.piesat.ucenter.rpc.dto.system.PortalAuzDto;
import com.piesat.ucenter.rpc.mapstruct.system.PortalAuzMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色审核
 *
 * @author wlg
 * @description
 * @date 2020年2月19日上午11:29:31
 */
@Service
public class PortalAuzServiceImpl extends BaseService<PortalAuzEntity> implements PortalAuzService {

    @Autowired
    private PortalAuzDao portalAuzDao;

    @Autowired
    private PortalAuzMapper portalAuzMapper;

    @Autowired
    private PortalAuzMapstruct portalAuzMapstruct;


    @Override
    public BaseDao<PortalAuzEntity> getBaseDao() {
        return portalAuzDao;
    }

    /**
     * 获取分页数据
     *
     * @param pageForm
     * @return
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020-02-19 13:14
     */
    @Override
    public PageBean findPageData(PageForm<PortalAuzDto> pageForm) throws Exception {
        PortalAuzEntity pe = portalAuzMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(), pageForm.getPageSize());

        if (!StringUtil.isEmpty(pe.getUsername())) pe.setUsername("%" + pe.getUsername() + "%");

        List<PortalAuzEntity> data = portalAuzMapper.selectList(pe);
        PageInfo<PortalAuzEntity> pageInfo = new PageInfo<>(data);

        List<PortalAuzDto> dtoData = portalAuzMapstruct.toDto(pageInfo.getList());
        PageBean page = new PageBean(pageInfo.getTotal(), pageInfo.getPages(), dtoData);
        return page;
    }

    /**
     * 根据id删除
     *
     * @param ids
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020-02-19 13:32
     */
    @Override
    @Transactional
    public void delByIds(String ids) throws Exception {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            portalAuzDao.deleteById(ids);
        }

    }

    /**
     * 通过/不通过 状态更改
     *
     * @param dto
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020-02-19 13:40
     */
    @Override
    @Transactional
    public void update(PortalAuzDto dto) throws Exception {
        PortalAuzEntity pe = portalAuzMapstruct.toEntity(dto);
        this.saveNotNull(pe);

    }


    /**
     * 新增
     *
     * @param dto
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020-02-19 13:42
     */
    @Override
    @Transactional
    public void add(PortalAuzDto dto) throws Exception {
        PortalAuzEntity pe = portalAuzMapstruct.toEntity(dto);
        portalAuzDao.save(pe);

    }

}
