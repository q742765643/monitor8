package com.piesat.common.jpa.generator;

import com.piesat.common.jpa.entity.UUIDEntity;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/18 18:23
 */
public class UUIDStringGenerator extends UUIDGenerator {
    private boolean isBlank(final String str) {
        return str == null || str.trim().length() == 0;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        final Serializable serializable = org.hibernate.id.UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator().generate(sharedSessionContractImplementor, o);
        final String uuid = serializable.toString().replaceAll("-", "");
        if (o != null && o instanceof UUIDEntity) {
            final UUIDEntity entity = (UUIDEntity) o;
            return this.isBlank(entity.getId()) ? uuid : entity.getId();
        }
        return uuid;
    }
}
