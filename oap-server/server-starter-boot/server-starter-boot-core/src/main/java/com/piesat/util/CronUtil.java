package com.piesat.util;

import org.apache.logging.log4j.core.util.CronExpression;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.text.ParseException;
import java.util.Date;

public class CronUtil {
    public static long calculateLastTime(String cron,long now){
        long l=0;
        try {
            CronExpression cronExpression=new CronExpression(cron);
            Date time2 = cronExpression.getNextValidTimeAfter(new Date(now));
            Date time3 = cronExpression.getNextValidTimeAfter(time2);
            long bad = (time3.getTime() -time2.getTime());
            long beginTime=now-bad;
            int i=0;
            while (i<30){
                i++;
                Date nowTime=cronExpression.getNextValidTimeAfter(new Date(beginTime));
                if(nowTime.getTime()==now){
                    l=beginTime;
                    break;
                }
                beginTime=beginTime-bad;
                l=beginTime;
            }
            return l;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

}
