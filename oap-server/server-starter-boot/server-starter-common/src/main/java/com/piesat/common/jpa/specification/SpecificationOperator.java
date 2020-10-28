package com.piesat.common.jpa.specification;

import lombok.Data;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/19 15:39
 */
@Data
public class SpecificationOperator {
    /**
     * 操作符的key，如查询时的name,id之类
     */
    private String key;
    /**
     * 操作符的value，具体要查询的值
     */
    private Object value;
    /**
     * 操作符，自己定义的一组操作符，用来方便查询
     */
    private String oper;
    /**
     * 连接的方式：and或者or
     */
    private String join;


    public static enum Operator {
        eq, ge, le, gt, lt, likeL, likeR, likeAll, isNull, isNotNull, notEqual ,in ,ges, les, gts, lts, ;
        @Override
        public String toString() {
            return name();
        }
    }
    public static enum Join {
        and, or;
        @Override
        public String toString() {
            return name();
        }
    }

}
