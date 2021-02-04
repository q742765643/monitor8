package com.piesat.util;

import com.piesat.skywalking.dto.FileMonitorLogDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : DdataTimeUtil
 * @Description :
 * @Author : zzj
 * @Date: 2021-02-01 13:53
 */
public class DdataTimeUtil {
    protected static final String REGEX = "(\\$\\{(.*?)\\})";
    protected static final Pattern PATTERN = Pattern.compile(REGEX);
    public static Date repalceRegx(String fileName,long time,Integer isUt) {
        String expression = "";
        try {
            Matcher m = PATTERN.matcher(fileName);
            while (m.find()) {
                String x1 = m.group(1);
                String x2=m.group(2);
                if(x2.indexOf(",")!=-1){
                    x2=x2.split(",")[0];
                    expression=x1.replaceAll(x2,"yyyyMMddHHmmss");
                }else {
                    expression=x1.replaceAll(x2,"yyyyMMddHHmmss");
                }

            }
            if(StringUtil.isNotEmpty(expression)){
                String dataTime = DateExpressionEngine.formatDateExpression(expression, time);
                SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
                time=format.parse(dataTime).getTime();

            }
        } catch (Exception e) {
        }
        if(1==isUt){
            time=time+3600*8*1000;
        }
        return new Date(time);
    }
    public static String repalceRegxString(String fileName,long time) {
        String expression = "";
        String ddateTime="";
        try {
            Matcher m = PATTERN.matcher(fileName);
            while (m.find()) {
                String x1 = m.group(1);
                String x2=m.group(2);
                if(x2.indexOf(",")!=-1){
                    x2=x2.split(",")[0];
                    expression=x1.replaceAll(x2,"yyyy-MM-dd HH:mm:ss");
                }else {
                    expression=x1.replaceAll(x2,"yyyy-MM-dd HH:mm:ss");
                }

            }
            if(StringUtil.isNotEmpty(expression)){
                ddateTime = DateExpressionEngine.formatDateExpression(expression, time);
            }else {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ddateTime=simpleDateFormat.format(time);
            }
        } catch (Exception e) {
        }
        return ddateTime;
    }

    public static void main(String[] args){
        System.out.println(repalceRegx("${MMdd,-15d}",new Date().getTime(),1));
    }
}

