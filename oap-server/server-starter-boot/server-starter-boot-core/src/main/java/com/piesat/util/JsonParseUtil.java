package com.piesat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JsonParseUtil {
    public static String checkFormat(String str){
        String _str = str.trim();
        if(_str.startsWith("[") && _str.endsWith("]")){
            return  _str.substring(1,_str.length()-1);
        }
        return  _str;
    }

    /**
     * 打印Map中的数据
     * @param map
     */
    public static void printJsonMap(Map map){
        Set entrySet = map.entrySet();
        Iterator<Map.Entry<String, Object>> it = entrySet.iterator();
        //最外层提取
        while(it.hasNext()){
            Map.Entry<String, Object> e = it.next();
            System.out.println("Key 值："+e.getKey()+"     Value 值："+e.getValue());
        }
    }



    /**
     * JSON 类型的字符串转换成 Map
     */
    public static void parseJSON2Map(Map jsonMap,String jsonStr,String parentKey){
        //字符串转换成JSON对象
        JSONObject json = JSONObject.parseObject(jsonStr);
        //最外层JSON解析
        for(Object k : json.keySet()){
            //JSONObject 实际上相当于一个Map集合，所以我们可以通过Key值获取Value
            Object v = json.get(k);
            //构造一个包含上层keyName的完整keyName
            String fullKey = (null == parentKey || parentKey.trim().equals("") ? k.toString() : parentKey + "." + k);

            if(v instanceof JSONArray){
                jsonMap.put(fullKey, JSON.toJSONString(v));
                //如果内层还是数组的话，继续解析
                /*Iterator it = ((JSONArray) v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = (JSONObject)it.next();
                    parseJSON2Map(jsonMap,json2.toString(),fullKey);
                }*/
            } else if(isNested(v)){
                parseJSON2Map(jsonMap,String.valueOf(v),fullKey);
            }
            else{
                jsonMap.put(fullKey, v);
            }
        }
    }

    public static boolean isNested(Object jsonObj){
        if(null==jsonObj){
            return false;
        }
        return jsonObj.toString().startsWith("{")||jsonObj.toString().startsWith("[{");
    }

    public static void println(Object str){
        System.out.println(str);
    }

    public static String formateDate(String dateStr){
        try {
            dateStr = dateStr.replace("Z", " UTC");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
            return format1.format(format.parse(dateStr));
        } catch (ParseException e) {
            log.error("转换时间失败！", e);
        }
        return null;
    }
    public static Date formateToDate(String dateStr){
        try {
            dateStr = dateStr.replace("Z", " UTC");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
            return format.parse(dateStr);
        } catch (ParseException e) {
            log.error("转换时间失败！", e);
        }
        return null;
    }
}
