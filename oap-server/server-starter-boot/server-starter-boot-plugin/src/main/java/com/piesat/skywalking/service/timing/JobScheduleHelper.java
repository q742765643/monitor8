package com.piesat.skywalking.service.timing;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.model.HtJobInfo;
import com.piesat.skywalking.service.trigger.TriggerService;
import com.piesat.sso.client.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JobScheduleHelper {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TriggerService triggerService;

    public static final long PRE_READ_MS = 5000;    // pre read

    private Thread scheduleThread;
    private Thread ringThread;
    private volatile boolean scheduleThreadToStop = false;
    private volatile boolean ringThreadToStop = false;
    private volatile static Map<Integer, List<HtJobInfoDto>> ringData = new ConcurrentHashMap<>();
    private static final String QUARTZ_HTHT_JOB = "QUARTZ.HTHT.JOB";
    private static final String QUARTZ_HTHT_JOBDETAIL= "QUARTZ.HTHT.JOBDETAIL";
    public void start(){

        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - System.currentTimeMillis()%1000 );
                } catch (InterruptedException e) {
                    if (!scheduleThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }
                int preReadCount = 300;

                while (!scheduleThreadToStop) {
                    long start = System.currentTimeMillis();
                    boolean preReadSuc = true;
                    try {

                        long nowTime = System.currentTimeMillis();
                        Set<DefaultTypedTuple> scheduleList = redisUtil.rangeByScoreWithScores(QUARTZ_HTHT_JOB, nowTime + PRE_READ_MS, preReadCount);
                        if (scheduleList!=null && scheduleList.size()>0) {
                            List<HtJobInfoDto> jobInfos = new ArrayList<>();
                            for (DefaultTypedTuple typedTuple : scheduleList) {
                                    String jobId=(String) typedTuple.getValue();
                                    HtJobInfoDto jobInfo = (HtJobInfoDto) redisUtil.hget(QUARTZ_HTHT_JOBDETAIL,jobId);
                                    long nextTime=typedTuple.getScore().longValue();
                                    jobInfo.setTriggerNextTime(nextTime);
                                // time-ring jump
                                if (nowTime > jobInfo.getTriggerNextTime() + PRE_READ_MS) {
                                    // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                    log.warn("schedule misfire, jobId = " + jobInfo.getId());

                                    // fresh next
                                    refreshNextValidTime(jobInfo, new Date());

                                } else if (nowTime > jobInfo.getTriggerNextTime()) {
                                    // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time

                                    // 1、trigger
                                    triggerService.trigger(jobInfo);

                                    // 2、fresh next
                                    refreshNextValidTime(jobInfo, new Date());

                                    // next-trigger-time in 5s, pre-read again
                                    if (jobInfo.getTriggerStatus()==1 && nowTime + PRE_READ_MS > jobInfo.getTriggerNextTime()) {

                                        // 1、make ring second
                                        int ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                        // 2、push time ring
                                        pushTimeRing(ringSecond, jobInfo);

                                        // 3、fresh next
                                        refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                    }

                                } else {
                                    // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time

                                    // 1、make ring second
                                    int ringSecond = (int)((jobInfo.getTriggerNextTime()/1000)%60);

                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo);

                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));

                                }
                                jobInfos.add(jobInfo);
                            }

                            // 3、update trigger info
                            for (HtJobInfoDto jobInfo : jobInfos) {
                                redisUtil.zsetAdd(QUARTZ_HTHT_JOB, jobInfo.getId(), jobInfo.getTriggerNextTime());
                            }

                        } else {
                            preReadSuc = false;
                        }

                        // tx stop


                    } catch (Exception e) {
                        if (!scheduleThreadToStop) {
                            log.error("JobScheduleHelper#scheduleThread error:{}", e);
                        }
                    } finally {


                    }
                    long cost = System.currentTimeMillis()-start;


                    // Wait seconds, align second
                    if (cost < 1000) {  // scan-overtime, not wait
                        try {
                            // pre-read period: success > scan each second; fail > skip this period;
                            TimeUnit.MILLISECONDS.sleep((preReadSuc?1000:PRE_READ_MS) - System.currentTimeMillis()%1000);
                        } catch (InterruptedException e) {
                            if (!scheduleThreadToStop) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }

                }

                log.info("JobScheduleHelper#scheduleThread stop");
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("JobScheduleHelper#scheduleThread");
        scheduleThread.start();


        // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // align second
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000 );
                } catch (InterruptedException e) {
                    if (!ringThreadToStop) {
                        log.error(e.getMessage(), e);
                    }
                }

                while (!ringThreadToStop) {

                    try {
                        // second data
                        List<HtJobInfoDto> ringItemData = new ArrayList<>();
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);   // 避免处理耗时太长，跨过刻度，向前校验一个刻度；
                        for (int i = 0; i < 2; i++) {
                            List<HtJobInfoDto> tmpData = ringData.remove( (nowSecond+60-i)%60 );
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }

                        if (ringItemData.size() > 0) {
                            // do trigger
                            for (HtJobInfoDto jobInfo: ringItemData) {
                                // do trigger
                                triggerService.trigger(jobInfo);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (Exception e) {
                        if (!ringThreadToStop) {
                            log.error("JobScheduleHelper#ringThread error:{}", e);
                        }
                    }

                    // next second, align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis()%1000);
                    } catch (InterruptedException e) {
                        if (!ringThreadToStop) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("JobScheduleHelper#ringThread");
        ringThread.start();
    }

    private void refreshNextValidTime(HtJobInfoDto jobInfo, Date fromTime) throws ParseException {
        Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(new Date(fromTime.getTime()-jobInfo.getDelayTime()));
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime.getTime()+jobInfo.getDelayTime());
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0);
            jobInfo.setTriggerNextTime(0);
        }
    }

    private void pushTimeRing(int ringSecond, HtJobInfoDto jobInfo){
        // push async ring
        List<HtJobInfoDto> ringItemData = ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList<HtJobInfoDto>();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobInfo);

    }
}
