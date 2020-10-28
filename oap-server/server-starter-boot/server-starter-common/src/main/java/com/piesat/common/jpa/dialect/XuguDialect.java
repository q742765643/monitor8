package com.piesat.common.jpa.dialect;

import com.xugu.dialect.XuguDialect5;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLDialect;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/18 14:24
 */
public class XuguDialect extends XuguDialect5 {
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
