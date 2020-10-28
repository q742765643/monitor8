package com.piesat.common.jpa.dialect;

import org.hibernate.dialect.PostgreSQLDialect;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-05-13 18:20
 **/
public class PostgreDialect extends PostgreSQLDialect {
    @Override
    public String getAddForeignKeyConstraintString(
            String constraintName,
            String[] foreignKey,
            String referencedTable,
            String[] primaryKey,
            boolean referencesPrimaryKey) {
//      设置foreignkey对应的列值可以为空
        return " alter "+ foreignKey[0] +" set default null " ;
    }
}

