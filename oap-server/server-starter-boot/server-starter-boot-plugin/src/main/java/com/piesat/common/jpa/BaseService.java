package com.piesat.common.jpa;

import com.piesat.common.jpa.dao.GenericDao;
import com.piesat.common.jpa.service.GenericServiceImpl;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 19:00
 **/
public abstract class BaseService<T> extends GenericServiceImpl<T, String> {
    @Override
    public GenericDao<T, String> getGenericDao() {
        return this.getBaseDao();
    }

    public abstract BaseDao<T> getBaseDao();
}


