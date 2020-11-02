package com.piesat.ucenter.rpc.mapstruct.system;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.system.PortalAuzEntity;
import com.piesat.ucenter.rpc.dto.system.PortalAuzDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * 用户角色审核
 *
 * @author wlg
 * @description
 * @date 2020年2月19日上午11:27:09
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PortalAuzMapstruct extends BaseMapper<PortalAuzDto, PortalAuzEntity> {

}
