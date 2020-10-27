package com.piesat.skywalking.schedule.service.folder;


import com.piesat.common.grpc.config.SpringUtil;

/**
 * @program: SyncMaster
 * @description:
 * @author: zzj
 * @create: 2018-12-25 16:25
 **/
public enum BusinessEnum {

    LOCAL(0, SpringUtil.getBean(FileLocalService.class)),
    //DRDS备份逻辑
    //ALI_DRDS("stdb", ""),
    //GBASE备份逻辑
    REMOTE(1, SpringUtil.getBean(FileSmaService.class));
    private int title;
    private FileBaseService bean;
    BusinessEnum(int title, FileBaseService bean) {
        this.title=title;
        this.bean=bean;
    }
    public static BusinessEnum match(int name) {

        for (BusinessEnum item : BusinessEnum.values()) {
            if(item.title==name){
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

