package com.piesat.util;

/**
 * @program: SyncMaster
 * @description:返回工具类
 * @author: zzj
 * @create: 2018-12-18 14:45
 **/
public enum ReturnCodeEnum {
    //成功
    SUCCESS(200, "成功"),
    //失败
    FIAL(1, " 失败"),

    /**
     * ==========101 100===============
     **/
    ReturnCodeEnum_401_ERROR(401, "未登陆或者未授权"),
    ReturnCodeEnum_402_ERROR(402, "账户被冻结"),
    ReturnCodeEnum_403_ERROR(403, "无访问权限"),
    ReturnCodeEnum_404_ERROR(404, "用户不存在"),
    ReturnCodeEnum_405_ERROR(405, "账户密码错误"),

    ReturnCodeEnum_501_ERROR(501, "GRPC调用无服务异常"),


    ReturnCodeEnum_13_ERROR(13, "执行sql异常"),
    ReturnCodeEnum_2_ERROR(2, "获取实现类异常"),
    ReturnCodeEnum_3_ERROR(3, "删除文件异常"),
    ReturnCodeEnum_4_ERROR(4, "移动文件异常"),
    ReturnCodeEnum_5_ERROR(5, "创建文件夹异常"),
    ReturnCodeEnum_6_ERROR(6, "执行mysqldump异常"),
    ReturnCodeEnum_7_ERROR(7, "OSS下载异常"),
    ReturnCodeEnum_8_ERROR(8, "未知错误"),
    ReturnCodeEnum_9_ERROR(9, "备份至OSS异常"),
    ReturnCodeEnum_10_ERROR(10, "执行select into file异常"),
    ReturnCodeEnum_11_ERROR(11, "迁移根目录错误"),
    ReturnCodeEnum_12_ERROR(12, "删除分区异常"),
    ReturnCodeEnum_14_ERROR(14, "执行虚谷备份异常"),
    ReturnCodeEnum_15_ERROR(15, "执行GBASE备份异常"),

    ReturnCodeEnum_601_ERROR(601, "表结构管理存在异常,查询失败");






    private int key;
    private String value;

    ReturnCodeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(int key) {
        for (ReturnCodeEnum st : ReturnCodeEnum.values()) {
            if (key == st.key) {
                return st.value;
            }
        }
        return "";
    }
    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
