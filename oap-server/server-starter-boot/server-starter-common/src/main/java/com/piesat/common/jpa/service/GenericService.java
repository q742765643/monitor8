package com.piesat.common.jpa.service;


import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2019/11/17.
 */
public interface GenericService<T, ID extends Serializable>{
    public Class<T> getEntityClass();
    public PageBean getPage(final Specification<T> specification, PageForm pageForm, Sort sort);
    public T getById(final ID id);
    public T getBySpecification(final Specification<T> specification);
    public boolean exists(final ID id);
    public long count();
    public long count(final Specification<T> specification);
    public List<T> getAll();
    public List<T> getAll(final Specification<T> specification);
    public PageBean getAll(final  PageForm pageForm,Sort sort);
    public List<T> getAll(final Sort sort);
    public List<T> getAll(final Specification<T> specification, final Sort sort);
    public T save(final T entity);
    public List<T> save(final Iterable<T> entities);
    public T saveNotNull(final T entity);
    public List<T> saveNotNull(final Iterable<T> entities);
    public void delete(final ID id);
    public void delete(final T entity);
    public void delete(final Iterable<T> entities);
    public void deleteByIds(final Iterable<ID> ids);
    public void deleteInBatch(final Iterable<T> entities);
    public void deleteAll();
    public void deleteAllInBatch();
    public List<Map<String,Object>> queryByNativeSQL(String sql, Map<String,Object> params);
    public List<Map<String,Object>> queryByNativeSQL(String sql, Object... params);
    public List<?> queryByNativeSQL(String sql,Class entityClass,Map<String,Object> params);
    public PageBean queryByNativeSQLPageMap(String sql, Map<String,Object> params, PageForm pageForm);
    public PageBean queryByNativeSQLPageList(String sql, Class entityClass, Map<String,Object> params, PageForm pageForm);
}
