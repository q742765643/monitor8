package com.piesat.common.jpa;

import com.piesat.common.jpa.dao.GenericDao;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 18:59
 **/
@NoRepositoryBean
public interface BaseDao<T> extends GenericDao<T, String>
{
}


