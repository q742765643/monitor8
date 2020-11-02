package com.piesat.skywalking.service.trigger;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.remote.RemoteService;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class TriggerService {
    @GrpcHthtClient
    private RemoteService remoteService;
    @Autowired
    private ExecutorService executorTriggerService;
    /*private ExecutorService executorService= new ThreadPoolExecutor(800, 800,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("trigger-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());*/

    public void trigger(HtJobInfoDto jobInfoDto) {
        executorTriggerService.execute(() -> {
            ResultT<String> resultT = new ResultT<>();
            JobContext jobContext = new JobContext();
            long triggerLastTime = jobInfoDto.getTriggerLastTime();
            if (triggerLastTime == 0) {
                triggerLastTime = jobInfoDto.getTriggerNextTime();
            }
            jobInfoDto.setTriggerLastTime(triggerLastTime - jobInfoDto.getDelayTime());
            jobContext.setHandler(jobInfoDto.getJobHandler());
            jobContext.setHtJobInfoDto(jobInfoDto);
            if (jobInfoDto.getTriggerType() == null) {
                jobInfoDto.setTriggerType(0);
            }
            if (1 == jobInfoDto.getTriggerType()) {
                List<?> list = remoteService.sharding(jobContext, resultT);
                if (list == null || list.size() == 0) {
                    return;
                }
                double batch = new BigDecimal(list.size()).divide(new BigDecimal(3), 2, RoundingMode.HALF_UP).doubleValue();
                int slice = (int) Math.ceil(batch);
                int start = 0;
                int end = 0;
                for (int i = 0; i < 3; i++) {
                    start = slice * i;
                    end = slice * (i + 1);
                    if (end > list.size()) {
                        end = list.size();
                    }
                    jobContext.setLists(list.subList(start, end));
                    remoteService.execute(jobContext, resultT);
                    if (end == list.size()) {
                        break;
                    }

                }
            } else {
                remoteService.execute(jobContext, resultT);
            }
        });


    }
}
