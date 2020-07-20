package com.piesat.ucenter.dao.monitor;

import com.piesat.common.jpa.BaseDao;
import com.piesat.ucenter.entity.monitor.OperLogEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zzj on 2019/12/16.
 */
@Repository
public interface OperLogDao extends BaseDao<OperLogEntity>{
    List<OperLogEntity> findByOperNameAndTitle(String operName,String title);
}
