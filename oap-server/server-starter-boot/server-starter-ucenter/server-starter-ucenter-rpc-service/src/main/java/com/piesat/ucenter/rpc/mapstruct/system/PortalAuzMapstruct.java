package com.piesat.ucenter.rpc.mapstruct.system;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.system.PortalAuzEntity;
import com.piesat.ucenter.rpc.dto.system.PortalAuzDto;

/** 用户角色审核
*@description
*@author wlg
*@date 2020年2月19日上午11:27:09
*
*/
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PortalAuzMapstruct extends BaseMapper<PortalAuzDto,PortalAuzEntity>{

}
