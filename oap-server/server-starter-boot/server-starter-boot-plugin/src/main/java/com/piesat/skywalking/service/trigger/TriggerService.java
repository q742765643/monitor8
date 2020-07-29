package com.piesat.skywalking.service.trigger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.remote.RemoteService;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class TriggerService {
    @GrpcHthtClient
    private RemoteService remoteService;
    private ExecutorService executorService= new ThreadPoolExecutor(800, 800,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("trigger-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    public void trigger(HtJobInfoDto jobInfoDto){
        executorService.execute(()->{
            ResultT<String> resultT=new ResultT<>();
            JobContext jobContext=new JobContext();
            long triggerLastTime=jobInfoDto.getTriggerLastTime();
            if(triggerLastTime==0){
                triggerLastTime=jobInfoDto.getTriggerNextTime();
            }
            jobInfoDto.setTriggerLastTime(triggerLastTime-jobInfoDto.getDelayTime());
            jobContext.setHandler(jobInfoDto.getJobHandler());
            jobContext.setHtJobInfoDto(jobInfoDto);
            if(1==jobInfoDto.getTriggerType()){
                List<?> list=remoteService.sharding(jobContext,resultT);
                if(list.size()==0){
                    return;
                }
                double batch = new BigDecimal(list.size()).divide(new BigDecimal(3),2, RoundingMode.HALF_UP).doubleValue();
                int  slice= (int) Math.ceil(batch);
                int start=0;
                int end=0;
                for(int i=0;i<3;i++){
                    if(i>0){
                        start=slice*i-1;
                    }
                    end=slice*(i+1)-1;
                    if(end>list.size()){
                        end=list.size()-1;
                    }
                    jobContext.setLists(list.subList(start,end));
                    remoteService.execute(jobContext,resultT);
                    if(slice<=1){
                        break;
                    }

                }
            }else {
                remoteService.execute(jobContext,resultT);
            }
        });


    }
}
