package com.piesat.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IndexNameUtil {
    public static String getIndexName(String name, Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String indexName = name + "-" + format.format(date);
        return indexName;
    }

    public static String getIndexName(String name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String indexName = name + "-" + format.format(new Date());
        return indexName;
    }


}
