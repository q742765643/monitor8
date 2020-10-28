package com.piesat.common.jpa.dialect;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-02-03 15:55
 **/
public class MysqlDialect  extends MySQL5InnoDBDialect {
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

