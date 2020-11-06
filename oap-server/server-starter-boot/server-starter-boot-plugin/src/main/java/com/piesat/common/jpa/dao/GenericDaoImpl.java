package com.piesat.common.jpa.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.*;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 18:43
 **/
@NoRepositoryBean
public class GenericDaoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements GenericDao<T, ID> {
    protected static final Logger logger;

    static {
        logger = LoggerFactory.getLogger((Class) GenericDaoImpl.class);
    }

    private final EntityManager em;
    private final Class<T> entityClass;
    private final String entityName;
    private final JpaEntityInformation<T, ?> entityInformation;

    public GenericDaoImpl(final JpaEntityInformation<T, ID> entityInformation, final EntityManager entityManager) {
        super((JpaEntityInformation) entityInformation, entityManager);
        this.em = entityManager;
        this.entityClass = (Class<T>) entityInformation.getJavaType();
        this.entityName = entityInformation.getEntityName();
        this.entityInformation = entityInformation;
    }

    /**
     * 获取对象的空属性
     */
    private static String[] getNullProperties(Object src) {
        // 1.获取Bean
        BeanWrapper srcBean = new BeanWrapperImpl(src);
        // 2.获取Bean的属性描述
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        // 3.获取Bean的空属性
        Set<String> properties = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : pds) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = srcBean.getPropertyValue(propertyName);
            if (StringUtils.isEmpty(propertyValue)) {
                srcBean.setPropertyValue(propertyName, null);
                properties.add(propertyName);
            }
        }
        return properties.toArray(new String[0]);
    }

    public EntityManager getEm() {
        return this.em;
    }

    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    public String getEntityName() {
        return this.entityName;
    }

    /**
     * 通用save方法 ：新增/选择性更新
     */
    @Override
    @Transactional
    public T saveNotNull(T entity) {

        // 获取ID
        ID entityId = (ID) this.entityInformation.getId(entity);
        T managedEntity = null;
        T mergedEntity;
        if (entityId == null || "".equals(entityId)) {
            em.persist(entity);
            mergedEntity = entity;
        } else {
            Optional<T> optional = this.findById(entityId);

            if (optional != null && optional.isPresent()) {
                managedEntity = optional.get();
            }
            if (managedEntity == null) {
                em.persist(entity);
                mergedEntity = entity;
            } else {
                BeanUtils.copyProperties(entity, managedEntity, getNullProperties(entity));
                em.merge(managedEntity);
                mergedEntity = managedEntity;
            }
        }
        return entity;
    }

    @Override
    @Transactional
    public List<T> saveNotNullAll(Iterable<T> entities) {
        Assert.notNull(entities, "The given Iterable of entities not be null!");
        List<T> result = new ArrayList();
        Iterator var3 = entities.iterator();

        while (var3.hasNext()) {
            T entity = (T) var3.next();
            result.add(this.saveNotNull(entity));
        }

        return result;
    }

    public JpaEntityInformation<T, ?> getEntityInformation() {
        return entityInformation;
    }
}

