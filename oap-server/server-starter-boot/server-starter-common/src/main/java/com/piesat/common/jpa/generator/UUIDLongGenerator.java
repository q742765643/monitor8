package com.piesat.common.jpa.generator;

import com.piesat.common.jpa.entity.UUIDEntity;
import lombok.NoArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 18:15
 **/
@NoArgsConstructor
public class UUIDLongGenerator implements IdentifierGenerator {

    private boolean isBlank(final String str) {
        return str == null || str.trim().length() == 0;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        final Serializable serializable = org.hibernate.id.UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator().generate(sharedSessionContractImplementor, o);
        final String uuid = serializable.toString().replaceAll("-", "");
        if (o != null && o instanceof UUIDEntity) {
            final UUIDEntity entity = (UUIDEntity) o;
            System.out.println(this.isBlank(entity.getId()) ? uuid : entity.getId());
            return this.isBlank(entity.getId()) ? uuid : entity.getId();
        }
        return uuid;
    }
}


