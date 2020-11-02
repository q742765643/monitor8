package com.piesat.enums;

public enum MonitorTypeEnum {
    CPU_USAGE("CPU使用率%",1),
    MEMORY_USAGE("内存使用率%",2),
    DISK_USAGE("磁盘使用率 %",3),
    FILE_REACH("文件到达 %",4),
    PRCESS("进程cpu时间变化次数",5),
    PING("ping丢包率",6);
    private String title;
    private Integer value;
    private MonitorTypeEnum(String title,Integer value) {
        this.title = title;
        this.value = value;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static MonitorTypeEnum match(Integer value) {
        if (value != null) {
            for (MonitorTypeEnum item:MonitorTypeEnum.values()) {
                if (item.getValue().equals(value)) {
                    return item;
                }
            }
        }
        return null;
    }
}
