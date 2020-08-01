package com.piesat.enums;

public enum MonitorTypeEnum {
    CPU_USAGE("CPU使用率%"),
    MEMORY_USAGE("内存使用率%"),
    DISK_USAGE("磁盘使用率 %"),
    FILE_REACH("文件到达 %"),
    PRCESS("进程cpu时间变化次数"),
    PING("ping耗时时间");
    private String title;
    private MonitorTypeEnum(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public static MonitorTypeEnum match(String name) {
        if (name != null) {
            for (MonitorTypeEnum item:MonitorTypeEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return null;
    }
}
