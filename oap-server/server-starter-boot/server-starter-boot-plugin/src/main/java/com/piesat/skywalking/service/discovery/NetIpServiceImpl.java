package com.piesat.skywalking.service.discovery;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.discover.NetIpService;
import com.piesat.skywalking.dao.NetIpDao;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.dto.NetIpDto;
import com.piesat.skywalking.entity.NetDiscoveryEntity;
import com.piesat.skywalking.entity.NetIpEntity;
import com.piesat.skywalking.mapper.NetIpMapper;
import com.piesat.skywalking.mapstruct.NetIpMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName : NetIpServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2021-03-29 10:33
 */
@Service
public class NetIpServiceImpl  extends BaseService<NetIpEntity> implements NetIpService {
    @Autowired
    private NetIpDao netIpDao;
    @Autowired
    private NetIpMapstruct netIpMapstruct;
    @Autowired
    private NetIpMapper netIpMapper;
    @Override
    public BaseDao<NetIpEntity> getBaseDao() {
        return netIpDao;
    }

    public PageBean selectPageList(PageForm<NetIpDto> pageForm) {
        NetIpEntity discovery = netIpMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.ASC, "ip");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }
    @Override
    @Transactional
    public NetIpDto save(NetIpDto netIpDto) {
        NetIpEntity netIpEntity = super.saveNotNull(netIpMapstruct.toEntity(netIpDto));
        return netIpDto;
    }


    @Override
    public NetIpDto findById(String discoveryId) {
        return netIpMapstruct.toDto(super.getById(discoveryId));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
    }
    @Override
    public void deleteByWhere(String discoveryId) {
        netIpMapper.deleteByWhere(discoveryId);
    }
}

