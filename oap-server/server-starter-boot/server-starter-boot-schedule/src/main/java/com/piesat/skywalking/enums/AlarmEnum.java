package com.piesat.skywalking.enums;


import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.skywalking.schedule.service.alarm.*;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;

/**
 * @program: SyncMaster
 * @description:
 * @author: zzj
 * @create: 2018-12-25 16:25
 **/
public enum AlarmEnum {

    CPU(1, SpringUtil.getBean(AlarmCpuService.class)),
    DISK(2, SpringUtil.getBean(AlarmDiskService.class)),
    MEMORY(3, SpringUtil.getBean(AlarmMemoryService.class)),
    PROCESS(4, SpringUtil.getBean(AlarmProcessService.class)),
    FILE(5, SpringUtil.getBean(AlarmFileService.class)),
    PING(6, SpringUtil.getBean(AlarmPingService.class));
    private int title;
    private AlarmBaseService bean;

    AlarmEnum(int title, AlarmBaseService bean) {
        this.title = title;
        this.bean = bean;
    }

    public static AlarmEnum match(int name) {

        for (AlarmEnum item : AlarmEnum.values()) {
            if (item.title == name) {
                return item;
            }
        }
        return null;
    }

    public int getTitle() {
        return title;
    }

    public AlarmBaseService getBean() {
        return bean;
    }
}

