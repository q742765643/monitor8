package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.skywalking.model.HtJobInfo;
import org.springframework.stereotype.Repository;

/**
 * @ClassName : JobInfoDao
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-15 18:02
 */
@Repository
public interface JobInfoDao extends BaseDao<HtJobInfo> {
}

