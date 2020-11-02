package com.piesat.common.jpa.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 18:43
 **/
@NoRepositoryBean
public interface GenericDao<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    public T saveNotNull(T entity);

    public List<T> saveNotNullAll(Iterable<T> entities);
}

