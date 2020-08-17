package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.host.PacketLossLogService;
import com.piesat.skywalking.dao.PacketLossLogDao;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.PacketLossLogDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.entity.PacketLossLogEntity;
import com.piesat.skywalking.mapstruct.PacketLossLogMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacketLossLogServiceImpl extends BaseService<PacketLossLogEntity> implements PacketLossLogService {
    @Autowired
    private PacketLossLogDao packetLossLogDao;
    @Autowired
    private PacketLossLogMapstruct packetLossLogMapstruct;

    @Override
    public BaseDao<PacketLossLogEntity> getBaseDao() {
        return packetLossLogDao;
    }

    public PageBean selectPageList(PageForm<PacketLossLogDto> pageForm) {
        PacketLossLogEntity packetLossLogEntity=packetLossLogMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(packetLossLogEntity.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(),packetLossLogEntity.getIp());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<PacketLossLogDto> selectBySpecification(PacketLossLogDto packetLossLogDto){
        PacketLossLogEntity packetLossLogEntity=packetLossLogMapstruct.toEntity(packetLossLogDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(packetLossLogEntity.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(),packetLossLogEntity.getIp());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<PacketLossLogEntity> packetLossLogEntities=this.getAll(specification);
        return packetLossLogMapstruct.toDto(packetLossLogEntities);
    }
    @Transactional
    public PacketLossLogDto save(PacketLossLogDto packetLossLogDto){
        PacketLossLogEntity packetLossLogEntity=packetLossLogMapstruct.toEntity(packetLossLogDto);
        packetLossLogEntity=super.save(packetLossLogEntity);
        return packetLossLogMapstruct.toDto(packetLossLogEntity);
    }
    public List<PacketLossLogDto> selectAll(){
        Sort sort = Sort.by("ip");
        List<PacketLossLogEntity> list = super.getAll(sort);
        return packetLossLogMapstruct.toDto(list);
    }

    public PacketLossLogDto findById(String id) {
        return packetLossLogMapstruct.toDto(super.getById(id));
    }

    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
    }


}
