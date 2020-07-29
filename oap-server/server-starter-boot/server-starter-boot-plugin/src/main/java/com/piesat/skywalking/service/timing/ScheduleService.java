package com.piesat.skywalking.service.timing;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.model.HtJobInfo;
import com.piesat.sso.client.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
public class ScheduleService {
    @Autowired
    private RedisUtil redisUtil;
    private static final String QUARTZ_HTHT_JOB = "QUARTZ.HTHT.JOB";
    private static final String QUARTZ_HTHT_JOBDETAIL= "QUARTZ.HTHT.JOBDETAIL";
    public void handleJob(HtJobInfoDto jobInfo){
        if(jobInfo.getTriggerStatus()==1){
               redisUtil.hset(QUARTZ_HTHT_JOBDETAIL,jobInfo.getId(),jobInfo);
            try {
                Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(new Date(new Date().getTime()-jobInfo.getDelayTime()));
                double score=0;
                if(!redisUtil.hasKey(QUARTZ_HTHT_JOB)){
                    score=0;
                }else{
                    score=redisUtil.zScore(QUARTZ_HTHT_JOB,jobInfo.getId());
                }
                double x=score- nextValidTime.getTime();
                redisUtil.zsetAdd(QUARTZ_HTHT_JOB,jobInfo.getId(),nextValidTime.getTime()+jobInfo.getDelayTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            redisUtil.hdel(QUARTZ_HTHT_JOBDETAIL,jobInfo.getId());
            redisUtil.zsetRemove(QUARTZ_HTHT_JOB,jobInfo.getId());
        }
    }
}
