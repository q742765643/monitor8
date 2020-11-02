package com.piesat.skywalking.service.timing;

import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.sso.client.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduleService {
    protected static final String QUARTZ_HTHT_JOB = "QUARTZ.HTHT.JOB";
    protected static final String QUARTZ_HTHT_JOBDETAIL = "QUARTZ.HTHT.JOBDETAIL";
    @Autowired
    protected RedisUtil redisUtil;

    public void handleJob(HtJobInfoDto jobInfo) {
        if (jobInfo.getTriggerStatus() == null || StringUtils.isEmpty(jobInfo.getJobCron())) {
            return;
        }
        if (jobInfo.getTriggerStatus() == 1) {
            redisUtil.hset(QUARTZ_HTHT_JOBDETAIL, jobInfo.getId(), jobInfo);
            try {
                Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(new Date(new Date().getTime() - jobInfo.getDelayTime()));
                double score = 0;
                if (!redisUtil.hasKey(QUARTZ_HTHT_JOB)) {
                    score = 0;
                } else {
                    score = redisUtil.zScore(QUARTZ_HTHT_JOB, jobInfo.getId());
                }
                double x = score - nextValidTime.getTime();
                redisUtil.zsetAdd(QUARTZ_HTHT_JOB, jobInfo.getId(), nextValidTime.getTime() + jobInfo.getDelayTime());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            redisUtil.hdel(QUARTZ_HTHT_JOBDETAIL, jobInfo.getId());
            redisUtil.zsetRemove(QUARTZ_HTHT_JOB, jobInfo.getId());
        }
    }

    public void deleteJob(String id) {
        redisUtil.hdel(QUARTZ_HTHT_JOBDETAIL, id);
        redisUtil.zsetRemove(QUARTZ_HTHT_JOB, id);
    }

    public void deleteJob(List<String> ids) {
        for (int i = 0; i < ids.size(); i++) {
            this.deleteJob(ids.get(i));
        }
    }
}
