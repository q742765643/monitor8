package com.piesat.skywalking.schedule.service.folder;

import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import com.piesat.skywalking.api.folder.FileWatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Slf4j
@Service
public class FileWatchServiceImpl implements FileWatchService {

    public void start(String path) {
        SimpleWatcher watcher = new SimpleWatcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                log.info("创建：{}-> {}", currentPath, obj);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                log.info("删除：{}-> {}", currentPath, obj);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                log.info("Overflow：{}-> {}", currentPath, obj);
            }
        };
        WatchMonitor monitor = WatchMonitor.createAll(path,
                watcher);
        //monitor.start();
        log.info("启动");

    }
}
