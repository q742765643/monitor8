package com.piesat.skywalking.schedule.service.remote;

import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.skywalking.api.remote.RemoteService;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.handler.base.BaseShardHandler;
import com.piesat.util.ResultT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class RemoteServiceImpl implements RemoteService {
    @Autowired
    private ExecutorService executorService;

    /* private ExecutorService executorService= new ThreadPoolExecutor(800, 800,
             0L, TimeUnit.MILLISECONDS,
             new LinkedBlockingQueue<Runnable>(5000), new ThreadFactoryBuilder().setNameFormat("receive-log-%d").build(), new ThreadPoolExecutor.AbortPolicy());
 */
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT) {
        BaseShardHandler baseShardHandler = (BaseShardHandler) SpringUtil.getBean(jobContext.getHandler());
        return baseShardHandler.sharding(jobContext, resultT);
    }

    public void execute(JobContext jobContext, ResultT<String> resultT) {
        executorService.execute(() -> {
            BaseHandler baseHandler = (BaseHandler) SpringUtil.getBean(jobContext.getHandler());
            baseHandler.execute(jobContext, resultT);
        });
    }
}
