package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.service.timing.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FileMonitorQuartzService extends ScheduleService {
    @Autowired
    private FileMonitorService fileMonitorService;

    public void initJob() {
        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        List<FileMonitorDto> fileMonitorDtos=fileMonitorService.selectBySpecification(fileMonitorDto);
        if(null!=fileMonitorDtos&&!fileMonitorDtos.isEmpty()){
            for(FileMonitorDto o:fileMonitorDtos){
                this.handleJob(o);
            }
        }
    }
}
