package com.piesat.ucenter.mapper.system;

import java.util.List;

import org.springframework.stereotype.Component;

import com.piesat.ucenter.entity.system.PortalAuzEntity;

/** 用户角色审核
*@description
*@author wlg
*@date 2020年2月19日上午11:09:27
*
*/
@Component
public interface PortalAuzMapper {
	/**
	 *  条件查询
	 * @description 
	 * @author wlg
	 * @date 2020年2月19日下午1:16:07
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	List<PortalAuzEntity> selectList(PortalAuzEntity entity) throws Exception;

}
