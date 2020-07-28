package com.piesat.util;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;

public class CronUtil {
    public static long calculateLastTime(String cron,long now){
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cron);

        Date time1 = cronSequenceGenerator.next(new Date(now));//下次执行时间
        Date time2 = cronSequenceGenerator.next(time1);
        Date time3 = cronSequenceGenerator.next(time2);
        long l = time1.getTime() -(time3.getTime() -time2.getTime());

        return l;
    }

}
