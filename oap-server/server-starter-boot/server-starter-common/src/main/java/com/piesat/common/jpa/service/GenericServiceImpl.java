package com.piesat.common.jpa.service;

import com.alibaba.fastjson.JSON;
import com.piesat.common.jpa.dao.GenericDao;
import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.util.BaseDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 18:46
 **/
//@Transactional(readOnly = true)
@Service
public abstract class GenericServiceImpl<T, ID extends Serializable> implements GenericService<T, ID>
{
    protected Logger logger;
    @PersistenceContext
    private EntityManager em;
    public GenericServiceImpl() {
        this.logger = LoggerFactory.getLogger((Class)this.getClass());
    }

    public abstract GenericDao<T, ID> getGenericDao();

    @Override
    public Class<T> getEntityClass() {
        final Type genType = this.getClass().getGenericSuperclass();
        final Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        return (Class<T>)params[0];
    }

    @Override
    public PageBean getPage(final Specification<T> specification, PageForm pageForm, Sort sort) {
        PageBean pageBean=new PageBean();
        sort=this.getSort(pageForm,sort);
        Page<T> page=this.getGenericDao().findAll(specification,this.buildPageRequest(pageForm,sort));
        pageBean.setPageData(page.getContent());
        pageBean.setTotalPage(page.getTotalPages());
        pageBean.setTotalCount(page.getTotalElements());
        return pageBean;
    }

    @Override
    public T getById(final ID id) {
        Optional<T> optionalT = this.getGenericDao().findById(id);
        return optionalT.isPresent() ? optionalT.get(): null;
    }

    @Override
    public T getBySpecification(final Specification<T> specification) {
        Optional<T> optionalT = this.getGenericDao().findOne(specification);
        return optionalT.isPresent() ? optionalT.get(): null;
    }

    @Override
    public boolean exists(final ID id) {
        return this.getGenericDao().existsById(id);
    }

    @Override
    public long count() {
        return this.getGenericDao().count();
    }

    @Override
    public long count(final Specification<T> specification) {
        return this.getGenericDao().count(specification);
    }

    @Override
    public List<T> getAll() {
        return this.getGenericDao().findAll();
    }

    @Override
    public List<T> getAll(final Specification<T> specification) {
        return this.getGenericDao().findAll(specification);
    }

    @Override
    public PageBean getAll(final PageForm pageForm,Sort sort) {
        PageBean pageBean=new PageBean();
        sort=this.getSort(pageForm,sort);
        Page<T> page=this.getGenericDao().findAll(this.buildPageRequest(pageForm,sort));
        pageBean.setPageData(page.getContent());
        pageBean.setTotalPage(page.getTotalPages());
        pageBean.setTotalCount(page.getTotalElements());
        return pageBean;
    }

    @Override
    public List<T> getAll(final Sort sort) {
        return this.getGenericDao().findAll(sort);
    }

    @Override
    public List<T> getAll(final Specification<T> specification, final Sort sort) {
        return this.getGenericDao().findAll(specification, sort);
    }

    @Transactional(readOnly = false)
    @Override
    public T save(final T entity) {
        return this.getGenericDao().save(entity);
    }

    @Transactional(readOnly = false)
    @Override
    public List<T> save(final Iterable<T> entities) {
        return this.getGenericDao().saveAll(entities);
    }

    @Transactional(readOnly = false)
    @Override
    public T saveNotNull(final T entity) {
        return this.getGenericDao().saveNotNull(entity);
    }

