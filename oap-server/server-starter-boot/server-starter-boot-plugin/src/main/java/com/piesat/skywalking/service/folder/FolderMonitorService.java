package com.piesat.skywalking.service.folder;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.folder.FileWatchService;
import org.springframework.stereotype.Service;

@Service
public class FolderMonitorService {
    @GrpcHthtClient
    private FileWatchService fileWatchService;

    public void start(String path){
        fileWatchService.start(path);
    }
}
