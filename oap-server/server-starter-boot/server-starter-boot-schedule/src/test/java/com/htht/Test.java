package com.htht;

import cn.hutool.core.date.DateUtil;
import com.piesat.util.DateExpressionEngine;
import com.piesat.util.HtDateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : Test
 * @Description :
 * @Author : zzj
 * @Date: 2021-01-28 17:28
 */
public class Test {
    public static void main(String[] args){
        /*String folderRegular = DateExpressionEngine.formatDateExpression("/aaaa", new Date().getTime());
        System.out.println(folderRegular);*/
        String a="Z_yyyyMMdd00_P[\\w\\W]*";
        String d=a.replaceAll("[ymdhsYMDHS]", "\\\\d");
        Pattern p = Pattern.compile(d);
        Matcher m = p.matcher("Z_2021020500_Pssssss000000.vz2");
        if (m.find()) {
            String timeStr = m.group(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, String> map = HtDateUtil.getTime(new Date());
             //HtDateUtil.matchingTime(DateUtil.parse(timeStr, format), a, map);
        }


    }
}

