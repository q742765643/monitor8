package com.piesat.enums;

public enum  MonitorConditionEnum {
    gt(">"),
    gte(">="),
    lt("<"),
    lte("<="),
    eq("=="),
    noteq("!="),
    none("none");

    private String title;
    private MonitorConditionEnum(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public static MonitorConditionEnum match(String name) {
        if (name != null) {
            for (MonitorConditionEnum item:MonitorConditionEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return null;
    }
}