    @Transactional(readOnly = false)
    @Override
    public List<T> saveNotNull(final Iterable<T> entities) {
        return this.getGenericDao().saveNotNullAll(entities);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(final ID id) {
        this.getGenericDao().deleteById(id);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(final T entity) {
        this.getGenericDao().delete(entity);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(final Iterable<T> entities) {
        this.getGenericDao().delete((T) entities);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByIds(final Iterable<ID> ids) {
        for (final ID id : ids) {
            this.delete(id);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteInBatch(final Iterable<T> entities) {
        this.getGenericDao().deleteInBatch(entities);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteAll() {
        this.getGenericDao().deleteAll();
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteAllInBatch() {
        this.getGenericDao().deleteAllInBatch();
    }
    /**
     * 执行原生SQL查询
     * @param sql
     * @param params sql参数
     * @return 结果集并影射成Map
     */
    @Override
    public List<Map<String,Object>> queryByNativeSQL(String sql, Map<String,Object> params){
        Query query=em.createNativeQuery(sql);
        if(params!=null&&params.size()>0){
            for(String param:params.keySet() ){
                query.setParameter(param,params.get(param));
            }
        }
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }
    /**
     * 执行原生SQL查询
     * @param sql
     * @param params sql参数
     * @return 结果集并影射成Map
     */
    @Override
    public List<Map<String,Object>> queryByNativeSQL(String sql, Object... params){
        Query query=em.createNativeQuery(sql);
        if(params!=null&&params.length>0){
            for(int i=0;i< params.length;i++){
                query.setParameter(i,params[i]);
            }
        }
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }
    /**
     * 执行原生SQL查询
     * @param sql
     * @param entityClass 结果转换实体类
     * @param params sql参数
     * @return 结果集并影射成entityClass
     */
    @Override
    public List<?> queryByNativeSQL(String sql, Class entityClass, Map<String,Object> params){
        Query query=em.createNativeQuery(sql,entityClass);
        if(params!=null&&params.size()>0){
            for(String param:params.keySet() ){
                query.setParameter(param,params.get(param));
            }
        }
        return query.getResultList();
    }

    @Override
    public PageBean queryByNativeSQLPageList(String sql, Class entityClass, Map<String,Object> params, PageForm pageForm){
        PageRequest pageRequest=this.buildPageRequest(pageForm,null);
        String newSql="select * from ("+sql+") limit "+pageRequest.getOffset()+","+pageRequest.getPageSize();        Query query=em.createNativeQuery(newSql,entityClass);
        if(params!=null&&params.size()>0){
            for(String param:params.keySet() ){
                query.setParameter(param,params.get(param));
            }
        }
        List<T> list=query.getResultList();
        long total=this.queryByNativeSQLCount(sql,params);
        PageBean pageBean=new PageBean();
        pageBean.setTotalCount(total);
        pageBean.setPageData(list);
        return pageBean;
    }
    @Override
    public PageBean queryByNativeSQLPageMap(String sql, Map<String,Object> params, PageForm pageForm){
        PageRequest pageRequest=this.buildPageRequest(pageForm,null);
        String newSql="select * from ("+sql+") limit "+pageRequest.getOffset()+","+pageRequest.getPageSize();
        Query query=em.createNativeQuery(newSql);
        if(params!=null&&params.size()>0){
            for(String param:params.keySet() ){
                query.setParameter(param,params.get(param));
            }
        }
        List<Map<String,Object>> list=query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        long total=this.queryByNativeSQLCount(sql,params);
        PageBean pageBean=new PageBean();
        pageBean.setTotalCount(total);
        pageBean.setPageData(list);
        return pageBean;
    }

    public long queryByNativeSQLCount(String sql, Map<String,Object> params){
        String newSql="select count(*) from ("+sql+")";
        Query query=em.createNativeQuery(newSql);
        if(params!=null&&params.size()>0){
            for(String param:params.keySet() ){
                query.setParameter(param,params.get(param));
            }
        }
        BigDecimal bigDecimal= (BigDecimal) query.getSingleResult();
        return bigDecimal.longValue();
    }
    public PageRequest buildPageRequest(PageForm pageForm,Sort sort) {
        if(sort==null){
            sort=Sort.unsorted();
        }
        return PageRequest.of(pageForm.getCurrentPage()-1, pageForm.getPageSize(),sort);
    }

    public Sort getSort(PageForm pageForm,Sort sort){
        Object obj=pageForm.getT();
        if(obj instanceof BaseDto){
            BaseDto baseDto= (BaseDto) obj;
            String  param=baseDto.getParams();
            if (null == param || !com.piesat.common.utils.StringUtils.isNotNullString(param)) {
                return sort;
            }
            Map<String, Object> map = JSON.parseObject(param, Map.class);
            Map<String, String> mapList = (Map<String, String>) map.get("orderBy");
            if (null == mapList || mapList.isEmpty()) {
                return sort;
            }
            List<Sort.Order> orders=new ArrayList<Sort.Order>();
            mapList.forEach((k,v)-> {
                String name =k.trim();
                String sortT =v;
                orders.add( new Sort.Order(Sort.Direction.fromString(sortT), name));
            });
            if(!orders.isEmpty()){
                sort=Sort.by(orders);
            }
        }else if(obj instanceof BaseEntity){
            BaseEntity baseEntity= (BaseEntity) obj;
            String  param=baseEntity.getParams();
            if (null == param || !com.piesat.common.utils.StringUtils.isNotNullString(param)) {
                return sort;
            }
            Map<String, Object> map = JSON.parseObject(param, Map.class);
            Map<String, String> mapList = (Map<String, String>) map.get("orderBy");
            if (null == mapList || mapList.isEmpty()) {
                return sort;
            }
            List<Sort.Order> orders=new ArrayList<Sort.Order>();
            mapList.forEach((k,v)-> {
                String name = k.trim();
                String sortT = v;
                orders.add( new Sort.Order(Sort.Direction.fromString(sortT), name));
            });
            if(!orders.isEmpty()){
                sort=Sort.by(orders);
            }
        }

        return sort;
    }

}


