package com.piesat.skywalking.enums;


import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.skywalking.schedule.service.folder.FileLocalService;
import com.piesat.skywalking.schedule.service.folder.FileSmaService;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;

/**
 * @program: SyncMaster
 * @description:
 * @author: zzj
 * @create: 2018-12-25 16:25
 **/
public enum FileEnum {

    LOCAL(0, SpringUtil.getBean(FileLocalService.class)),
    //DRDS备份逻辑
    //ALI_DRDS("stdb", ""),
    //GBASE备份逻辑
    REMOTE(1, SpringUtil.getBean(FileSmaService.class));
    private int title;
    private FileBaseService bean;

    FileEnum(int title, FileBaseService bean) {
        this.title = title;
        this.bean = bean;
    }

    public static FileEnum match(int name) {

        for (FileEnum item : FileEnum.values()) {
            if (item.title == name) {
                return item;
            }
        }
        return null;
    }

    public int getTitle() {
        return title;
    }

    public FileBaseService getBean() {
        return bean;
    }
}

