package com.piesat.common;

import java.util.*;

/**
 * @author cwh
 * @date 2020年 04月29日 17:54:35
 */
public class MapUtil {

    public static Map<String, Object> transformUpperCase(Map<String, Object> orgMap) {
        Map<String, Object> resultMap = new HashMap<>();

        if (orgMap == null || orgMap.isEmpty()) {
            return resultMap;
        }
        Set<String> keySet = orgMap.keySet();
        for (String key : keySet) {
            String newKey = key.toLowerCase();
//            newKey = newKey.replace("_", "");
            resultMap.put(newKey, orgMap.get(key));
        }

        return resultMap;
    }

    public static List<Map<String, Object>> transformMapList(List<Map<String, Object>> orgMap) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> map : orgMap) {
            list.add(transformUpperCase(map));
        }
        return list;
    }

}
