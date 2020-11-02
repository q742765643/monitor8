package com.piesat.ucenter.mapper.system;

import com.piesat.ucenter.entity.system.PortalAuzEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户角色审核
 *
 * @author wlg
 * @description
 * @date 2020年2月19日上午11:09:27
 */
@Component
public interface PortalAuzMapper {
    /**
     * 条件查询
     *
     * @param entity
     * @return
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020年2月19日下午1:16:07
     */
    List<PortalAuzEntity> selectList(PortalAuzEntity entity) throws Exception;

}
